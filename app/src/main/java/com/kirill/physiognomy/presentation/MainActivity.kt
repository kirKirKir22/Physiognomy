package com.kirill.physiognomy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kirill.physiognomy.presentation.onboarding.OnboardingScreen
import com.kirill.physiognomy.presentation.profile.ProfileSetupScreen
import com.kirill.physiognomy.presentation.ui.theme.PhysiognomyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Позволяет контенту рисоваться под системными панелями (статус-бар, навбар)
        enableEdgeToEdge()

        setContent {
            // Используем нашу тему. Системная тема (темная/светлая) определится автоматически
            PhysiognomyTheme {

                // Добавляем состояние для переключения между экранами
                var currentScreen by remember { mutableStateOf("onboarding") }

                when (currentScreen) {
                    "onboarding" -> {
                        // Вызываем наш экран приветствия
                        OnboardingScreen(
                            onStartClick = {
                                // Теперь по клику переходим на экран профиля
                                currentScreen = "profile"
                            }
                        )
                    }
                    "profile" -> {
                        // Вызываем экран создания профиля (код которого я дал в прошлом сообщении)
                        ProfileSetupScreen(
                            onBackClick = {
                                // Возврат назад на экран приветствия
                                currentScreen = "onboarding"
                            },
                            onStartAnalysis = { name, age, gender, mode ->
                                // Здесь мы позже добавим сохранение в базу и переход к чек-листу.
                                // Пока просто выводим в консоль для проверки.
                                println("Создан профиль: Имя=$name, Возраст=$age, Пол=$gender, Режим=$mode")
                            }
                        )
                    }
                }
            }
        }
    }
}