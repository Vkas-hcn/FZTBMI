package com.ling.ding.sighed.adtool

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart

object TranplusConfig {
    var adShowTime: Long = 0
    var showAdTime: Long = 0



    fun getFBInfOr() {
        val jsonBean = ShowDataTool.getAdminData()
        val data = jsonBean?.assetConfig?.identifiers?.facebook?.placementId
        if (data.isNullOrBlank()) {
            return
        }
        ShowDataTool.showLog("getFBInfOr: ${data}")
        FacebookSdk.setApplicationId(data)
        FacebookSdk.sdkInitialize(mainStart)
        AppEventsLogger.activateApp(mainStart)
    }

    fun canShowLocked(): Boolean {
        val powerManager = mainStart.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager =
            mainStart.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (powerManager == null || keyguardManager == null) {
            return false
        }
        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }


    fun adNumAndPoint(): Boolean {
        val adminBean = ShowDataTool.getAdminData()
        if (adminBean == null) {
            ShowDataTool.showLog("AdminBean is null, cannot determine adNumAndPoint")
            return false
        }
        // 从配置中获取最大失败次数
        val maxFailNum = adminBean.adOperations.constraints.interactions.errorHandling.maxErrors
        // 如果失败次数超过最大限制且需要重置
        ShowDataTool.showLog("maxFailNum=${maxFailNum}----startApp.localStorage.isAdFailCount=${FirstRunFun.localStorage.isAdFailCount}")

        if (FirstRunFun.localStorage.isAdFailCount >= maxFailNum) {
            ShowDataTool.showLog("Ad failure count has exceeded the limit, resetting...")
            return true
        }
        return false
    }
}