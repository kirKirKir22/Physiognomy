package com.kirill.physiognomy.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onBackClick: () -> Unit,
    onStartAnalysis: (name: String, age: String, gender: String, mode: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("М") }
    var mode by remember { mutableStateOf("Blitz") }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создание профиля", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    // Для планшета ширина 600dp, для телефона — вся ширина
                    .widthIn(max = if (isTablet) 600.dp else Int.MAX_VALUE.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 16.dp,
                        // Для планшета добавляем отступ снизу, для телефона оставляем как есть
                        bottom = if (isTablet) 80.dp else 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Заполните профиль",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Эти данные нужны для контекста. " +
                            "В физиогномике пол и возраст влияют на трактовку некоторых черт.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Поле Имя (на планшете ограничиваем ширину)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    placeholder = { Text("Например, Александр") },
                    modifier = Modifier.fillMaxWidth(if (isTablet) 0.7f else 1f), // 70% на планшете
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Поле Возраст
                OutlinedTextField(
                    value = age,
                    onValueChange = { if (it.length <= 3) age = it.filter { char -> char.isDigit() } },
                    label = { Text("Возраст") },
                    placeholder = { Text("Например, 30") },
                    modifier = Modifier.fillMaxWidth(if (isTablet) 0.4f else 1f), // 40% на планшете
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Выбор пола
                Text("Пол", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(if (isTablet) 0.6f else 1f), // 60% на планшете
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderButton(
                        text = "Мужской",
                        selected = gender == "М",
                        onClick = { gender = "М" },
                        modifier = Modifier.weight(1f)
                    )
                    GenderButton(
                        text = "Женский",
                        selected = gender == "Ж",
                        onClick = { gender = "Ж" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Выбор режима
                Text("Режим исследования", style = MaterialTheme.typography.titleMedium)
                ModeCard(
                    title = "Блиц",
                    description = "7 ключевых черт. Займет около 2 минут.",
                    selected = mode == "Blitz",
                    onClick = { mode = "Blitz" }
                )
                ModeCard(
                    title = "Углубленный",
                    description = "Все 26 шагов. Полный анализ лица и характера.",
                    selected = mode == "Deep",
                    onClick = { mode = "Deep" }
                )

                // Кнопка теперь привязана к контенту, а не к низу экрана
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onStartAnalysis(name, age, gender, mode) },
                    enabled = name.isNotBlank() && age.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth(if (isTablet) 0.6f else 1f) // 60% ширины на планшете
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Перейти к анализу", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Вспомогательные функции GenderButton и ModeCard (без изменений)
@Composable
private fun GenderButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.height(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}