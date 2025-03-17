package com.ling.ding.sighed.adcan


import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import androidx.annotation.Keep
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.txtmain.FirstRunFun

@Keep
object AdConfigManager {
    var adShowTime: Long = 0

    fun initializeFacebookSdk() {
        val jsonBean = ShowDataTool.getAdminData()
        val placementId = jsonBean?.assetConfig?.identifiers?.facebook?.placementId
        if (placementId.isNullOrBlank()) return

        ShowDataTool.showLog("initializeFacebookSdk: $placementId")
        FacebookSdk.setApplicationId(placementId)
        FacebookSdk.sdkInitialize(FirstRunFun.mainStart)
        AppEventsLogger.activateApp(FirstRunFun.mainStart)
    }

    fun isDeviceLocked(): Boolean {
        val powerManager = FirstRunFun.mainStart.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = FirstRunFun.mainStart.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (powerManager == null || keyguardManager == null) return false

        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }

    fun shouldResetAdFailCount(): Boolean {
        val adminBean = ShowDataTool.getAdminData() ?: return false
        val maxFailNum = adminBean.adOperations.constraints.interactions.errorHandling.maxErrors
        return FirstRunFun.localStorage.isAdFailCount >= maxFailNum
    }
}