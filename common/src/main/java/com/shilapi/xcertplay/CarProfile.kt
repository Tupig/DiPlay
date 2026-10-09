// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

/**
 * The panel a head unit drives CarPlay onto. Dimensions are the native panel values, not the current
 * window, so they stay stable while the app runs in a smaller surface.
 */
data class CarPanel(
    val diagonalInches: Double,
    val widthPixels: Int,
    val heightPixels: Int,
    val widthMillimeters: Int,
    val heightMillimeters: Int,
    val refreshHz: Int,
) {
    init {
        require(widthPixels > 0 && heightPixels > 0) { "panel pixels must be positive" }
        require(widthMillimeters > 0 && heightMillimeters > 0) { "panel millimeters must be positive" }
        require(diagonalInches > 0) { "panel diagonal must be positive" }
    }
}

/**
 * What a head unit can do. Consumers stay capability-gated: a head unit with no profile keeps the
 * answers it has today, so an unmatched device can never lose a feature.
 */
data class CarCapabilities(
    /** A separate screen CarPlay can present its map on. */
    val clusterDisplay: Boolean,
    /** The passenger half of a triple-display layout. */
    val copilotDisplay: Boolean,
    /** Vehicle signals the app can read: battery, speed, gear, doors. */
    val vehicleData: Boolean,
    val usbHostPort: Boolean,
    val wifiDirect: Boolean,
)

/**
 * One head-unit variant. Pure data, so the whole catalog can be built and exercised on the JVM
 * without an Android device or a real car.
 */
data class CarProfile(
    /** Stable identifier, e.g. `c11-2024-erev`. Tests and diagnostics key off this. */
    val id: String,
    /** Brand shown to the phone in place of a platform placeholder. */
    val oem: String,
    val modelLine: String,
    val modelYear: Int,
    val powertrain: String,
    /** System on chip: `SA8155P` before the 2024 refresh, `SA8295P` from 2024 on. */
    val soc: String,
    /** Head-unit system release, matched against `Build.DISPLAY`. */
    val systemVersion: String,
    val panel: CarPanel,
    val capabilities: CarCapabilities,
    /** CarPlay frame rate this hardware sustains by default; within `AirPlayDisplaySettings` range. */
    val recommendedFps: Int,
) {
    init {
        require(id.isNotBlank()) { "profile id must not be blank" }
        require(modelYear in 1990..2100) { "implausible model year $modelYear" }
    }

    /** Read on the phone and in the diagnostic report as the car's name. */
    val label: String get() = "$oem $modelLine $modelYear"

    /** Hardware identification for the diagnostic report, next to the raw `Build` strings. */
    val detail: String
        get() = "$label · $soc · ${panel.diagonalInches}\" · " +
            "${panel.widthPixels}×${panel.heightPixels} · $systemVersion"
}

/** Everything the running device reports about itself. Tests inject fake head-unit values here. */
data class HeadUnitObservation(
    /** `Build.MANUFACTURER` */
    val manufacturer: String,
    /** `Build.BRAND` */
    val brand: String,
    /** `Build.MODEL` */
    val model: String,
    /** `Build.BOARD` */
    val board: String,
    /** `Build.DISPLAY`, carries the head-unit system release such as a LeapOS version. */
    val systemVersion: String,
    /** The widest display the app has seen, i.e. the panel, not the current window. */
    val displayWidthPixels: Int,
    val displayHeightPixels: Int,
) {
    /**
     * Lower-case text every OEM token is searched in. `Build` strings differ per region and firmware,
     * so a head unit is identified by matching any of several spellings rather than one exact value.
     */
    val buildFingerprint: String
        get() = listOf(manufacturer, brand, model, board, systemVersion)
            .joinToString(" ")
            .lowercase()
}

/**
 * Resolves the head unit the app is running on to a catalog entry.
 *
 * Two gates, neither of which gates a feature: the build must read as an OEM the catalog covers, and
 * the panel it drives must be one of that catalog's panels. A device that clears neither gate gets
 * null and behaves exactly as it does today.
 */
object CarProfiles {
    /** Relative error per axis still accepted as a match, allowing for overscan and reported margins. */
    const val GEOMETRY_TOLERANCE = 0.06

    /**
     * Finds the profile for [observation], or null when the device is not a covered head unit.
     *
     * Several entries describe the same panel — model years and powertrains that share hardware. Such
     * a tie is resolved by [HeadUnitObservation.systemVersion] when it names one of them, otherwise to
     * the newest, which is safe because every field that affects behavior is identical across a tie
     * (the catalog tests assert that).
     */
    fun match(
        observation: HeadUnitObservation,
        catalog: List<CarProfile> = LeapmotorC11Catalog.all,
        oemTokens: Set<String> = LeapmotorC11Catalog.oemTokens,
    ): CarProfile? {
        if (oemTokens.none { it.lowercase() in observation.buildFingerprint }) return null

        val candidates = catalog.filter { panelMatches(observation, it.panel) }
            .takeIf { it.isNotEmpty() } ?: return null

        candidates.firstOrNull { it.systemVersion.isNotBlank() && it.systemVersion in observation.systemVersion }
            ?.let { return it }

        return candidates.maxByOrNull { it.modelYear }
    }

    /** Rotation-normalised panel comparison, so a portrait observation still matches a landscape panel. */
    private fun panelMatches(observation: HeadUnitObservation, panel: CarPanel): Boolean {
        val width = observation.displayWidthPixels
        val height = observation.displayHeightPixels
        if (width <= 0 || height <= 0) return false
        val landscape = withinTolerance(width, panel.widthPixels) && withinTolerance(height, panel.heightPixels)
        val portrait = withinTolerance(width, panel.heightPixels) && withinTolerance(height, panel.widthPixels)
        return landscape || portrait
    }

    private fun withinTolerance(observed: Int, expected: Int): Boolean =
        kotlin.math.abs(observed - expected).toDouble() / expected <= GEOMETRY_TOLERANCE
}
