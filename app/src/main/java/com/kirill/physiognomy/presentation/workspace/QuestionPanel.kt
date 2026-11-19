package com.kirill.physiognomy.presentation.workspace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kirill.physiognomy.domain.model.AnswerOption
import com.kirill.physiognomy.domain.model.Question

@Composable
fun QuestionPanel(
    question: Question,
    selectedOptionId: String?,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Состояние для раскрытия описания самого вопроса
    var isQuestionExpanded by remember(question.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- ЗАГОЛОВОК ВОПРОСА ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isQuestionExpanded = !isQuestionExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = question.subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = if (isQuestionExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "Развернуть описание",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Сворачиваемое описание вопроса (тот самый текст про "Стиль мышления")
        AnimatedVisibility(visible = isQuestionExpanded) {
            Text(
                text = question.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- СПИСОК ВАРИАНТОВ ---
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEach { option ->
                val isSelected = option.id == selectedOptionId
                OptionCard(
                    option = option,
                    isSelected = isSelected,
                    onSelect = { onOptionSelect(option.id) }
                )
            }
        }
    }
}

@Composable
private fun OptionCard(
    option: AnswerOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // 2. Состояние для раскрытия описания конкретного варианта
    var isDetailsExpanded by remember { mutableStateOf(false) }

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
    ) {
        // ВЕРХНЯЯ ЧАСТЬ: Выбор варианта и кнопка "i"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onSelect) // Клик по тексту выбирает вариант
            )

            // Кнопка (i)
            IconButton(
                onClick = { isDetailsExpanded = !isDetailsExpanded },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Подробнее",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // НИЖНЯЯ ЧАСТЬ: Раскрывающийся блок (Картинка + Текст)
        AnimatedVisibility(visible = isDetailsExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // МЕСТО ДЛЯ КАРТИНКИ
                if (option.iconRes != null) {
                    // Если картинка есть, показываем её
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = option.iconRes),
                        contentDescription = option.label,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    )
                } else {
                    // Если картинки нет, показываем аккуратную заглушку
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Рисунок\nскоро",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ТЕКСТ ОПИСАНИЯ
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.description, // Краткая суть
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = option.fullDescription, // Глубокий текст
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}