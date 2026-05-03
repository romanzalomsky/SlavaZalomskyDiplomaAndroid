package com.zalomsky.sportscore.utils

import android.util.Base64
import com.zalomsky.sportscore.domain.models.RoleModel
import org.json.JSONObject

object JwtUtils {
    fun getRoleFromToken(token: String): RoleModel? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null
            
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)
            
            // Проверяем разные варианты ключей, которые может использовать бэкенд
            val roleString = when {
                jsonObject.has("role") -> jsonObject.getString("role")
                jsonObject.has("user_role") -> jsonObject.getString("user_role")
                jsonObject.has("userRole") -> jsonObject.getString("userRole")
                else -> return null
            }
            
            RoleModel.valueOf(roleString.uppercase())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
