package dev.greecetripplanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val key: String,
    val icon: String,
    val label: String,
    val colorHex: String
)
