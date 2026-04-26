package com.andresilva.greecetripplanner.data.model

import kotlinx.serialization.Serializable

/**
 * A point of interest in Greece with all its metadata.
 * These are pre-seeded and immutable.
 */
@Serializable
data class Poi(
    val id: String,
    val name: String,
    val region: String,
    val hours: Double,
    val categories: List<String>,
    val lat: Double,
    val lng: Double,
    val description: String,
    val tip: String? = null
) {
    val mapsUrl: String
        get() = "https://www.google.com/maps/search/?api=1&query=$lat,$lng"

    val searchUrl: String
        get() = "https://www.google.com/search?q=${java.net.URLEncoder.encode("$name Greece", "UTF-8")}"

    val imagesUrl: String
        get() = "https://www.google.com/search?tbm=isch&q=${java.net.URLEncoder.encode("$name Greece", "UTF-8")}"
}
