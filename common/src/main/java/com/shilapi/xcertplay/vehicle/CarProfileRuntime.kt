// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import com.shilapi.xcertplay.airplay.AirPlayDisplaySettings

/**
 * Reads the running device into a [HeadUnitObservation] and resolves it against [LeapmotorC11Catalog].
 *
 * Nothing here gates a feature: a device that matches no profile keeps the values DiPlay has always
 * used. Tests replace [resolver] to run as any head unit in the catalog.
 */
object CarProfileRuntime {
    /** CarPlay frame rate for a head unit with no catalog entry — what DiPlay has always defaulted to. */
    const val GENERIC_RECOMMENDED_FPS = 30

    /** The device's own report. Tests put this back in `@After` when they replace [resolver]. */
    val defaultResolver: (Context) -> CarProfile? = { resolve(it) }

    /** Replace with a fixed profile in tests, and reset to [defaultResolver] in `@After`. */
    @Volatile
    var resolver: (Context) -> CarProfile? = defaultResolver

    fun match(context: Context): CarProfile? = resolver(context)

    /** The profile's frame rate when one matches, otherwise [GENERIC_RECOMMENDED_FPS]. */
    fun recommendedFps(context: Context): Int = AirPlayDisplaySettings.sanitizeFps(
        match(context)?.recommendedFps ?: GENERIC_RECOMMENDED_FPS,
    )

    /**
     * What the device reports about itself. The panel is read from the widest display ever seen
     * rather than the current window, so running in a smaller surface still identifies the car.
     */
    fun observation(context: Context): HeadUnitObservation {
        val (detectedWidth, detectedHeight) = AirPlayPersistence.loadMaximumDetectedDisplay(context)
        val metrics = context.resources.displayMetrics
        val fromDisplay = detectedWidth > 0 && detectedHeight > 0
        return HeadUnitObservation(
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            model = Build.MODEL.orEmpty(),
            board = Build.BOARD.orEmpty(),
            systemVersion = Build.DISPLAY.orEmpty(),
            displayWidthPixels = if (fromDisplay) detectedWidth else metrics.widthPixels,
            displayHeightPixels = if (fromDisplay) detectedHeight else metrics.heightPixels,
        )
    }

    private fun resolve(context: Context): CarProfile? = CarProfiles.match(observation(context))
}
