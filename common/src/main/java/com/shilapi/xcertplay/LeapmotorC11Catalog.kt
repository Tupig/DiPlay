// SPDX-License-Identifier: AGPL-3.0-only
package com.shilapi.xcertplay

/**
 * Every Leapmotor C11 head unit DiPlay claims to cover, model year by model year and powertrain by
 * powertrain.
 *
 * The C11 changed head unit once: the 2024 refresh moved it from the SA8155P driving a 12.8" 1080p
 * panel to the SA8295P driving a 14.6" 2.5K panel, and the system release jumped from the 1.x LeapOS
 * line to the 3.x line. Everything inside one of those two generations is interchangeable, which is
 * what lets [CarProfiles] resolve an ambiguous match without changing behavior.
 *
 * Values are the published panel specifications. They stand in until a diagnostic report from a real
 * car confirms what its `Build` strings actually say; tests inject [HeadUnitObservation] directly, so
 * nothing here has to be true on a device to be exercised.
 */
object LeapmotorC11Catalog {
    const val OEM = "Leapmotor"
    const val MODEL_LINE = "C11"

    /** Spellings of the brand as it can appear in `Build`, Latin and Chinese. */
    val oemTokens: Set<String> = setOf(OEM.lowercase(), "零跑")

    /** C11 is sold as an extended-range and as a pure-electric car; both share the head unit. */
    val powertrains: List<String> = listOf("EREV", "BEV")

    private data class Generation(
        val years: List<Int>,
        val panel: CarPanel,
        val soc: String,
        val recommendedFps: Int,
    )

    private val generations = listOf(
        Generation(
            years = listOf(2021, 2022, 2023),
            panel = CarPanel(
                diagonalInches = 12.8,
                widthPixels = 1920,
                heightPixels = 1080,
                widthMillimeters = 283,
                heightMillimeters = 159,
                refreshHz = 60,
            ),
            soc = "SA8155P",
            recommendedFps = 30,
        ),
        Generation(
            years = listOf(2024, 2025),
            panel = CarPanel(
                diagonalInches = 14.6,
                widthPixels = 2560,
                heightPixels = 1600,
                widthMillimeters = 315,
                heightMillimeters = 197,
                refreshHz = 60,
            ),
            soc = "SA8295P",
            recommendedFps = 60,
        ),
    )

    /**
     * Head-unit release per model year, matched as a substring of `Build.DISPLAY`. A year with no
     * confirmed release stays blank and simply loses the tie-break to the newest entry sharing its
     * panel, which never changes what the app does because the hardware is identical.
     */
    private val systemVersions = mapOf(
        2021 to "",
        2022 to "",
        2023 to "1.24",
        2024 to "3.21",
        2025 to "",
    )

    /**
     * Assumed capabilities pending a diagnostic report from a real C11: the triple display and the
     * USB host port are hardware facts, while no vehicle-data channel from the app has been shown to
     * work, so that stays off until one is.
     */
    private val capabilities = CarCapabilities(
        clusterDisplay = true,
        copilotDisplay = true,
        vehicleData = false,
        usbHostPort = true,
        wifiDirect = true,
    )

    val all: List<CarProfile> = generations.flatMap { generation ->
        generation.years.flatMap { year ->
            powertrains.map { powertrain ->
                CarProfile(
                    id = "$MODEL_LINE-${year}-$powertrain".lowercase(),
                    oem = OEM,
                    modelLine = MODEL_LINE,
                    modelYear = year,
                    powertrain = powertrain,
                    soc = generation.soc,
                    systemVersion = systemVersions.getValue(year),
                    panel = generation.panel,
                    capabilities = capabilities,
                    recommendedFps = generation.recommendedFps,
                )
            }
        }
    }

    fun byId(id: String): CarProfile? = all.firstOrNull { it.id == id }
}
