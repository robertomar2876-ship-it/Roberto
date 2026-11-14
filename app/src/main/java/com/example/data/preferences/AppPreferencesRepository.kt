package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "psico_prefs")

data class AccessibilitySettings(
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val darkThemePreference: Boolean? = null // null = seguir sistema
)

data class StorageLimitsConfig(
    val maxVideoMb: Int = 250,
    val maxPhotoMb: Int = 15,
    val maxDocumentMb: Int = 25
)

data class AdminAuthState(
    val isConfigured: Boolean = false,
    val adminDisplayName: String = "Roberto Martínez Paternina"
)

class AppPreferencesRepository(private val context: Context) {

    private object Keys {
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val DARK_THEME = stringPreferencesKey("dark_theme_mode") // "system", "light", "dark"

        val MAX_VIDEO_MB = intPreferencesKey("max_video_mb")
        val MAX_PHOTO_MB = intPreferencesKey("max_photo_mb")
        val MAX_DOC_MB = intPreferencesKey("max_doc_mb")

        val ADMIN_SALT = stringPreferencesKey("admin_salt")
        val ADMIN_HASH = stringPreferencesKey("admin_hash")
        val ADMIN_NAME = stringPreferencesKey("admin_name")
    }

    val accessibilitySettings: Flow<AccessibilitySettings> = context.dataStore.data.map { prefs ->
        val darkMode = when (prefs[Keys.DARK_THEME]) {
            "light" -> false
            "dark" -> true
            else -> null
        }
        AccessibilitySettings(
            fontScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            highContrast = prefs[Keys.HIGH_CONTRAST] ?: false,
            darkThemePreference = darkMode
        )
    }

    val storageLimits: Flow<StorageLimitsConfig> = context.dataStore.data.map { prefs ->
        StorageLimitsConfig(
            maxVideoMb = prefs[Keys.MAX_VIDEO_MB] ?: 250,
            maxPhotoMb = prefs[Keys.MAX_PHOTO_MB] ?: 15,
            maxDocumentMb = prefs[Keys.MAX_DOC_MB] ?: 25
        )
    }

    val adminAuthState: Flow<AdminAuthState> = context.dataStore.data.map { prefs ->
        val configured = !prefs[Keys.ADMIN_HASH].isNullOrBlank() && !prefs[Keys.ADMIN_SALT].isNullOrBlank()
        AdminAuthState(
            isConfigured = configured,
            adminDisplayName = prefs[Keys.ADMIN_NAME] ?: "Roberto Martínez Paternina"
        )
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.9f, 1.35f) }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }

    suspend fun setDarkThemeMode(mode: String) {
        context.dataStore.edit { it[Keys.DARK_THEME] = mode }
    }

    suspend fun updateStorageLimits(videoMb: Int, photoMb: Int, docMb: Int) {
        context.dataStore.edit {
            it[Keys.MAX_VIDEO_MB] = videoMb.coerceIn(10, 2048)
            it[Keys.MAX_PHOTO_MB] = photoMb.coerceIn(1, 100)
            it[Keys.MAX_DOC_MB] = docMb.coerceIn(1, 200)
        }
    }

    suspend fun configureAdminAccess(displayName: String, password: String) {
        require(password.length >= 6) { "La clave debe tener al menos 6 caracteres." }
        val saltBytes = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val salt = Base64.getEncoder().encodeToString(saltBytes)
        val hash = hashPassword(password, salt)
        context.dataStore.edit {
            it[Keys.ADMIN_NAME] = displayName.trim().ifEmpty { "Roberto Martínez Paternina" }
            it[Keys.ADMIN_SALT] = salt
            it[Keys.ADMIN_HASH] = hash
        }
    }

    suspend fun verifyAdminPassword(password: String): Boolean {
        val prefs = context.dataStore.data.first()
        val salt = prefs[Keys.ADMIN_SALT] ?: return false
        val expectedHash = prefs[Keys.ADMIN_HASH] ?: return false
        return hashPassword(password, salt) == expectedHash
    }

    private fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        var current = "$salt:$password".toByteArray(Charsets.UTF_8)
        repeat(5000) {
            current = digest.digest(current)
        }
        return Base64.getEncoder().encodeToString(current)
    }
}
