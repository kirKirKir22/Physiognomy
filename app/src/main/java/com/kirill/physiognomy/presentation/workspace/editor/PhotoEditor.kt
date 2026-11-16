package com.kirill.physiognomy.presentation.workspace.editor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kirill.physiognomy.domain.model.AppConstants
import com.kirill.physiognomy.domain.model.PhotoItem
import kotlin.math.abs

@Composable
fun PhotoEditor(
    photos: List<PhotoItem>,
    currentIndex: Int,
    effectsEnabled: Boolean,
    photosRemaining: Int,
    onPickPhotos: (List<Uri>) -> Unit,
    onSelectPhoto: (Int) -> Unit,
    onBrightness: (Float) -> Unit,
    onContrast: (Float) -> Unit,
    onRotation: (Float) -> Unit, // <-- ДОБАВЛЕНО
    onCommitEdits: () -> Unit,
    onToggleGrayscale: () -> Unit,
    onToggleMirror: () -> Unit,
    onResetPhoto: () -> Unit,
    onDeletePhoto: () -> Unit,
    onToggleEffects: () -> Unit,
    onLimitReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val photo = photos.getOrNull(currentIndex)
    var confirmDelete by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(AppConstants.MAX_PHOTOS),
    ) { uris -> onPickPhotos(uris) }

    val canPick = photosRemaining > 0
    val launchPicker = {
        if (canPick) {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            onLimitReached()
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0B0B10))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    RoundedCornerShape(22.dp)
                ),
        ) {
            if (photo == null) {
                EmptyPhotoState(onPick = { launchPicker() })
            } else {
                PhotoCanvas(
                    photo = photo,
                    effectsEnabled = effectsEnabled,
                    photosCount = photos.size,
                    currentIndex = currentIndex,
                    onSelectPhoto = onSelectPhoto,
                    onRotation = onRotation, // <-- ПЕРЕДАЕМ
                    modifier = Modifier.fillMaxSize(),
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                ) {
                    Text(
                        text = "${currentIndex + 1} / ${photos.size}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                    )
                }

                IconButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.DeleteOutline,
                        contentDescription = "Удалить",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onToggleEffects,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        if (effectsEnabled) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Эффекты",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    EditorTool(
                        icon = Icons.Filled.FilterBAndW,
                        label = "Ч/Б",
                        active = photo.grayscale,
                        onClick = onToggleGrayscale
                    )
                    EditorTool(
                        icon = Icons.Filled.Flip,
                        label = "Зеркало",
                        active = photo.mirrored,
                        onClick = onToggleMirror
                    )
                    EditorTool(
                        icon = Icons.Filled.RestartAlt,
                        label = "Сброс",
                        onClick = onResetPhoto
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(end = 8.dp, top = 56.dp, bottom = 62.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    VerticalSlider(
                        icon = Icons.Filled.BrightnessMedium,
                        label = "Яркость",
                        value = photo.brightness,
                        range = -1f..1f,
                        onValueChange = onBrightness,
                        onValueChangeFinished = onCommitEdits,
                        modifier = Modifier.weight(1f),
                    )
                    VerticalSlider(
                        icon = Icons.Filled.Contrast,
                        label = "Контраст",
                        value = photo.contrast,
                        range = 0.4f..2.2f,
                        onValueChange = onContrast,
                        onValueChangeFinished = onCommitEdits,
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    EditorBarButton(
                        icon = Icons.Filled.AddPhotoAlternate,
                        label = if (photosRemaining > 0) "Загрузить фото" else "Лимит",
                        onClick = { launchPicker() },
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Удалить фото?") },
            text = { Text("Снимок будет удалён без возможности восстановления.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; onDeletePhoto() }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Отмена") }
            },
        )
    }
}

@Composable
private fun EditorTool(
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (active) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.45f),
        contentColor = if (active) MaterialTheme.colorScheme.onPrimary else Color.White.copy(alpha = 0.85f),
        border = BorderStroke(
            1.dp,
            if (active) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.12f)
        ),
    ) {
        Column(
            modifier = Modifier
                .width(64.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun VerticalSlider(
    icon: ImageVector,
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fraction = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)
    val changeValue by rememberUpdatedState(onValueChange)
    val changeFinished by rememberUpdatedState(onValueChangeFinished)

    // ВАЖНО: выносим цвет из Composable-контекста
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(12.dp), color = Color.Black.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)), modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .width(48.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(range) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            fun update(y: Float) {
                                val h = size.height.toFloat()
                                if (h > 0) changeValue(
                                    range.start + (1f - (y / h).coerceIn(0f, 1f)) * (range.endInclusive - range.start)
                                )
                            }
                            update(down.position.y)
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.pressed } ?: break
                                update(change.position.y)
                                change.consume()
                            }
                            changeFinished()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val x = size.width / 2f
                    val top = 6.dp.toPx()
                    val bottom = size.height - 6.dp.toPx()

                    // Используем вынесенный primaryColor
                    drawLine(
                        Color.White.copy(alpha = 0.2f),
                        Offset(x, top),
                        Offset(x, bottom),
                        3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    val knobY = bottom - fraction * (bottom - top)
                    drawLine(
                        primaryColor,
                        Offset(x, knobY),
                        Offset(x, bottom),
                        3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(Color.White, 5.dp.toPx(), Offset(x, knobY))
                }
            }
        }
    }
}

@Composable
private fun EditorBarButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
        }
    }
}

@Composable
private fun PhotoCanvas(
    photo: PhotoItem,
    effectsEnabled: Boolean,
    photosCount: Int,
    currentIndex: Int,
    onSelectPhoto: (Int) -> Unit,
    onRotation: (Float) -> Unit, // <-- ДОБАВЛЕНО
    modifier: Modifier = Modifier
) {
    var scale by remember(photo.path) { mutableFloatStateOf(1f) }
    var offset by remember(photo.path) { mutableStateOf(Offset.Zero) }
    val swipeThreshold = with(LocalDensity.current) { 26.dp.toPx() }
    val colorFilter = if (!effectsEnabled) null else photo.toColorFilter()

    // Локальный угол для плавности, но синхронизируем с моделью при завершении жеста
    var currentRotation by remember(photo.path) { mutableFloatStateOf(photo.rotation) }

    Box(modifier = modifier.pointerInput(photo.path, photosCount) {
        var lastSwipeAt = 0L
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            var zoomed = scale > 1.01f
            var accumulatedX = 0f
            do {
                val event = awaitPointerEvent()
                val zoom = event.calculateZoom()
                val pan = event.calculatePan()
                val rotationDelta = event.calculateRotation()

                if (event.changes.count { it.pressed } >= 2) {
                    zoomed = true
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset += pan
                    currentRotation += rotationDelta // <-- МЕНЯЕМ ЛОКАЛЬНУЮ ПЕРЕМЕННУЮ
                    event.changes.forEach { if (it.pressed) it.consume() }
                } else if (event.changes.count { it.pressed } == 1) {
                    val change = event.changes.first { it.pressed }
                    val delta = change.positionChange()
                    if (zoomed) {
                        offset += delta; change.consume()
                    } else {
                        accumulatedX += delta.x
                        if (abs(accumulatedX) > swipeThreshold) {
                            val now = System.currentTimeMillis()
                            if (now - lastSwipeAt > 180) {
                                lastSwipeAt = now
                                val target =
                                    if (accumulatedX > 0) currentIndex - 1 else currentIndex + 1
                                if (target in 0 until photosCount) onSelectPhoto(target)
                            }
                            change.consume()
                        }
                    }
                }
            } while (event.changes.any { it.pressed })

            // Когда палец отпущен, сохраняем угол в модель
            onRotation(currentRotation)
        }
    }) {
        AsyncImage(
            model = photo.path,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = colorFilter,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale * if (photo.mirrored && effectsEnabled) -1f else 1f,
                    scaleY = scale,
                    rotationZ = currentRotation, // <-- ИСПОЛЬЗУЕМ ЛОКАЛЬНЫЙ УГОЛ
                    translationX = offset.x,
                    translationY = offset.y,
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color(0xFFB9A9FF).copy(alpha = 0.8f),
                start = Offset(size.width / 2f, 0f),
                end = Offset(size.width / 2f, size.height),
                strokeWidth = 1.4.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
            )
        }
    }
}

private fun PhotoItem.toColorFilter(): ColorFilter {
    val b = brightness * 255f
    val c = contrast
    val shift = 128f * (1f - c) + b
    val values = if (grayscale) {
        val lr = 0.213f * c; val lg = 0.715f * c; val lb = 0.072f * c
        floatArrayOf(lr, lg, lb, 0f, shift, lr, lg, lb, 0f, shift, lr, lg, lb, 0f, shift, 0f, 0f, 0f, 1f, 0f)
    } else {
        floatArrayOf(c, 0f, 0f, 0f, shift, 0f, c, 0f, 0f, shift, 0f, 0f, c, 0f, shift, 0f, 0f, 0f, 1f, 0f)
    }
    return ColorFilter.colorMatrix(ColorMatrix(values))
}

@Composable
private fun EmptyPhotoState(onPick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Filled.Image,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.35f),
            modifier = Modifier.size(52.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Добавьте фото лица",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onPick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.14f))
        ) {
            Text("Загрузить фото", color = Color.White)
        }
    }
}