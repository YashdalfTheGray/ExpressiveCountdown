package com.yashdalfthegray.expressivecountdown

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*
import kotlinx.serialization.json.Json

class SerializableColorSchemeTest {

    @Test
    fun colorScheme_serializeAndDeserialize_preservesColors() {
        val originalScheme = lightColorScheme(
            primary = Color.Red,
            secondary = Color.Blue,
            background = Color.White
        )

        val serialized = originalScheme.toSerializable()
        val deserialized = serialized.toColorScheme()

        assertEquals(originalScheme.primary, deserialized.primary)
        assertEquals(originalScheme.secondary, deserialized.secondary)
        assertEquals(originalScheme.background, deserialized.background)
    }

    @Test
    fun storedCustomTheme_jsonSerialization_handlesInvalidJson() {
        val validTheme = generateThemeFromSeedColor(Color.Red)
        val validJson = Json.encodeToString(validTheme)

        val decoded = Json.decodeFromString<StoredCustomTheme>(validJson)
        assertNotNull(decoded)

        assertThrows(Exception::class.java) {
            Json.decodeFromString<StoredCustomTheme>("\"invalid json\"")
        }
    }

    @Test
    fun generateThemeFromSeedColor_createsValidTheme() {
        val seedColor = Color.Red
        val theme = generateThemeFromSeedColor(seedColor)

        assertNotNull(theme.light)
        assertNotNull(theme.dark)
        assertNotEquals(theme.light.primary, 0)
        assertNotEquals(theme.dark.primary, 0)
    }
}