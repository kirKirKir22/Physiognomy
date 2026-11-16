package com.kirill.physiognomy.presentation.workspace

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val photos by photoEditorViewModel.photos.collectAsState()
    val currentIndex by photoEditorViewModel.currentPhotoIndex.collectAsState()
    val effectsEnabled by photoEditorViewModel.effectsEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // Показываем режим в заголовке
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
            // Фото-редактор (45% высоты) — один для обоих режимов
            PhotoEditor(
                photos = photos,
                currentIndex = currentIndex,
                effectsEnabled = effectsEnabled,
                photosRemaining = AppConstants.MAX_PHOTOS - photos.size,
                onPickPhotos = { uris -> photoEditorViewModel.addPhotos(uris) },
                onSelectPhoto = { index -> photoEditorViewModel.selectPhoto(index) },
                onBrightness = { value -> photoEditorViewModel.updateBrightness(value) },
                onContrast = { value -> photoEditorViewModel.updateContrast(value) },
                onCommitEdits = { /* В будущем здесь будет сохранение, пока пусто */ },
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

            // Зона вопросов (55% высоты) — меняется в зависимости от режима
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (profile.mode == "Blitz") {
                            "Здесь будет Блиц-опрос (7 шагов)"
                        } else {
                            "Здесь будет Углубленный опрос (26 шагов)"
                        },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}