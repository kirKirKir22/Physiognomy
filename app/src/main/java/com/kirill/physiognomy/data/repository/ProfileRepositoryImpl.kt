package com.kirill.physiognomy.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.kirill.physiognomy.domain.model.ResearchProfile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProfileRepositoryImpl(context: Context) : ProfileRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("physiognomy_profiles", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    override fun saveProfile(profile: ResearchProfile) {
        val currentProfiles = getAllProfiles().toMutableList()

        // Если профиль с таким ID уже есть — удаляем старый
        currentProfiles.removeAll { it.id == profile.id }

        // Добавляем новый
        currentProfiles.add(profile)

        // Сохраняем обратно в JSON
        val jsonString = json.encodeToString(currentProfiles)
        prefs.edit().putString("all_profiles", jsonString).apply()
    }

    override fun getAllProfiles(): List<ResearchProfile> {
        val jsonString = prefs.getString("all_profiles", null) ?: return emptyList()
        return try {
            json.decodeFromString<List<ResearchProfile>>(jsonString)
        } catch (e: Exception) {
            emptyList() // Если JSON битый — возвращаем пустой список
        }
    }

    override fun getProfile(id: String): ResearchProfile? {
        return getAllProfiles().find { it.id == id }
    }

    override fun deleteProfile(id: String) {
        val currentProfiles = getAllProfiles().toMutableList()
        currentProfiles.removeAll { it.id == id }
        val jsonString = json.encodeToString(currentProfiles)
        prefs.edit().putString("all_profiles", jsonString).apply()
    }
}