// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.shilapi.xcertplay.adb.AdbKeys
import com.shilapi.xcertplay.adb.LocalAdb

/**
 * 零跑车机本机 ADB 通道（127.0.0.1:5555，与 BYD 版共用 [AdbKeys] 密钥）。
 *
 * 后台读取永不请求授权（mayAsk = false），行驶中不会弹出「允许 USB 调试？」框；
 * adbd 不可达或拒绝时每 30 s 才重试一次，且只记录一次警告日志（BydAdbShell 的退避范式）。
 */
internal class LeapmotorAdbShell(private val tag: String) {
    private var adb: LocalAdb? = null
    private var retryAtMillis = 0L
    private var unavailableLogged = false

    /** 最近一次连接尝试的结果；READY 且 [run] 返回 null 表示链路本身失败。 */
    @Volatile var lastAccess: LocalAdb.Access? = null
        private set

    @Synchronized
    fun run(context: Context, command: String): String? {
        val now = SystemClock.elapsedRealtime()
        if (now < retryAtMillis) return null
        val client = adb ?: LocalAdb(AdbKeys.load(context)).also { adb = it }
        val access = client.connect(mayAsk = false)
        lastAccess = access
        if (access != LocalAdb.Access.READY) {
            retryAtMillis = now + RETRY_MILLIS
            if (!unavailableLogged) {
                unavailableLogged = true
                Log.w(tag, "ADB access $access")
            }
            return null
        }
        unavailableLogged = false
        return client.shell(command)
    }

    @Synchronized
    fun close() {
        adb?.close()
        adb = null
    }

    private companion object {
        const val RETRY_MILLIS = 30_000L
    }
}
