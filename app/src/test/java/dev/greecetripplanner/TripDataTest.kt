package dev.greecetripplanner

import dev.greecetripplanner.data.TripData
import dev.greecetripplanner.data.model.TripDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TripDataTest {

    @Test
    fun `all templates have 8 regions`() {
        TripData.templates.forEach { t ->
            assertEquals("Template ${t.key} should have 8 regions", 8, t.regions.size)
        }
    }

    @Test
    fun `all templates have day POI entries`() {
        TripData.templates.forEach { t ->
            assertTrue(
                "Template ${t.key} should have day POI entries",
                t.dayPois.isNotEmpty()
            )
        }
    }

    @Test
    fun `all template day POIs reference valid POIs`() {
        TripData.templates.forEach { t ->
            t.dayPois.forEach { (day, poiIds) ->
                poiIds.forEach { id ->
                    assertNotNull(
                        "POI $id in template ${t.key} day $day not found",
                        TripData.poiMap[id]
                    )
                }
            }
        }
    }

    @Test
    fun `all POIs reference valid regions`() {
        TripData.pois.forEach { poi ->
            assertNotNull(
                "POI ${poi.id} references unknown region ${poi.region}",
                TripData.regionMap[poi.region]
            )
        }
    }

    @Test
    fun `all POIs reference valid categories`() {
        TripData.pois.forEach { poi ->
            poi.categories.forEach { catKey ->
                assertNotNull(
                    "POI ${poi.id} references unknown category $catKey",
                    TripData.categoryMap[catKey]
                )
            }
        }
    }

    @Test
    fun `regions have valid coordinates`() {
        TripData.regions.forEach { r ->
            assertTrue("Region ${r.key} lat should be > 0", r.centerLat > 0)
            assertTrue("Region ${r.key} lng should be > 0", r.centerLng > 0)
        }
    }

    @Test
    fun `TripDay TOTAL_DAYS is 8`() {
        assertEquals(8, TripDay.TOTAL_DAYS)
    }

    @Test
    fun `TripDay DATES has correct size`() {
        assertEquals(TripDay.TOTAL_DAYS, TripDay.DATES.size)
    }

    @Test
    fun `all templates have narratives`() {
        TripData.templates.forEach { t ->
            assertTrue(
                "Template ${t.key} should have narratives",
                t.dayNarratives.isNotEmpty()
            )
        }
    }
}
