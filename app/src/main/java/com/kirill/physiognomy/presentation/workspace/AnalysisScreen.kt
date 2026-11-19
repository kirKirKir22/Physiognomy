package com.kirill.physiognomy.presentation.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kirill.physiognomy.domain.model.AppConstants
import com.kirill.physiognomy.domain.model.ResearchProfile
import com.kirill.physiognomy.presentation.workspace.editor.PhotoEditor
import com.kirill.physiognomy.presentation.workspace.editor.PhotoEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    profile: ResearchProfile,
    onBackClick: () -> Unit,
    photoEditorViewModel: PhotoEditorViewModel = viewModel()
) {
    // Инициализируем ViewModel строго внутри тела Composable
    val checklistViewModel: ChecklistViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChecklistViewModel(isBlitzMode = profile.mode == "Blitz") as T
            }
        }
    )

    val photos by photoEditorViewModel.photos.collectAsState()
    val currentIndex by photoEditorViewModel.currentPhotoIndex.collectAsState()
    val effectsEnabled by photoEditorViewModel.effectsEnabled.collectAsState()

    val currentQuestionIndex by checklistViewModel.currentQuestionIndex.collectAsState()
    val answers by checklistViewModel.answers.collectAsState()
    val currentQuestion = remember(currentQuestionIndex) {
        checklistViewModel.getCurrentQuestion()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Анализ (${if (profile.mode == "Blitz") "Блиц" else "Углубленный"})") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            PhotoEditor(
                photos = photos,
                currentIndex = currentIndex,
                effectsEnabled = effectsEnabled,
                photosRemaining = AppConstants.MAX_PHOTOS - photos.size,
                onPickPhotos = { uris -> photoEditorViewModel.addPhotos(uris) },
                onSelectPhoto = { index -> photoEditorViewModel.selectPhoto(index) },
                onBrightness = { value -> photoEditorViewModel.updateBrightness(value) },
                onContrast = { value -> photoEditorViewModel.updateContrast(value) },
                onCommitEdits = { },
                onToggleGrayscale = { photoEditorViewModel.toggleGrayscale() },
                onToggleMirror = { photoEditorViewModel.toggleMirror() },
                onResetPhoto = { photoEditorViewModel.resetPhoto() },
                onDeletePhoto = { photoEditorViewModel.deleteCurrentPhoto() },
                onToggleEffects = { photoEditorViewModel.toggleEffects() },
                onRotation = { angle -> photoEditorViewModel.updateRotation(angle) },
                onLimitReached = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (currentQuestion != null) {
                        Box(modifier = Modifier.weight(1f)) {
                            QuestionPanel(
                                question = currentQuestion,
                                selectedOptionId = answers[currentQuestion.id],
                                onOptionSelect = { optionId ->
                                    checklistViewModel.selectOption(currentQuestion.id, optionId)
                                }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { checklistViewModel.previousQuestion() },
                                enabled = currentQuestionIndex > 0,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Назад")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = {
                                    println("DEBUG: Нажата кнопка Далее. Текущий индекс: $currentQuestionIndex")
                                    checklistViewModel.nextQuestion()
                                },
                                // Безопасная проверка: если ответ для текущего вопроса есть в карте
                                enabled = answers[currentQuestion.id] != null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (checklistViewModel.isLastQuestion()) "Завершить" else "Далее")
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Вопросы не найдены. Проверьте режим (Blitz/Deep).",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}