package com.yashdalfthegray.expressivecountdown

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CountdownTest {

    private fun fixedClock(isoInstant: String, zone: ZoneId = ZoneId.of("UTC")): Clock {
        return Clock.fixed(Instant.parse(isoInstant), zone)
    }

    @Test
    fun daysLeft_today_isZero() {
        val clock = fixedClock("2025-09-22T10:00:00Z")
        val today = LocalDate.now(clock)
        val result = daysLeft(clock, today)
        assertEquals(0L, result)
    }

    @Test
    fun daysLeft_tomorrow_isOne() {
        val clock = fixedClock("2025-09-22T10:00:00Z")
        val tomorrow = LocalDate.now(clock).plusDays(1)
        val result = daysLeft(clock, tomorrow)
        assertEquals(1L, result)
    }

    @Test
    fun daysLeft_pastDate_isZeroWhenClamped() {
        val clock = fixedClock("2025-09-22T10:00:00Z")
        val past = LocalDate.now(clock).minusDays(3)
        val result = daysLeft(clock, past) // clampToZero=true by default
        assertEquals(0L, result)
    }

    @Test
    fun daysLeft_pastDate_negativeWhenNotClamped() {
        val clock = fixedClock("2025-09-22T10:00:00Z")
        val past = LocalDate.now(clock).minusDays(3)
        val result = daysLeft(clock, past, clampToZero = false)
        assertEquals(-3L, result)
    }

    @Test
    fun daysLeft_oneYearFromNow_correctCount() {
        val clock = fixedClock("2025-09-22T10:00:00Z")
        val oneYearLater = LocalDate.now(clock).plusYears(1)
        val result = daysLeft(clock, oneYearLater)
        assertEquals(365L, result) // 2025 is not a leap year
    }

    @Test
    fun daysLeft_acrossTimeZones_consistent() {
        val utcClock = fixedClock("2025-09-22T23:30:00Z", ZoneId.of("UTC"))
        val pstClock = fixedClock("2025-09-22T23:30:00Z", ZoneId.of("America/Los_Angeles"))
        val tomorrow = LocalDate.of(2025, 9, 23)

        val utcResult = daysLeft(utcClock, tomorrow)
        val pstResult = daysLeft(pstClock, tomorrow)

        assertEquals(utcResult, pstResult)
    }

    @Test
    fun daysLeft_endOfMonth_handlesCorrectly() {
        val clock = fixedClock("2025-01-31T10:00:00Z")
        val nextMonth = LocalDate.of(2025, 2, 28)
        val result = daysLeft(clock, nextMonth)
        assertEquals(28L, result)
    }
}