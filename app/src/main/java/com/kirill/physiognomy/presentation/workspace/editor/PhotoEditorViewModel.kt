package com.kirill.physiognomy.presentation.workspace.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kirill.physiognomy.domain.model.PhotoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhotoEditorViewModel : ViewModel() {

    // Список фото
    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    // Индекс текущего выбранного фото
    private val _currentPhotoIndex = MutableStateFlow(0)
    val currentPhotoIndex: StateFlow<Int> = _currentPhotoIndex.asStateFlow()

    // Включены ли эффекты (для кнопки "глаз")
    private val _effectsEnabled = MutableStateFlow(true)
    val effectsEnabled: StateFlow<Boolean> = _effectsEnabled.asStateFlow()

    // Добавление новых фото из галереи
    fun addPhotos(uris: List<Uri>) {
        val newPhotos = uris.map { PhotoItem(path = it.toString()) }
        _photos.update { current -> current + newPhotos }
    }

    // Выбор фото по индексу (свайп)
    fun selectPhoto(index: Int) {
        if (index in _photos.value.indices) {
            _currentPhotoIndex.value = index
        }
    }

    // Включение/выключение эффектов (кнопка "Показать оригинал")
    fun toggleEffects() {
        _effectsEnabled.value = !_effectsEnabled.value
    }

    // Удаление текущего фото
    fun deleteCurrentPhoto() {
        val currentList = _photos.value.toMutableList()
        if (currentList.isNotEmpty()) {
            currentList.removeAt(_currentPhotoIndex.value)
            _photos.value = currentList
            if (_currentPhotoIndex.value >= currentList.size) {
                _currentPhotoIndex.value = (currentList.size - 1).coerceAtLeast(0)
            }
        }
    }

    // Переключение Ч/Б
    fun toggleGrayscale() {
        updateCurrentPhoto { it.copy(grayscale = !it.grayscale) }
    }

    // Переключение зеркала
    fun toggleMirror() {
        updateCurrentPhoto { it.copy(mirrored = !it.mirrored) }
    }

    // Яркость
    fun updateBrightness(value: Float) {
        updateCurrentPhoto { it.copy(brightness = value) }
    }

    // Контраст
    fun updateContrast(value: Float) {
        updateCurrentPhoto { it.copy(contrast = value) }
    }

    // Сброс эффектов
    fun resetPhoto() {
        updateCurrentPhoto {
            it.copy(
                brightness = 0f,
                contrast = 1f,
                grayscale = false,
                mirrored = false,
                rotation = 0f
            )
        }
    }

    // Вспомогательная функция для обновления текущего фото
    private fun updateCurrentPhoto(update: (PhotoItem) -> PhotoItem) {
        val currentList = _photos.value.toMutableList()
        val index = _currentPhotoIndex.value
        if (index in currentList.indices) {
            currentList[index] = update(currentList[index])
            _photos.value = currentList
        }
    }

    // Вращение
    fun updateRotation(value: Float) {
        updateCurrentPhoto { it.copy(rotation = value) }
    }

}