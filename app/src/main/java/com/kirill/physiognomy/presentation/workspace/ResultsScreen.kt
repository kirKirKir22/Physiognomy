package com.kirill.physiognomy.presentation.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kirill.physiognomy.domain.model.AnswerOption
import com.kirill.physiognomy.domain.model.PhysiognomyCatalog
import com.kirill.physiognomy.domain.model.Question
import com.kirill.physiognomy.domain.model.ResearchProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    profile: ResearchProfile,
    onBackClick: () -> Unit,
    onNewAnalysisClick: () -> Unit
) {
    // Собираем все пары (Вопрос, Ответ) для данного профиля
    val results: List<Pair<Question, AnswerOption>> = profile.answers.mapNotNull { (questionId, optionId) ->
        val question = PhysiognomyCatalog.questions.find { it.id == questionId }
        val option = question?.options?.find { it.id == optionId }
        if (question != null && option != null) question to option else null
    }.sortedBy { (q, _) ->
        // Сортируем в том порядке, в котором вопросы идут в каталоге
        PhysiognomyCatalog.questions.indexOf(q)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результаты анализа") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === ШАПКА С ИМЕНЕМ ===
            HeaderCard(profile = profile)

            // === ИТОГОВЫЙ ПОРТРЕТ ===
            PortraitCard(profile = profile, results = results)

            // === ДЕТАЛЬНЫЙ РАЗБОР ===
            if (results.isNotEmpty()) {
                Text(
                    text = "Детальный разбор",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
                results.forEachIndexed { index, (question, option) ->
                    ResultQuestionCard(
                        index = index + 1,
                        question = question,
                        option = option
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Вы ещё не ответили ни на один вопрос.",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // === КНОПКИ ДЕЙСТВИЙ ===
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNewAnalysisClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Новый анализ", style = MaterialTheme.typography.titleMedium)
            }
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Вернуться к профилю")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// === ШАПКА ===
@Composable
private fun HeaderCard(profile: ResearchProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${profile.age} лет • ${if (profile.gender == "М") "мужчина" else "женщина"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "Режим: ${if (profile.mode == "Blitz") "Блиц" else "Углублённый"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// === ИТОГОВЫЙ ПОРТРЕТ ===
@Composable
private fun PortraitCard(profile: ResearchProfile, results: List<Pair<Question, AnswerOption>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Психологический портрет",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (results.isEmpty()) {
                Text(
                    text = "Недостаточно данных для формирования портрета.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = buildPortraitText(profile, results),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// === КАРТОЧКА ВОПРОСА В ДЕТАЛЬНОМ РАЗБОРЕ ===
@Composable
private fun ResultQuestionCard(
    index: Int,
    question: Question,
    option: AnswerOption
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Номер + заголовок
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = question.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = question.subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Выбранный вариант
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Summarize,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Краткое описание
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Полное описание
            Text(
                text = option.fullDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// === ФОРМИРОВАНИЕ СВЯЗНОГО ТЕКСТА ПОРТРЕТА ===
private fun buildPortraitText(
    profile: ResearchProfile,
    results: List<Pair<Question, AnswerOption>>
): String = buildString {
    append("На основе анализа черт лица ")
    append(profile.name)
    append(" можно составить следующий психологический портрет.\n\n")

    results.forEachIndexed { index, (question, option) ->
        append("• ")
        append(question.title)
        append(" (")
        append(option.label.lowercase())
        append("): ")
        append(option.fullDescription)
        if (index < results.size - 1) append("\n\n")
    }

    append("\n\n")
    append("Это наблюдение, а не диагноз. Физиогномика — инструмент для размышления, а не истина в последней инстанции.")
}