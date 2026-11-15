package com.kirill.physiognomy.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kirill.physiognomy.data.repository.ProfileRepository
import com.kirill.physiognomy.domain.model.ResearchProfile
import java.util.UUID

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    fun createAndSaveProfile(name: String, age: Int, gender: String, mode: String): ResearchProfile {
        val newProfile = ResearchProfile(
            id = UUID.randomUUID().toString(), // ID генерируется здесь
            name = name,
            age = age,
            gender = gender,
            mode = mode,
            answers = emptyMap()
        )
        repository.saveProfile(newProfile)
        return newProfile // Возвращаем его
    }
}

// Фабрика для создания ViewModel с передачей контекста
class ProfileViewModelFactory(private val repository: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}