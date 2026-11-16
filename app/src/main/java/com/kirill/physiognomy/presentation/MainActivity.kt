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
import com.kirill.physiognomy.presentation.workspace.WorkspaceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Позволяет контенту рисоваться под системными панелями (статус-бар, навбар)
        enableEdgeToEdge()

        // --- ДОБАВЛЕНО: Инициализация репозитория и фабрики ---
        val repository = ProfileRepositoryImpl(applicationContext)
        val viewModelFactory = ProfileViewModelFactory(repository)
        // -----------------------------------------------------

        setContent {
            // Используем нашу тему. Системная тема (темная/светлая) определится автоматически
            PhysiognomyTheme {

                // Добавляем состояние для переключения между экранами
                var currentScreen by remember { mutableStateOf("onboarding") }

                // --- ДОБАВЛЕНО: Состояние для текущего профиля ---
                var currentProfile by remember { mutableStateOf<ResearchProfile?>(null) }
                // -------------------------------------------------

                // --- ДОБАВЛЕНО: Получаем ViewModel ---
                val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
                // ------------------------------------

                when (currentScreen) {
                    "onboarding" -> {
                        // Вызываем наш экран приветствия
                        OnboardingScreen(
                            onStartClick = {
                                // Теперь по клику переходим на экран профиля
                                currentScreen = "profile"
                            })
                    }

                    "profile" -> {
                        // Вызываем экран создания профиля
                        ProfileSetupScreen(onBackClick = {
                            // Возврат назад на экран приветствия
                            currentScreen = "onboarding"
                        }, onStartAnalysis = { name, age, gender, mode ->
                            val ageInt = age.toIntOrNull() ?: 0

                            // --- ИЗМЕНЕНО: Получаем готовый профиль от ViewModel ---
                            // ViewModel сам создает ID, сохраняет в базу и возвращает объект
                            val newProfile = profileViewModel.createAndSaveProfile(
                                name, ageInt, gender, mode
                            )
                            currentProfile = newProfile
                            // ----------------------------------------------------------

                            // --- ДОБАВЛЕНО: Переход на чек-лист ---
                            currentScreen = "workspace"
                            // -------------------------------------
                        })
                    }
                    // --- ДОБАВЛЕНО: Ветка для экрана чек-листа ---
                    "workspace" -> {
                        currentProfile?.let { profile ->
                            WorkspaceScreen(
                                profile = profile,
                                onBackClick = { currentScreen = "profile" },
                                onContinueClick = {
                                    // Теперь мы идем на единый экран анализа
                                    currentScreen = "analysis"
                                }
                            )
                        }
                    }
                    // --- НОВАЯ ВЕТКА ---
                    "analysis" -> {
                        currentProfile?.let { profile ->
                            AnalysisScreen(
                                profile = profile,
                                onBackClick = { currentScreen = "workspace" }
                            )
                        }
                    }
                }
            }
        }
    }
}