package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * AppSettings - User ki pasandeeda settings ko mehfooz karne ke liye
 * Language, Voice Speed, Theme Mode aur API Key yahan manage hoti hain.
 */
class AppSettings(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("nayab_ai_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LANGUAGE = "key_language" // "sd", "ur", "en"
        const val KEY_VOICE_SPEED = "key_voice_speed" // float 0.5 to 2.0
        const val KEY_VOICE_PITCH = "key_voice_pitch" // float 0.5 to 2.0
        const val KEY_THEME_MODE = "key_theme_mode" // "system", "dark", "light"
        const val KEY_CUSTOM_API_KEY = "key_custom_api_key"
        const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        const val KEY_USER_EMAIL = "key_user_email"
        const val KEY_USER_NAME = "key_user_name"
        const val KEY_USER_PHOTO = "key_user_photo"
        const val KEY_CUSTOM_BANNER_URI = "key_custom_banner_uri"
    }

    // User Authentication States
    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userPhoto: String
        get() = prefs.getString(KEY_USER_PHOTO, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_PHOTO, value).apply()

    var customBannerUri: String
        get() = prefs.getString(KEY_CUSTOM_BANNER_URI, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_BANNER_URI, value).apply()

    // Selected Language (Default: Sindhi 'sd')
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "sd") ?: "sd"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    // Voice Speed (Default: 0.95f for clearer Sindhi phonetics)
    var voiceSpeed: Float
        get() = prefs.getFloat(KEY_VOICE_SPEED, 0.95f)
        set(value) = prefs.edit().putFloat(KEY_VOICE_SPEED, value).apply()

    // Voice Pitch (Default: 1.0f)
    var voicePitch: Float
        get() = prefs.getFloat(KEY_VOICE_PITCH, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_VOICE_PITCH, value).apply()

    // Theme Mode (Default: "system")
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    // Custom API Key (Agar user settings mein apna key enter kare)
    var customApiKey: String
        get() = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_API_KEY, value.trim()).apply()
}
