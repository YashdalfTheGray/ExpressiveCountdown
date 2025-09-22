package com.yashdalfthegray.expressivecountdown

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max

fun daysLeft(
    clock: java.time.Clock,
    targetDate: LocalDate,
    clampToZero: Boolean = true
): Long {
    val today = LocalDate.now(clock)
    val daysLeft = ChronoUnit.DAYS.between(today, targetDate)
    return if (clampToZero) max(daysLeft, 0) else daysLeft
}