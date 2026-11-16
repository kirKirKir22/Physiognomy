package com.kirill.physiognomy.presentation.workspace.editor

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PhotoEditorViewModelTest {

    private lateinit var viewModel: PhotoEditorViewModel

    @Before
    fun setUp() {
        viewModel = PhotoEditorViewModel()
    }

    // --- ТЕСТЫ: ДОБАВЛЕНИЕ И УДАЛЕНИЕ ---

    @Test
    fun `addPhotos adds new photos to the list`() {
        viewModel.addPhotos(listOf(Uri.EMPTY, Uri.EMPTY))
        assertEquals(2, viewModel.photos.value.size)
    }

    @Test
    fun `deleteCurrentPhoto removes the current photo`() {
        viewModel.addPhotos(listOf(Uri.parse("content://test/1"), Uri.parse("content://test/2")))
        viewModel.deleteCurrentPhoto()

        assertEquals(1, viewModel.photos.value.size)
        assertEquals("content://test/2", viewModel.photos.value[0].path)
    }

    @Test
    fun `deleteCurrentPhoto adjusts index if last photo is deleted`() {
        viewModel.addPhotos(listOf(Uri.parse("content://test/1"), Uri.parse("content://test/2")))
        viewModel.selectPhoto(1)
        viewModel.deleteCurrentPhoto()

        assertEquals(1, viewModel.photos.value.size)
        assertEquals(0, viewModel.currentPhotoIndex.value)
    }

    @Test
    fun `deleteCurrentPhoto on empty list does nothing`() {
        viewModel.deleteCurrentPhoto()
        assertEquals(0, viewModel.photos.value.size)
    }

    // --- ТЕСТЫ: ВЫБОР ФОТО ---

    @Test
    fun `selectPhoto changes current index if index is valid`() {
        viewModel.addPhotos(listOf(Uri.EMPTY, Uri.EMPTY))
        viewModel.selectPhoto(1)
        assertEquals(1, viewModel.currentPhotoIndex.value)
    }

    @Test
    fun `selectPhoto does nothing if index is out of bounds`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.selectPhoto(5)
        assertEquals(0, viewModel.currentPhotoIndex.value)
    }

    // --- ТЕСТЫ: ЭФФЕКТЫ ---

    @Test
    fun `toggleGrayscale switches grayscale flag`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.toggleGrayscale()
        assertTrue(viewModel.photos.value[0].grayscale)
    }

    @Test
    fun `toggleMirror switches mirrored flag`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.toggleMirror()
        assertTrue(viewModel.photos.value[0].mirrored)
    }

    @Test
    fun `updateBrightness changes brightness value`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.updateBrightness(0.5f)
        assertEquals(0.5f, viewModel.photos.value[0].brightness)
    }

    @Test
    fun `updateContrast changes contrast value`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.updateContrast(1.5f)
        assertEquals(1.5f, viewModel.photos.value[0].contrast)
    }

    @Test
    fun `updateRotation changes rotation value`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.updateRotation(45f)
        assertEquals(45f, viewModel.photos.value[0].rotation)
    }

    @Test
    fun `resetPhoto resets all effects to default`() {
        viewModel.addPhotos(listOf(Uri.EMPTY))
        viewModel.toggleGrayscale()
        viewModel.toggleMirror()
        viewModel.updateBrightness(0.8f)
        viewModel.updateContrast(2.0f)
        viewModel.updateRotation(90f)

        viewModel.resetPhoto()

        val photo = viewModel.photos.value[0]
        assertEquals(false, photo.grayscale)
        assertEquals(false, photo.mirrored)
        assertEquals(0f, photo.brightness)
        assertEquals(1f, photo.contrast)
        assertEquals(0f, photo.rotation) // <-- Проверяем сброс вращения
    }

    // --- ТЕСТЫ: ЭФФЕКТЫ ТОЛЬКО НА ТЕКУЩЕМ ФОТО ---

    @Test
    fun `effects apply only to current photo, not all`() {
        viewModel.addPhotos(listOf(Uri.EMPTY, Uri.EMPTY))
        viewModel.toggleGrayscale()

        assertEquals(true, viewModel.photos.value[0].grayscale)
        assertEquals(false, viewModel.photos.value[1].grayscale)
    }
}