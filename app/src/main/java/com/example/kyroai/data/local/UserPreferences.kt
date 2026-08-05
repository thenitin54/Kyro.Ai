package com.example.kyroai.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.kyroai.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kyro_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(prefs.getString("theme", "dark") ?: "dark")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _useDemoMode = MutableStateFlow(prefs.getBoolean("use_demo_mode", false))
    val useDemoMode: StateFlow<Boolean> = _useDemoMode.asStateFlow()

    private val _apiKey = MutableStateFlow(
        prefs.getString("api_key", null) ?: BuildConfig.GEMINI_API_KEY
    )
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(prefs.getString("selected_model", "gemini-3.5-flash") ?: "gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(prefs.getString("selected_language", "en") ?: "en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _systemPersona = MutableStateFlow(prefs.getString("system_persona", "You are Kyro AI, a helpful, precise, and intelligent AI assistant.") ?: "You are Kyro AI, a helpful, precise, and intelligent AI assistant.")
    val systemPersona: StateFlow<String> = _systemPersona.asStateFlow()

    private val _username = MutableStateFlow(prefs.getString("username", "User") ?: "User")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun setTheme(theme: String) {
        prefs.edit().putString("theme", theme).apply()
        _theme.value = theme
    }

    fun setUseDemoMode(demoMode: Boolean) {
        prefs.edit().putBoolean("use_demo_mode", demoMode).apply()
        _useDemoMode.value = demoMode
    }

    fun setApiKey(key: String) {
        prefs.edit().putString("api_key", key).apply()
        _apiKey.value = key
    }

    fun setSelectedModel(model: String) {
        prefs.edit().putString("selected_model", model).apply()
        _selectedModel.value = model
    }

    fun setSelectedLanguage(language: String) {
        prefs.edit().putString("selected_language", language).apply()
        _selectedLanguage.value = language
    }

    fun setSystemPersona(persona: String) {
        prefs.edit().putString("system_persona", persona).apply()
        _systemPersona.value = persona
    }

    fun setUsername(name: String) {
        prefs.edit().putString("username", name).apply()
        _username.value = name
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
        _isLoggedIn.value = loggedIn
    }
}
