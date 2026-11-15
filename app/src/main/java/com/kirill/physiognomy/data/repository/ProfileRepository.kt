package com.kirill.physiognomy.data.repository

import com.kirill.physiognomy.domain.model.ResearchProfile

interface ProfileRepository {
    fun saveProfile(profile: ResearchProfile)
    fun getAllProfiles(): List<ResearchProfile>
    fun getProfile(id: String): ResearchProfile?
    fun deleteProfile(id: String)
}