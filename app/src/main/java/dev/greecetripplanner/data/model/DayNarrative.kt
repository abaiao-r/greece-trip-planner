package dev.greecetripplanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DayNarrative(
    val tagline: String,
    val description: String,
    val tip: String? = null,
    val tags: List<String> = emptyList()
)
