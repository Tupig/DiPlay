package com.shilapi.xcertplay

import android.content.pm.PackageInfo
import com.shilapi.xcertplay.hud.BydOutputSettings
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * The OEM label is what the phone is told the car is. It has to name the head unit that is really
 * running the app, while every head unit that still reports as BYD keeps the BYD default.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])
class OemLabelTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before fun setUp() = clearBydDetection()

    @After fun tearDown() = clearBydDetection()

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
        context.getSharedPreferences("xcertplay_airplay", 0).edit().clear().commit()
    }

    @Test fun aHeadUnitWithoutBydIsNeverPresentedToThePhoneAsAByd() {
        assertFalse(BydOutputSettings.available(context))

        assertNotEquals("BYD", AirPlayPersistence.defaultOemLabel(context))
    }

    @Test fun theDetectedLabelIsAPlausibleCarNameRatherThanAPlatformPlaceholder() {
        assertFalse(BydOutputSettings.available(context))

        val label = AirPlayPersistence.defaultOemLabel(context)
        assertTrue("blank label hides the car icon on the phone", label.isNotBlank())
        assertTrue("label has to read as a car name", label.any { it.isLetter() })
        assertFalse(
            "platform placeholders would read as the car's brand",
            label.lowercase() in setOf("android", "aosp", "generic", "unknown"),
        )
    }

    @Test fun aHeadUnitStillReportingAsBydKeepsTheBydDefault() {
        shadowOf(context.packageManager).installPackage(PackageInfo().apply {
            packageName = "com.byd.amapservice"
        })
        assertTrue(BydOutputSettings.available(context))

        assertEquals("BYD", AirPlayPersistence.defaultOemLabel(context))
    }

    @Test fun aNameSavedByTheDriverWinsOverTheDetectedDefault() {
        AirPlayPersistence.saveOemLabel(context, "My car")

        assertEquals("My car", AirPlayPersistence.loadOemLabel(context))
    }

    @Test fun aStoredButBlankNameFallsBackToTheDetectedDefault() {
        AirPlayPersistence.saveOemLabel(context, "   ")

        assertNotEquals("BYD", AirPlayPersistence.loadOemLabel(context))
    }
}
