package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nova_settings")

data class UserSettings(
    val displayName: String = "Cosmic Explorer",
    val preferredModelLabel: String = "Gemini 3.8 Flash (Preview)",
    val responseCreativity: Float = 0.65f,
    val streamResponses: Boolean = true,
    val voiceAutoSpeak: Boolean = true,
    val hapticFeedback: Boolean = true,
    val darkCosmicMode: Boolean = true
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val MODEL_LABEL = stringPreferencesKey("model_label")
        val CREATIVITY = floatPreferencesKey("creativity")
        val STREAM_RESPONSES = booleanPreferencesKey("stream_responses")
        val VOICE_AUTO_SPEAK = booleanPreferencesKey("voice_auto_speak")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val DARK_COSMIC_MODE = booleanPreferencesKey("dark_cosmic_mode")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            displayName = prefs[Keys.DISPLAY_NAME] ?: "Cosmic Explorer",
            preferredModelLabel = prefs[Keys.MODEL_LABEL] ?: "Gemini 3.8 Flash (Preview)",
            responseCreativity = prefs[Keys.CREATIVITY] ?: 0.65f,
            streamResponses = prefs[Keys.STREAM_RESPONSES] ?: true,
            voiceAutoSpeak = prefs[Keys.VOICE_AUTO_SPEAK] ?: true,
            hapticFeedback = prefs[Keys.HAPTIC_FEEDBACK] ?: true,
            darkCosmicMode = prefs[Keys.DARK_COSMIC_MODE] ?: true
        )
    }

    suspend fun updateDisplayName(name: String) {
        context.dataStore.edit { it[Keys.DISPLAY_NAME] = name.trim().ifEmpty { "Cosmic Explorer" } }
    }

    suspend fun updateModelLabel(label: String) {
        context.dataStore.edit { it[Keys.MODEL_LABEL] = label }
    }

    suspend fun updateCreativity(value: Float) {
        context.dataStore.edit { it[Keys.CREATIVITY] = value.coerceIn(0f, 1f) }
    }

    suspend fun updateStreamResponses(enabled: Boolean) {
        context.dataStore.edit { it[Keys.STREAM_RESPONSES] = enabled }
    }

    suspend fun updateVoiceAutoSpeak(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VOICE_AUTO_SPEAK] = enabled }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTIC_FEEDBACK] = enabled }
    }

    suspend fun updateDarkCosmicMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_COSMIC_MODE] = enabled }
    }
}
