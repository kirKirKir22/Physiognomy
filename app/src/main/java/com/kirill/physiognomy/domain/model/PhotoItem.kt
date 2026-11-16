package com.kirill.physiognomy.domain.model


data class PhotoItem(
    val path: String,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val grayscale: Boolean = false,
    val mirrored: Boolean = false,
    val rotation: Float = 0f
)