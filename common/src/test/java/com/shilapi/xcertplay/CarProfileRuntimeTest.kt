package com.shilapi.xcertplay

import android.content.pm.PackageInfo
import com.shilapi.xcertplay.hud.BydOutputSettings
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * How the resolved profile reaches the app: the name iOS is shown, the frame rate a fresh install
 * starts on, and the line the diagnostic report prints so a real car's year can be confirmed later.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])
class CarProfileRuntimeTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private val prefs get() = context.getSharedPreferences("xcertplay_airplay", 0)

    private val c11_2024 get() = LeapmotorC11Catalog.byId("c11-2024-erev")!!
    private val c11_2021 get() = LeapmotorC11Catalog.byId("c11-2021-bev")!!

    @Before fun setUp() {
        clearBydDetection()
        prefs.edit().clear().commit()
    }

    @After fun tearDown() {
        CarProfileRuntime.resolver = CarProfileRuntime.defaultResolver
        clearBydDetection()
        prefs.edit().clear().commit()
    }

    private fun clearBydDetection() {
        listOf(
            "com.byd.carsettings",
            "com.byd.appmgr",
            "com.byd.deviceinfo",
            "com.byd.service",
            "com.byd.amapservice",
            "com.byd.clusterdebug",
            "com.ts.car.someip.service",
        ).forEach { shadowOf(context.packageManager).removePackage(it) }
    }

    @Test fun aMatchedHeadUnitIsNamedAfterItsOwnBrand() {
        CarProfileRuntime.resolver = { c11_2024 }

        assertEquals("Leapmotor", AirPlayPersistence.defaultOemLabel(context))
    }

    /** Detection order: BYD wins outright, so a catalog entry can never rename a real BYD. */
    @Test fun aBydHeadUnitStillKeepsTheBydLabelEvenWhenACatalogEntryMatches() {
        shadowOf(context.packageManager).installPackage(PackageInfo().apply {
            packageName = "com.byd.amapservice"
        })
        assertTrue(BydOutputSettings.available(context))
        CarProfileRuntime.resolver = { c11_2024 }

        assertEquals("BYD", AirPlayPersistence.defaultOemLabel(context))
    }

    @Test fun anUnmatchedHeadUnitIsNamedExactlyAsItWasBeforeTheCatalogExisted() {
        CarProfileRuntime.resolver = { null }

        val label = AirPlayPersistence.defaultOemLabel(context)
        assertFalse(BydOutputSettings.available(context))
        assertNotEquals("BYD", label)
        assertTrue("label has to read as a car name", label.any { it.isLetter() })
        assertFalse(
            "platform placeholders would read as the car's brand",
            label.lowercase() in setOf("android", "aosp", "generic", "unknown"),
        )
    }

    @Test fun theDefaultFrameRateComesFromTheMatchedHeadUnit() {
        CarProfileRuntime.resolver = { c11_2024 }
        assertEquals(60, AirPlayPersistence.loadFps(context))

        CarProfileRuntime.resolver = { c11_2021 }
        prefs.edit().clear().commit()
        assertEquals(30, AirPlayPersistence.loadFps(context))
    }

    @Test fun aFrameRateTheDriverChoseIsNeverOverwrittenByTheCatalog() {
        AirPlayPersistence.saveFps(context, 30)
        CarProfileRuntime.resolver = { c11_2024 }

        assertEquals(30, AirPlayPersistence.loadFps(context))
    }

    @Test fun aFreshInstallOnAnUnknownHeadUnitKeepsTheOldDefault() {
        CarProfileRuntime.resolver = { null }

        assertEquals(CarProfileRuntime.GENERIC_RECOMMENDED_FPS, AirPlayPersistence.loadFps(context))
    }

    /** The real code path, exercised on a device the catalog does not cover. */
    @Test fun theDefaultResolverMatchesNothingOnAHeadUnitOutsideTheCatalog() {
        assertNull(CarProfileRuntime.defaultResolver(context))
        assertNull(CarProfileRuntime.match(context))
    }
}
