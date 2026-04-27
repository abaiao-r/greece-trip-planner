package dev.greecetripplanner

import dev.greecetripplanner.util.formatHours
import dev.greecetripplanner.util.formatMinutes
import dev.greecetripplanner.util.driveHours
import dev.greecetripplanner.util.driveKm
import dev.greecetripplanner.util.activityHours
import dev.greecetripplanner.util.availableHours
import dev.greecetripplanner.util.budgetStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TripHelpersTest {

    @Test
    fun `formatHours whole number`() {
        assertEquals("2h", formatHours(2.0))
    }

    @Test
    fun `formatHours fractional`() {
        assertEquals("1.5h", formatHours(1.5))
    }

    @Test
    fun `formatMinutes`() {
        assertEquals("15min", formatMinutes(15))
    }

    @Test
    fun `driveHours null returns zero`() {
        assertEquals(0.0, driveHours(null, "athens"), 0.001)
        assertEquals(0.0, driveHours("athens", null), 0.001)
    }

    @Test
    fun `driveHours same region returns zero`() {
        assertEquals(0.0, driveHours("athens", "athens"), 0.001)
    }

    @Test
    fun `driveKm null returns zero`() {
        assertEquals(0, driveKm(null, "athens"))
        assertEquals(0, driveKm("athens", null))
    }

    @Test
    fun `driveKm same region returns zero`() {
        assertEquals(0, driveKm("athens", "athens"))
    }

    @Test
    fun `driveHours between known regions`() {
        val h = driveHours("athens", "delphi")
        assert(h > 0) { "Expected positive drive hours, got $h" }
    }

    @Test
    fun `driveKm between known regions`() {
        val km = driveKm("athens", "delphi")
        assert(km > 0) { "Expected positive drive km, got $km" }
    }

    @Test
    fun `activityHours with empty list`() {
        assertEquals(0.0, activityHours(emptyList()), 0.001)
    }

    @Test
    fun `availableHours day 0 is 3h`() {
        assertEquals(3.0, availableHours(0), 0.001)
    }

    @Test
    fun `availableHours day 1 is 10h`() {
        assertEquals(10.0, availableHours(1), 0.001)
    }

    @Test
    fun `availableHours last day is 4h`() {
        assertEquals(4.0, availableHours(7), 0.001)
    }

    @Test
    fun `budgetStatus ok`() {
        assertEquals("ok", budgetStatus(5.0, 10.0))
    }

    @Test
    fun `budgetStatus tight`() {
        assertEquals("tight", budgetStatus(9.0, 10.0))
    }

    @Test
    fun `budgetStatus over`() {
        assertEquals("over", budgetStatus(11.0, 10.0))
    }
}
