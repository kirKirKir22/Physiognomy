package com.kirill.physiognomy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kirill.physiognomy.data.repository.ProfileRepositoryImpl
import com.kirill.physiognomy.domain.model.ResearchProfile
import com.kirill.physiognomy.presentation.onboarding.OnboardingScreen
import com.kirill.physiognomy.presentation.profile.ProfileSetupScreen
import com.kirill.physiognomy.presentation.profile.ProfileViewModel
import com.kirill.physiognomy.presentation.profile.ProfileViewModelFactory
import com.kirill.physiognomy.presentation.ui.theme.PhysiognomyTheme
import com.kirill.physiognomy.presentation.workspace.AnalysisScreen
import com.kirill.physiognomy.presentation.workspace.ResultsScreen
import com.kirill.physiognomy.presentation.workspace.WorkspaceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ProfileRepositoryImpl(applicationContext)
        val viewModelFactory = ProfileViewModelFactory(repository)

        setContent {
            PhysiognomyTheme {
                var currentScreen by remember { mutableStateOf("onboarding") }
                var currentProfile by remember { mutableStateOf<ResearchProfile?>(null) }

                val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)

                when (currentScreen) {
                    "onboarding" -> {
                        OnboardingScreen(
                            onStartClick = { currentScreen = "profile" }
                        )
                    }
                    "profile" -> {
                        ProfileSetupScreen(
                            onBackClick = { currentScreen = "onboarding" },
                            onStartAnalysis = { name, age, gender, mode ->
                                val ageInt = age.toIntOrNull() ?: 0
                                val newProfile = profileViewModel.createAndSaveProfile(
                                    name, ageInt, gender, mode
                                )
                                currentProfile = newProfile
                                currentScreen = "workspace"
                            }
                        )
                    }
                    "workspace" -> {
                        currentProfile?.let { profile ->
                            WorkspaceScreen(
                                profile = profile,
                                onBackClick = { currentScreen = "profile" },
                                onContinueClick = { currentScreen = "analysis" }
                            )
                        }
                    }
                    "analysis" -> {
                        currentProfile?.let { profile ->
                            AnalysisScreen(
                                profile = profile,
                                onBackClick = { currentScreen = "workspace" },
                                onFinishClick = { currentScreen = "results" }
                            )
                        }
                    }
                    "results" -> {
                        // ✅ Перечитываем СВЕЖИЙ профиль из репозитория —
                        // currentProfile хранит старый снапшот с пустыми answers
                        val freshProfile = currentProfile?.let { repository.getProfile(it.id) }
                        freshProfile?.let { profile ->
                            ResultsScreen(
                                profile = profile,
                                onBackClick = { currentScreen = "analysis" },
                                onNewAnalysisClick = {
                                    currentProfile = null
                                    currentScreen = "onboarding"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}