package com.kirill.physiognomy.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kirill.physiognomy.presentation.onboarding.OnboardingScreen
import com.kirill.physiognomy.presentation.ui.theme.PhysiognomyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Позволяет контенту рисоваться под системными панелями (статус-бар, навбар)
        enableEdgeToEdge()

        setContent {
            // Используем нашу тему. Системная тема (темная/светлая) определится автоматически
            PhysiognomyTheme {
                // Вызываем наш экран приветствия
                OnboardingScreen(
                    onStartClick = {
                        // Пока здесь пусто. Сюда мы добавим навигацию на экран создания профиля
                        // на следующем шаге.
                    }
                )
            }
        }
    }
}