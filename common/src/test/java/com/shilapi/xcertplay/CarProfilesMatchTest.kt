package com.shilapi.xcertplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Matching runs on nothing but what the device reports, so these tests inject fake head units and
 * walk every catalog entry plus the devices DiPlay must leave alone.
 */
class CarProfilesMatchTest {
    private fun observation(
        profile: CarProfile,
        manufacturer: String = LeapmotorC11Catalog.OEM,
        width: Int = profile.panel.widthPixels,
        height: Int = profile.panel.heightPixels,
        systemVersion: String = profile.systemVersion,
    ) = HeadUnitObservation(
        manufacturer = manufacturer,
        brand = manufacturer,
        model = "${profile.modelLine} ${profile.modelYear}",
        board = "board",
        systemVersion = systemVersion,
        displayWidthPixels = width,
        displayHeightPixels = height,
    )

    /** What a match may never change, no matter which entry was ambiguous. */
    private fun assertSameHardware(expected: CarProfile, actual: CarProfile?) {
        assertNotNullMessage(expected, actual)
        actual!!
        assertEquals("${expected.id} panel", expected.panel, actual.panel)
        assertEquals("${expected.id} soc", expected.soc, actual.soc)
        assertEquals("${expected.id} fps", expected.recommendedFps, actual.recommendedFps)
        assertEquals("${expected.id} capabilities", expected.capabilities, actual.capabilities)
        assertEquals("${expected.id} brand", expected.oem, actual.oem)
    }

    private fun assertNotNullMessage(expected: CarProfile, actual: CarProfile?) {
        assertTrue("no head unit matched ${expected.id}", actual != null)
    }

    @Test fun everyCatalogEntryIsFoundFromItsOwnPanel() {
        for (profile in LeapmotorC11Catalog.all) {
            assertSameHardware(profile, CarProfiles.match(observation(profile)))
        }
    }

    @Test fun aConfirmedSystemReleaseSelectsExactlyThatModelYear() {
        for (profile in LeapmotorC11Catalog.all.filter { it.systemVersion.isNotBlank() }) {
            val match = CarProfiles.match(observation(profile))
            assertSameHardware(profile, match)
            assertEquals(
                "${profile.id} must not be resolved to another model year",
                profile.modelYear,
                match!!.modelYear,
            )
        }
    }

    @Test fun anUnconfirmedReleaseFindsTheSameHardwareAsTheNewestEntryOnThatPanel() {
        for (profile in LeapmotorC11Catalog.all.filter { it.systemVersion.isBlank() }) {
            val match = CarProfiles.match(observation(profile, systemVersion = ""))
            assertSameHardware(profile, match)
            assertEquals(
                "${profile.id} should fall back to the newest entry on its own panel",
                profile.panel,
                match!!.panel,
            )
        }
    }

    @Test fun aHeadUnitFromAnotherBrandIsNeverClaimed() {
        for (manufacturer in listOf("", "Google", "google", "HMD Global", "samsung")) {
            for (profile in LeapmotorC11Catalog.all) {
                assertNull(
                    "DiPlay must not claim a $manufacturer head unit as ${profile.id}",
                    CarProfiles.match(observation(profile, manufacturer = manufacturer)),
                )
            }
        }
    }

    @Test fun aChineseSpellingOfTheBrandIsAlsoRecognised() {
        val match = CarProfiles.match(observation(LeapmotorC11Catalog.all.first(), manufacturer = "零跑"))
        assertTrue("零跑 must resolve into the catalog", match != null)
        assertEquals(LeapmotorC11Catalog.OEM, match!!.oem)
    }

    @Test fun aLeapmotorWithAPanelOutsideTheCatalogIsNotGuessed() {
        for (size in listOf(800 to 480, 1024 to 600, 3840 to 2160)) {
            assertNull(
                "a ${size.first}x${size.second} Leapmotor is not a C11",
                CarProfiles.match(observation(LeapmotorC11Catalog.all.first(), width = size.first, height = size.second)),
            )
        }
    }

    @Test fun aPortraitReportStillMatchesTheLandscapePanel() {
        for (profile in LeapmotorC11Catalog.all.distinctBy { it.panel }) {
            val match = CarProfiles.match(observation(profile, width = profile.panel.heightPixels, height = profile.panel.widthPixels))
            assertSameHardware(profile, match)
        }
    }

    @Test fun smallReportedDeltasStillMatchButOverscanBeyondTheToleranceDoesNot() {
        val profile = LeapmotorC11Catalog.all.first { it.panel.widthPixels == 1920 }

        assertSameHardware(
            profile,
            CarProfiles.match(
                observation(
                    profile,
                    width = (profile.panel.widthPixels * 1.05).toInt(),
                    height = (profile.panel.heightPixels * 1.05).toInt(),
                ),
            ),
        )

        assertNull(
            "a panel reported 10% larger is a different head unit",
            CarProfiles.match(
                observation(
                    profile,
                    width = (profile.panel.widthPixels * 1.10).toInt(),
                    height = (profile.panel.heightPixels * 1.10).toInt(),
                ),
            ),
        )
    }

    @Test fun aDisplayThatHasNotBeenSeenYetNeverMatches() {
        val profile = LeapmotorC11Catalog.all.first()
        assertNull(CarProfiles.match(observation(profile, width = 0, height = 0)))
        assertNull(CarProfiles.match(observation(profile, width = -1, height = -1)))
    }

    @Test fun aTieIsResolvedToAnEntryWhoseBehaviorIsIdenticalToTheRealOne() {
        // A 2021 C11 reports no recognizable LeapOS release, so the year cannot be told from a 2022
        // or a 2023 one. The label may be wrong; the frame rate and the capabilities may not be.
        val real2021 = LeapmotorC11Catalog.byId("c11-2021-erev")!!
        val match = CarProfiles.match(observation(real2021, systemVersion = "V1.02.75"))

        assertSameHardware(real2021, match)
        assertNotEquals(
            "the panel alone cannot name the model year, only the hardware",
            real2021.modelYear,
            match!!.modelYear,
        )
    }
}
