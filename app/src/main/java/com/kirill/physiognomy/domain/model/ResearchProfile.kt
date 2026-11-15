package com.kirill.physiognomy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ResearchProfile(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String,
    val mode: String,
    val answers: Map<String, String> = emptyMap()
)