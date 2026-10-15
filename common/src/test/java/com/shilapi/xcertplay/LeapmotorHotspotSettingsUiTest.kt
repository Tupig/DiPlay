// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.shilapi.xcertplay.host.R
import com.shilapi.xcertplay.orchestration.WirelessHotspotMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

/**
 * #8: a Leapmotor head unit with ADB takes the same ADB hotspot card as BYD instead of the
 * system-settings fallback; units that are neither keep the existing fallback.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29], qualifiers = "en", manifest = Config.NONE)
class LeapmotorHotspotSettingsUiTest {
    private lateinit var activity: DiPlayActivity
    private lateinit var parent: LinearLayout

    @Before fun setUp() {
        val app = RuntimeEnvironment.getApplication()
        app.getSharedPreferences("diplay_car_hotspot", 0).edit().clear().commit()
        shadowOf(app.packageManager).installPackage(PackageInfo().apply {
            packageName = "com.leapmotor.carsettings"
            applicationInfo = ApplicationInfo().apply {
                packageName = "com.leapmotor.carsettings"
                flags = ApplicationInfo.FLAG_SYSTEM
            }
        })
        activity = Robolectric.buildActivity(DiPlayActivity::class.java).get()
        activity.setTheme(android.R.style.Theme_Material_NoActionBar)
        AirPlayPersistence.saveWirelessHotspotMode(activity, WirelessHotspotMode.MANUAL)
        parent = LinearLayout(activity)
        assertTrue(CarHotspotSetup.isLeapmotorHeadUnit(activity))
        assertFalse(CarHotspotSetup.isBydHeadUnit(activity))
    }

    @Test fun leapmotorHeadUnitTakesTheAdbHotspotCardNotTheSystemFallback() {
        bydAdbSettings()
        // The synchronous part of the ADB path: one hidden container awaiting the ADB probe thread.
        assertEquals(1, parent.childCount)
        assertEquals(View.GONE, parent.getChildAt(0).visibility)
        assertFalse(labels(parent).contains(activity.getString(R.string.settings_car_hotspot_control)))
    }

    @Test fun bydHeadUnitDoesNotFallBackToSystemHotspotSettings() {
        shadowOf(RuntimeEnvironment.getApplication().packageManager).installPackage(PackageInfo().apply {
            packageName = "com.byd.carsettings"
            applicationInfo = ApplicationInfo().apply {
                packageName = "com.byd.carsettings"
                flags = ApplicationInfo.FLAG_SYSTEM
            }
        })
        assertTrue(CarHotspotSetup.isBydHeadUnit(activity))
        bydAdbSettings()
        assertEquals(1, parent.childCount)
        assertEquals(View.GONE, parent.getChildAt(0).visibility)
        assertFalse(labels(parent).contains(activity.getString(R.string.settings_car_hotspot_control)))
    }

    @Test fun unknownHeadUnitKeepsTheSystemHotspotFallback() {
        shadowOf(RuntimeEnvironment.getApplication().packageManager).removePackage("com.leapmotor.carsettings")
        assertFalse(CarHotspotSetup.isLeapmotorHeadUnit(activity))
        bydAdbSettings()
        assertTrue(labels(parent).contains(activity.getString(R.string.settings_car_hotspot_control)))
    }

    private fun bydAdbSettings() {
        ReflectionHelpers.callInstanceMethod<Unit>(activity, "bydAdbSettings",
            ReflectionHelpers.ClassParameter.from(LinearLayout::class.java, parent))
    }

    private fun descendants(view: View): List<View> = buildList {
        add(view)
        if (view is ViewGroup) for (index in 0 until view.childCount) addAll(descendants(view.getChildAt(index)))
    }

    private fun labels(view: View): List<String> =
        descendants(view).filterIsInstance<TextView>().map { it.text.toString() }
}
