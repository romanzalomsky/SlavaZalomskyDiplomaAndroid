package com.zalomsky.sportscore.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun saveToken(token: String) {
        // Очищаем токен от кавычек и пробелов, которые могли прийти из JSON
        val cleanToken = token.trim().removeSurrounding("\"")
        sharedPreferences.edit().putString(KEY_TOKEN, cleanToken).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveRole(roleName: String) {
        sharedPreferences.edit().putString(KEY_USER_ROLE, roleName).apply()
    }

    fun getRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    fun clearPreferences() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ROLE)
            .apply()
    }
}
