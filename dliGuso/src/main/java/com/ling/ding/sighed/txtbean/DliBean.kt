package com.ling.ding.sighed.txtbean

import androidx.annotation.Keep

@Keep
data class DliBean(
    val adOperations: AdOperations,
    val assetConfig: AssetConfig,
    val userManagement: UserManagement
)

data class AdOperations(
    val constraints: Constraints,
    val scheduling: Scheduling
)

data class AssetConfig(
    val identifiers: Identifiers
)

data class UserManagement(
    val profile: Profile
)

data class Constraints(
    val impressions: Impressions,
    val interactions: Interactions
)

data class Scheduling(
    val detection: Detection,
    val display: Display
)

data class Impressions(
    val daily: Int,
    val hourly: Int
)

data class Interactions(
    val clicks: Clicks,
    val errorHandling: ErrorHandling
)

data class Clicks(
    val daily: Int
)

data class ErrorHandling(
    val maxErrors: Int
)

data class Detection(
    val initialDelaySec: Int,
    val intervalSec: Int
)

data class Display(
    val delaySettings: DelaySettings,
    val frequencySec: Int
)

data class DelaySettings(
    val randomDelayMs: RandomDelayMs
)

data class RandomDelayMs(
    val max: Int,
    val min: Int
)

data class Identifiers(
    val campaignId: String,
    val facebook: Facebook
)

data class Facebook(
    val placementId: String
)

data class Profile(
    val classification: String,
    val privileges: Privileges
)

data class Privileges(
    val upload: Upload
)

data class Upload(
    val enabled: Int
)


