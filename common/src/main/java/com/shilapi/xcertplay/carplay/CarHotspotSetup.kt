package com.shilapi.xcertplay

import android.content.Context
import android.content.pm.ApplicationInfo
import android.provider.Settings
import android.util.Log
import com.shilapi.xcertplay.adb.AdbKeys
import com.shilapi.xcertplay.adb.LocalAdb
import com.shilapi.xcertplay.leapmotor.LeapmotorOutputSettings
import com.shilapi.xcertplay.network.CarHotspotSettings
import com.shilapi.xcertplay.orchestration.ManualHotspotValidation

/** Each grant is requested explicitly from settings; startup never calls this authorization path. */
internal object CarHotspotSetup {
    // 闆惰窇杞︽満妫€娴嬶細閫氳繃杞﹁締鏁版嵁鎺ュ彛鎴栧寘鍚嶆娴?
    fun isLeapmotorHeadUnit(context: Context): Boolean = LeapmotorOutputSettings.clusterNavigation(context) || runCatching {
        context.packageManager.getApplicationInfo("com.leapmotor.carsettings", 0).flags and ApplicationInfo.FLAG_SYSTEM != 0
    }.getOrDefault(false)

    // 鍏煎鏃т唬鐮?
    @Deprecated("浣跨敤 isLeapmotorHeadUnit")
    fun isBydHeadUnit(context: Context): Boolean = isLeapmotorHeadUnit(context)

    enum class Permission(val appOp: String) {
        HOTSPOT("WRITE_SETTINGS"), BOOT_LAUNCH("SYSTEM_ALERT_WINDOW");

        fun granted(context: Context): Boolean = when (this) {
            HOTSPOT -> Settings.System.canWrite(context)
            BOOT_LAUNCH -> Settings.canDrawOverlays(context)
        }
    }

    fun check(context: Context, adb: LocalAdb = LocalAdb(AdbKeys.load(context))): LocalAdb.Access = adb.use {
        it.connect(mayAsk = false)
    }

    fun grant(context: Context, permissions: List<Permission>, adb: LocalAdb = LocalAdb(AdbKeys.load(context))): LocalAdb.Access =
        adb.use {
            val access = it.connect(mayAsk = true)
            Log.i("DiPlay-ADB", "switch connection: $access")
            if (access == LocalAdb.Access.READY) {
                for (permission in permissions) {
                    if (!permission.granted(context)) {
                        Log.i("DiPlay-ADB", "request permission: ${permission.appOp}")
                        it.shell("appops set ${context.packageName} ${permission.appOp} allow")
                    }
                    val granted = permission.granted(context)
                    Log.i("DiPlay-ADB", "permission ${permission.appOp}: granted=$granted")
                    if (!granted) break
                }
            }
            access
        }

    fun shouldStartOnLaunch(context: Context, hasSession: Boolean): Boolean =
        !hasSession && CarHotspotSettings.shouldEnable(context,
            AirPlayPersistence.loadWirelessEnabled(context), AirPlayPersistence.loadWirelessHotspotMode(context)) &&
            ManualHotspotValidation.error(AirPlayPersistence.loadManualHotspotSsid(context),
                AirPlayPersistence.loadManualHotspotPassphrase(context)) == null
}

