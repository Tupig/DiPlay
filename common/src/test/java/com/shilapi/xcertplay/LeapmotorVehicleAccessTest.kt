// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class LeapmotorVehicleAccessTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        LeapmotorVehicleDataStore(context).saveAdbSpeedCommand(null)
    }

    @Test fun uncalibratedSpeedCommandDegradesToNullWithoutAdb() {
        assertNull(LeapmotorVehicleAccess.getVehicleSpeed(context))
        assertFalse(LeapmotorVehicleAccess.isAvailable(context))
    }

    @Test fun speedCommandRoundTripAndAvailabilityFollowCalibration() {
        val store = LeapmotorVehicleDataStore(context)
        assertNull(store.getAdbSpeedCommand())
        store.saveAdbSpeedCommand("  dumpsys car_service | grep speed  ")
        assertEquals("dumpsys car_service | grep speed", store.getAdbSpeedCommand())
        assertTrue(LeapmotorVehicleAccess.isAvailable(context))
        store.saveAdbSpeedCommand(null)
        assertNull(store.getAdbSpeedCommand())
        assertFalse(LeapmotorVehicleAccess.isAvailable(context))
        store.saveAdbSpeedCommand("   ")
        assertNull(store.getAdbSpeedCommand())
    }

    @Test fun parseSpeedReadsTheFirstNumberFromCommandOutput() {
        assertEquals(42.5f, LeapmotorVehicleAccess.parseSpeed("42.5 km/h"))
        assertEquals(0f, LeapmotorVehicleAccess.parseSpeed("0"))
        assertEquals(88.8f, LeapmotorVehicleAccess.parseSpeed("speed: 88.8"))
        assertNull(LeapmotorVehicleAccess.parseSpeed("no number"))
        assertNull(LeapmotorVehicleAccess.parseSpeed(""))
        assertNull(LeapmotorVehicleAccess.parseSpeed(null))
    }

    @Test fun calibratedSpeedReadSurvivesUnreachableAdbd() {
        LeapmotorVehicleDataStore(context).saveAdbSpeedCommand("dumpsys window")
        // 测试环境无 adbd（127.0.0.1:5555 拒连）：不抛异常，降级返回 null（#8 无 ADB 不崩溃）。
        assertNull(LeapmotorVehicleAccess.getVehicleSpeed(context))
    }
}
