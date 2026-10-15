package com.shilapi.xcertplay

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.shilapi.xcertplay.adb.AdbKeys
import java.security.MessageDigest

/**
 * An accessibility service to automatically check "Always allow / Use by default"
 * and click "OK / Confirm" when the Android system USB permission dialog appears for DiPlay.
 */
class UsbAutoConfirmService : AccessibilityService() {

    private var lastClickTime = -DEBOUNCE_MILLIS
    private var usbWindowId: Int? = null
    private var ignoredWindowId: Int? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val className = event.className?.toString()
            usbWindowId = if (isSystemUsbWindow(event.packageName?.toString(), className)) {
                if (event.windowId != usbWindowId) Log.i(TAG, "System dialog window class=$className; checking its prompt")
                event.windowId
            } else null
        }
        if (usbWindowId != event.windowId) return
        val now = SystemClock.uptimeMillis()
        if (now - lastClickTime < DEBOUNCE_MILLIS) return
        val root = runCatching { rootInActiveWindow }.getOrNull() ?: return
        try {
            if (root.windowId != usbWindowId || root.packageName?.toString() !in SYSTEM_PACKAGES) return
            val texts = mutableListOf<String>()
            visit(root) { node ->
                node.text?.toString()?.let(texts::add)
                node.contentDescription?.toString()?.let(texts::add)
                false
            }
            val joined = texts.joinToString(" ")
            val appLabel = applicationInfo.loadLabel(packageManager).toString()
            if (!isTargetPrompt(joined, appLabel)) {
                // The ADB "Allow USB debugging?" dialog never names DiPlay; the only stable
                // identity of the request is the fingerprint of the key we generated. Foreign
                // computers' fingerprints do not match and stay manual.
                if (!isOwnAdbAuthorizationPrompt(joined, ownAdbFingerprints(applicationContext))) {
                    if (ignoredWindowId != usbWindowId) {
                        ignoredWindowId = usbWindowId
                        Log.i(TAG, "System dialog does not ask for DiPlay USB access; left alone")
                    }
                    return
                }
                visit(root) { node ->
                    node.isCheckable && !node.isChecked && node.isEnabled &&
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                val adbConfirmed = visit(root) { node ->
                    val label = node.text?.toString()?.trim()
                    node.isEnabled && node.isClickable &&
                        (node.viewIdResourceName == "android:id/button1" || label in CONFIRM_LABELS) &&
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                }
                if (adbConfirmed) {
                    lastClickTime = now
                    Log.i(TAG, "Auto-confirmed ADB debugging authorization for our own key")
                }
                return
            }
            // Only the system USB dialog's optional default checkbox may be changed.
            visit(root) { node ->
                node.isCheckable && !node.isChecked && node.isEnabled &&
                    node.viewIdResourceName == "android:id/alwaysUse" &&
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            val confirmed = visit(root) { node ->
                val label = node.text?.toString()?.trim()
                node.isEnabled && node.isClickable &&
                    (node.viewIdResourceName == "android:id/button1" || label in CONFIRM_LABELS) &&
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            if (confirmed) {
                lastClickTime = now
                Log.i(TAG, "Successfully auto-confirmed DiPlay USB permission dialog")
            }
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }
    }

    override fun onInterrupt() { usbWindowId = null }

    private fun visit(node: AccessibilityNodeInfo, depth: Int = 0, action: (AccessibilityNodeInfo) -> Boolean): Boolean {
        if (depth > 32) return false
        if (action(node)) return true
        for (i in 0 until node.childCount) {
            val child = runCatching { node.getChild(i) }.getOrNull() ?: continue
            try {
                if (visit(child, depth + 1, action)) return true
            } finally {
                @Suppress("DEPRECATION")
                child.recycle()
            }
        }
        return false
    }

    companion object {
        private const val TAG = "UsbAutoConfirm"
        private const val DEBOUNCE_MILLIS = 800L
        private val SYSTEM_PACKAGES = setOf("com.android.systemui", "android")
        private val USB_ACTIVITIES = setOf(
            "com.android.systemui.usb.UsbPermissionActivity",
            "com.android.systemui.usb.UsbConfirmActivity",
        )
        /**
         * Both USB activities extend AlertActivity, which reports its window to accessibility as
         * android.app.Dialog rather than by activity name, on Android 7 to 14 alike. So the USB
         * dialog never matched, and auto-confirm did nothing even when BYD showed the system
         * UsbPermissionActivity (#409). Other systemui dialog activities, such as screen-capture
         * consent, report the same name, so [isTargetPrompt] must also find a USB device.
         */
        private const val ALERT_ACTIVITY_CLASS = "android.app.Dialog"
        private const val ADB_DEBUGGING_ACTIVITY = "com.android.server.adb.AdbDebuggingActivity"
        private val CONFIRM_LABELS = setOf("确定", "允许", "OK", "Allow", "Confirm")
        private val USB_DEVICE_WORDS = Regex("USB|iPhone|iPad", RegexOption.IGNORE_CASE)

        internal fun isSystemUsbWindow(pkg: String?, className: String?): Boolean =
            pkg in SYSTEM_PACKAGES && (className in USB_ACTIVITIES || className == ALERT_ACTIVITY_CLASS ||
                className == ADB_DEBUGGING_ACTIVITY)

        /**
         * The prompt must name this app and a USB device. Android 10+ words it "Allow DiPlay to
         * access iPhone?" with no "USB", Android 7.1 names "the USB device" instead of the phone,
         * and Chinese puts the name between letters ("要允许DiPlay访问iPhone吗？"), so only ASCII
         * word characters count as part of a longer name such as "FakeDiPlay".
         */
        internal fun isTargetPrompt(text: String, appLabel: String): Boolean =
            appLabel.isNotBlank() &&
                Regex("(?<![A-Za-z0-9_])${Regex.escape(appLabel)}(?![A-Za-z0-9_])", RegexOption.IGNORE_CASE)
                    .containsMatchIn(text) &&
                USB_DEVICE_WORDS.containsMatchIn(text)

        @Volatile
        private var cachedOwnAdbFingerprints: List<String>? = null

        internal fun installOwnAdbFingerprintsForTests(fingerprints: List<String>?) {
            cachedOwnAdbFingerprints = fingerprints
        }

        /** Fingerprints of our own ADB public key in the encodings adbd may present, lowercase hex without separators. */
        internal fun ownAdbFingerprints(context: Context): List<String>? {
            cachedOwnAdbFingerprints?.let { return it }
            return runCatching {
                val public = AdbKeys.load(context).public
                fun digest(algorithm: String, data: ByteArray): String =
                    MessageDigest.getInstance(algorithm).digest(data).joinToString("") { "%02x".format(it.toInt() and 0xff) }
                val message = AdbKeys.publicKeyMessage(public)
                val base64 = String(message, Charsets.UTF_8).substringBefore(' ')
                val blob = Base64.decode(base64, Base64.DEFAULT)
                buildList {
                    for (algorithm in listOf("MD5", "SHA-256")) {
                        add(digest(algorithm, public.encoded))
                        add(digest(algorithm, base64.toByteArray(Charsets.UTF_8)))
                        add(digest(algorithm, blob))
                    }
                }
            }.onSuccess { cachedOwnAdbFingerprints = it }.getOrNull()
        }

        /**
         * The ADB "Allow USB debugging?" prompt never names DiPlay and is localized, so the only
         * stable identity is the fingerprint of our own key. Foreign computers present their own
         * keys, do not match, and stay manual.
         */
        internal fun isOwnAdbAuthorizationPrompt(text: String, fingerprints: List<String>?): Boolean {
            if (fingerprints.isNullOrEmpty()) return false
            val hexStream = buildString {
                for (c in text.lowercase()) if (c.isDigit() || c in 'a'..'f') append(c)
            }
            if (hexStream.length < 32) return false
            return fingerprints.any { hexStream.contains(it) }
        }


        fun isEnabled(context: Context): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            ) ?: return false
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            val myService = ComponentName(context, UsbAutoConfirmService::class.java).flattenToString()
            val myShortService = ComponentName(context, UsbAutoConfirmService::class.java).flattenToShortString()
            while (colonSplitter.hasNext()) {
                val componentName = colonSplitter.next()
                if (componentName.equals(myService, ignoreCase = true) ||
                    componentName.equals(myShortService, ignoreCase = true)
                ) {
                    return true
                }
            }
            return false
        }

        /** BYD's settings app does not answer the standard action but has its own screen (#409). */
        private val ACCESSIBILITY_SCREENS = listOf(
            ComponentName("com.byd.systemsettings", "com.byd.systemsettings.accessibility.AccessibilityMainActivity"),
            ComponentName("com.android.settings", "com.android.settings.Settings\$AccessibilitySettingsActivity"),
        )

        fun openSettings(context: Context): Boolean =
            (listOf(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) + ACCESSIBILITY_SCREENS.map { Intent().setComponent(it) })
                .any { intent ->
                    runCatching {
                        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        true
                    }.getOrDefault(false)
                }
    }
}
