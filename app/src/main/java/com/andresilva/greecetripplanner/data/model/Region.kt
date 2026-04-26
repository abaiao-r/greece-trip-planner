package com.andresilva.greecetripplanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Region(
    val key: String,
    val name: String,
    val transitMinutes: Int,
    val centerLat: Double,
    val centerLng: Double,
    val description: String,
    val poiCount: Int
)
