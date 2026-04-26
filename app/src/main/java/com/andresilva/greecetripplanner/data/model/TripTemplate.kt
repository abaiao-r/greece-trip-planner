package com.andresilva.greecetripplanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TripTemplate(
    val key: String,
    val name: String,
    val icon: String,
    val description: String,
    val regions: List<String>,
    val dayPois: Map<Int, List<String>>
)
