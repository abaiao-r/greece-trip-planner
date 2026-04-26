package com.andresilva.greecetripplanner.ui.screens.map

import android.graphics.DashPathEffect
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.andresilva.greecetripplanner.data.TripData
import com.andresilva.greecetripplanner.data.model.Poi
import com.andresilva.greecetripplanner.data.model.TripDay
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private val CARTO_VOYAGER = XYTileSource(
    "CartoDB-Voyager",
    0, 19, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
    ),
)

private val AIRPORT_POINT = GeoPoint(37.9364, 23.9445)

@Composable
fun MapComposable(
    days: List<TripDay>,
    activeDayIndex: Int?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(Unit) {
        mapView.setTileSource(CARTO_VOYAGER)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(7.0)
        mapView.controller.setCenter(GeoPoint(38.5, 23.8))

        onDispose {
            mapView.onDetach()
        }
    }

    DisposableEffect(days, activeDayIndex) {
        updateMapOverlays(mapView, days, activeDayIndex)
        onDispose { }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize(),
    )
}

private fun updateMapOverlays(
    mapView: MapView,
    days: List<TripDay>,
    activeDayIndex: Int?,
) {
    mapView.overlays.clear()

    val allPoints = mutableListOf<GeoPoint>()

    if (activeDayIndex != null) {
        // Show single day
        val day = days.getOrNull(activeDayIndex) ?: return
        val pois = day.poiIds.mapNotNull { TripData.poiMap[it] }
        addPoiMarkers(mapView, pois, activeDayIndex)

        val geoPoints = pois.map { GeoPoint(it.lat, it.lng) }
        allPoints.addAll(geoPoints)

        if (geoPoints.size >= 2) {
            val line = Polyline().apply {
                setPoints(geoPoints)
                outlinePaint.color = TripData.dayColors.getOrElse(activeDayIndex) { 0xFF1C69D4 }.toInt()
                outlinePaint.strokeWidth = 4f
            }
            mapView.overlays.add(line)
        }
    } else {
        // Show all days — draw full route through region centers
        val routePoints = mutableListOf<GeoPoint>()
        routePoints.add(AIRPORT_POINT)

        days.forEach { day ->
            val region = day.region?.let { TripData.regionMap[it] }
            if (region != null) {
                val regionCenter = GeoPoint(region.centerLat, region.centerLng)
                // Avoid duplicate consecutive points (same region across days)
                if (routePoints.isEmpty() || routePoints.last() != regionCenter) {
                    routePoints.add(regionCenter)
                }
            }
        }

        // Close loop back to airport
        routePoints.add(AIRPORT_POINT)

        if (routePoints.size >= 2) {
            val routeLine = Polyline().apply {
                setPoints(routePoints)
                outlinePaint.color = 0xFF1C69D4.toInt()
                outlinePaint.strokeWidth = 3f
                outlinePaint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 0f)
            }
            mapView.overlays.add(routeLine)
        }

        allPoints.addAll(routePoints)

        // Airport marker
        val airportMarker = Marker(mapView).apply {
            position = AIRPORT_POINT
            title = "Athens Airport (ATH)"
            snippet = "✈️ Start & End"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(airportMarker)

        // Per-day POI markers and polylines
        days.forEach { day ->
            val pois = day.poiIds.mapNotNull { TripData.poiMap[it] }
            addPoiMarkers(mapView, pois, day.dayIndex)

            val geoPoints = pois.map { GeoPoint(it.lat, it.lng) }

            if (geoPoints.size >= 2) {
                val line = Polyline().apply {
                    setPoints(geoPoints)
                    outlinePaint.color = TripData.dayColors.getOrElse(day.dayIndex) { 0xFF1C69D4 }.toInt()
                    outlinePaint.strokeWidth = 3f
                }
                mapView.overlays.add(line)
            }
        }
    }

    // Zoom to fit
    if (allPoints.size >= 2) {
        val box = BoundingBox.fromGeoPoints(allPoints)
        mapView.post {
            mapView.zoomToBoundingBox(box.increaseByScale(1.3f), true)
        }
    } else if (allPoints.size == 1) {
        mapView.controller.setCenter(allPoints[0])
        mapView.controller.setZoom(13.0)
    }

    mapView.invalidate()
}

private fun addPoiMarkers(
    mapView: MapView,
    pois: List<Poi>,
    dayIndex: Int,
) {
    pois.forEachIndexed { idx, poi ->
        val cat = TripData.categoryMap[poi.categories.firstOrNull() ?: ""]
        val marker = Marker(mapView).apply {
            position = GeoPoint(poi.lat, poi.lng)
            title = poi.name
            snippet = "D$dayIndex #${idx + 1} • ${cat?.icon ?: "📍"} ${poi.description}"
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }
        mapView.overlays.add(marker)
    }
}
