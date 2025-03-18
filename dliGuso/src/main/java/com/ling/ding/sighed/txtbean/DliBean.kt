package com.ling.ding.sighed.txtbean

import androidx.annotation.Keep

@Keep
data class DliBean(
    val adOperations: AdOperations,
    val assetConfig: AssetConfig,
    val userManagement: UserManagement
)

@Keep
data class AdOperations(
    val constraints: Constraints,
    val scheduling: Scheduling
)

@Keep
data class AssetConfig(
    val identifiers: Identifiers
)

@Keep
data class UserManagement(
    val profile: Profile
)

@Keep
data class Constraints(
    val impressions: Impressions,
    val interactions: Interactions
)

@Keep
data class Scheduling(
    val detection: Detection,
    val display: Display
)

@Keep
data class Impressions(
    val daily: Int,
    val hourly: Int
)

@Keep
data class Interactions(
    val clicks: Clicks,
    val errorHandling: ErrorHandling
)

@Keep
data class Clicks(
    val daily: Int
)

@Keep
data class ErrorHandling(
    val maxErrors: Int
)

@Keep
data class Detection(
    val initialDelaySec: Int,
    val intervalSec: Int
)

@Keep
data class Display(
    val delaySettings: DelaySettings,
    val frequencySec: Int
)

@Keep
data class DelaySettings(
    val randomDelayMs: RandomDelayMs
)

@Keep
data class RandomDelayMs(
    val max: Int,
    val min: Int
)

@Keep
data class Identifiers(
    val campaignId: String,
    val facebook: Facebook,
    val linkData:String
)

@Keep
data class Facebook(
    val placementId: String
)

@Keep
data class Profile(
    val classification: String,
    val privileges: Privileges
)

@Keep
data class Privileges(
    val upload: Upload
)

@Keep
data class Upload(
    val enabled: Int
)


