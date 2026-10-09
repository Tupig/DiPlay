package com.shilapi.xcertplay

import com.shilapi.xcertplay.airplay.AirPlayDisplaySettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

/**
 * The catalog is pure data, so every claim about it is checked on the JVM: which years exist, whether
 * the panel geometry agrees with the published diagonal, and above all whether two entries that
 * [CarProfiles] could confuse are actually interchangeable.
 */
class LeapmotorC11CatalogTest {
    @Test fun coversEveryModelYearFromTheFirstC11ThroughTheLatestOne() {
        assertEquals(
            listOf(2021, 2022, 2023, 2024, 2025),
            LeapmotorC11Catalog.all.map { it.modelYear }.distinct().sorted(),
        )
    }

    @Test fun coversBothPowertrainsInEveryModelYear() {
        for (year in 2021..2025) {
            val powertrains = LeapmotorC11Catalog.all
                .filter { it.modelYear == year }
                .map { it.powertrain }
                .sorted()
            assertEquals(year.toString(), LeapmotorC11Catalog.powertrains.sorted(), powertrains)
        }
    }

    @Test fun everyEntryHasAUniqueIdThatResolvesBackToIt() {
        val ids = LeapmotorC11Catalog.all.map { it.id }
        assertEquals("profile ids must be unique", ids.size, ids.toSet().size)
        for (profile in LeapmotorC11Catalog.all) {
            assertEquals(profile, LeapmotorC11Catalog.byId(profile.id))
        }
    }

    @Test fun panelMillimetersAgreeWithThePublishedDiagonal() {
        for (profile in LeapmotorC11Catalog.all) {
            val panel = profile.panel
            val measured = hypot(panel.widthMillimeters.toDouble(), panel.heightMillimeters.toDouble())
            val published = panel.diagonalInches * 25.4
            assertEquals("${profile.id} panel", published, measured, published * 0.02)
        }
    }

    @Test fun recommendedFrameRatesAreValidSettingsValues() {
        for (profile in LeapmotorC11Catalog.all) {
            assertTrue(
                "${profile.id} fps ${profile.recommendedFps}",
                profile.recommendedFps in AirPlayDisplaySettings.MIN_FPS..AirPlayDisplaySettings.MAX_FPS,
            )
            assertEquals(
                "${profile.id} fps must survive the settings sanitizer unchanged",
                profile.recommendedFps,
                AirPlayDisplaySettings.sanitizeFps(profile.recommendedFps),
            )
        }
    }

    /**
     * [CarProfiles] can only narrow a match down to the panel, so entries sharing a panel must agree
     * on everything that changes what the app does. Without this, an ambiguous match would silently
     * alter the frame rate or a capability.
     */
    @Test fun entriesSharingAPanelAlwaysAgreeOnEverythingThatChangesBehavior() {
        val disagreements = LeapmotorC11Catalog.all
            .groupBy { it.panel }
            .filterValues { profiles -> profiles.map { it.behavior }.distinct().size > 1 }
        assertTrue(
            "profiles sharing a panel must be interchangeable, but these disagree: $disagreements",
            disagreements.isEmpty(),
        )
    }

    /** The head unit changed once, in 2024; a catalog with more generations is a different claim. */
    @Test fun exactlyOneGenerationChangedWithinTheCatalogedYears() {
        assertEquals(2, LeapmotorC11Catalog.all.map { it.panel }.distinct().size)
        assertEquals(2, LeapmotorC11Catalog.all.map { it.soc }.distinct().size)
    }

    @Test fun vehicleDataStaysOffUntilItHasBeenProvenOnARealCar() {
        for (profile in LeapmotorC11Catalog.all) {
            assertFalse(
                "${profile.id} has no known vehicle-data channel yet",
                profile.capabilities.vehicleData,
            )
        }
    }

    @Test fun everyEntryIsNamedAfterTheBrandItIsCatalogedUnder() {
        for (profile in LeapmotorC11Catalog.all) {
            assertEquals(LeapmotorC11Catalog.OEM, profile.oem)
            assertEquals(LeapmotorC11Catalog.MODEL_LINE, profile.modelLine)
            assertEquals("${profile.oem} ${profile.modelLine} ${profile.modelYear}", profile.label)
        }
    }

    private val CarProfile.behavior: Triple<String, Int, CarCapabilities>
        get() = Triple(soc, recommendedFps, capabilities)
}
