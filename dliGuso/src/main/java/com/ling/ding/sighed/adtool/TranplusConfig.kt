package com.ling.ding.sighed.adtool

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart



/**
 * 广告配置管理类
 * 负责Facebook SDK初始化、设备状态检查和广告限制管理
 */
object TranplusConfig {
    // 广告展示相关时间记录
    var adShowTime: Long = 0
    var showAdTime: Long = 0

    /**
     * 初始化Facebook SDK
     */
    fun initializeFacebookSDK() {
        val jsonBean = ShowDataTool.getAdminData()
        val facebookPlacementId = jsonBean?.assetConfig?.identifiers?.facebook?.placementId

        if (facebookPlacementId.isNullOrBlank()) {
            ShowDataTool.showLog("Facebook初始化失败: PlacementID为空")
            return
        }

        ShowDataTool.showLog("Facebook初始化: ID=${facebookPlacementId}")

        try {
            FacebookSdk.setApplicationId(facebookPlacementId)
            FacebookSdk.sdkInitialize(mainStart)
            AppEventsLogger.activateApp(mainStart)
        } catch (e: Exception) {
            ShowDataTool.showLog("Facebook初始化异常: ${e.message}")
        }
    }

    /**
     * 检查设备是否处于锁屏或息屏状态
     *
     * @return true 如果处于锁屏或息屏状态，否则返回false
     */
    fun isDeviceLocked(): Boolean {
        val powerManager = mainStart.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val keyguardManager = mainStart.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager

        if (powerManager == null || keyguardManager == null) {
            ShowDataTool.showLog("系统服务获取失败，默认设备状态为锁定")
            return true
        }

        val isScreenOn = powerManager.isInteractive
        val isInKeyguardRestrictedInputMode = keyguardManager.inKeyguardRestrictedInputMode()

        return !isScreenOn || isInKeyguardRestrictedInputMode
    }

    /**
     * 检查广告失败次数是否超过配置的最大值
     *
     * @return true 如果广告失败次数已达上限，否则返回false
     */
    fun hasReachedAdFailureLimit(): Boolean {
        val adminBean = ShowDataTool.getAdminData()

        if (adminBean == null) {
            ShowDataTool.showLog("配置数据为空，无法检查广告失败次数限制")
            return false
        }

        val maxFailNum = adminBean.adOperations.constraints.interactions.errorHandling.maxErrors
        val currentFailCount = FirstRunFun.localStorage.isAdFailCount

        ShowDataTool.showLog("广告失败检查: 最大失败次数=${maxFailNum}, 当前失败次数=${currentFailCount}")

        return currentFailCount >= maxFailNum
    }
}