package com.ling.ding.sighed.adtool

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.ling.ding.sighed.limt.AdLimiter
import com.ling.ding.sighed.limt.DailyClickLimitRule
import com.ling.ding.sighed.limt.DailyShowLimitRule
import com.ling.ding.sighed.limt.HourlyShowLimitRule
import com.ling.ding.sighed.limt.TimeManager
import com.ling.ding.sighed.netool.AppPointData
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtdata.DrinkConfigData
import com.ling.ding.sighed.txtdata.DrinkStartApp
import com.ling.ding.sighed.txtdata.LocalStorage
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.txtmain.FirstRunFun.localStorage
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import com.ling.ding.sighed.txtmain.IntetNetSHow
import com.ling.ding.sighed.zmain.TWMain
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TranplusUtils {
    // 异步任务
    private var adMonitoringJob: Job? = null

    // 广告频率限制器
    private lateinit var adLimiter : AdLimiter

    // 广告对象
    private lateinit var interstitialAd: TPInterstitial

    // 状态标记
    private var isLoading = false
    private var isAdDataAvailable = false
    private var wasAdClicked = false

    // 配置常量
    private companion object {
        // 广告缓存有效期：50分钟
        private const val AD_CACHE_DURATION_MS = 50 * 60 * 1000L

        // 广告加载超时：60秒
        private const val AD_LOAD_TIMEOUT_MS = 60 * 1000L

        // 活动关闭延迟
        private const val ACTIVITY_CLOSE_DELAY_MS = 1011L
    }

    // 上次广告加载时间
    private var lastAdLoadTime: Long = 0

    fun getAdLimiter(): AdLimiter {
        val localStorage = LocalStorage(mainStart)
        val beanAllData = ShowDataTool.getAdminData()

        val rules = listOf(
            HourlyShowLimitRule(localStorage, beanAllData?.adOperations?.constraints?.impressions?.hourly?:0),
            DailyShowLimitRule(localStorage, beanAllData?.adOperations?.constraints?.impressions?.daily?:0),
            DailyClickLimitRule(localStorage, beanAllData?.adOperations?.constraints?.interactions?.clicks?.daily?:0)
        )
        val timeManager = TimeManager(localStorage, rules)
         adLimiter = AdLimiter(localStorage, timeManager, rules)
        return adLimiter
    }
    /**
     * 初始化并启动广告监控
     */
    fun startAdMonitoring() {
        getAdLimiter()
        // 初始化Facebook SDK
        TranplusConfig.initializeFacebookSDK()

        // 初始化广告SDK
        initializeInterstitialAd()

        // 检查广告失败次数限制
        if (TranplusConfig.hasReachedAdFailureLimit()) {
            ShowDataTool.showLog("广告失败次数已达上限，不启动广告监控")
            return
        }

        // 获取配置的检测间隔
        val adminData = ShowDataTool.getAdminData() ?: return
        val checkIntervalMs = adminData.adOperations.scheduling.detection.intervalSec.toLong() * 1000L

        ShowDataTool.showLog("启动广告监控: 检测间隔=${checkIntervalMs}ms")

        // 启动监控任务
        adMonitoringJob = CoroutineScope(Dispatchers.Main).launch {
            // 等待应用完全启动
            waitForAppStartup()

            // 开始定期检查广告展示条件
            monitorAdConditions(checkIntervalMs)
        }
    }

    /**
     * 等待应用启动完成
     */
    private suspend fun waitForAppStartup() {
        while (true) {
            val activities = IntetNetSHow.getActivity()

            // 检查当前活动是否是目标启动界面
            if (activities.isEmpty() ||
                (activities.last().javaClass.name != DrinkConfigData.startPack2)) {

                if (activities.isEmpty()) {
                    ShowDataTool.showLog("活动栈为空，应用可能未完全启动")
                } else {
                    ShowDataTool.showLog("当前活动: ${activities.last().javaClass.name}")
                }
                ShowDataTool.showLog("替换图标")
                TWMain.dliGuso(869784)
//                FirstRunFun.disableComponent(mainStart,"com.sophisticated.person.under.sun.ui.show.qqqqddd.Kkkpp")
                break
            }

            delay(500)
        }
    }

    /**
     * 定期监控广告展示条件
     */
    private suspend fun monitorAdConditions(checkIntervalMs: Long) {
        while (true) {
            ShowDataTool.showLog("广告条件检测循环")
            TtPoint.postPointData(false, "timertask")

            // 检查广告失败次数是否已达上限
            if (TranplusConfig.hasReachedAdFailureLimit()) {
                TtPoint.postPointData(false, "jumpfail")
                adMonitoringJob?.cancel()
                break
            }

            // 加载广告并检查是否可以展示
            loadAd()
            checkAndShowAdIfPossible()

            // 等待指定的检测间隔
            delay(checkIntervalMs)
        }
    }

    /**
     * 初始化插屏广告及其监听器
     */
    private fun initializeInterstitialAd() {
        val adConfig = ShowDataTool.getAdminData() ?: return
        val campaignId = adConfig.assetConfig.identifiers.campaignId

        ShowDataTool.showLog("初始化插屏广告: ID=${campaignId}")

        interstitialAd = TPInterstitial(mainStart, campaignId)

        // 设置广告事件监听器
        interstitialAd.setAdListener(createAdListener())
    }

    fun getAdInstatnce(): TPInterstitial {
        return interstitialAd
    }

    /**
     * 创建广告事件监听器
     */
    private fun createAdListener(): InterstitialAdListener {
        return object : InterstitialAdListener {
            // 广告加载成功
            override fun onAdLoaded(adInfo: TPAdInfo) {
                ShowDataTool.showLog("插屏广告加载成功")
                lastAdLoadTime = System.currentTimeMillis()
                TtPoint.postPointData(false, "getadvertise")
                isAdDataAvailable = true
            }

            // 广告被点击
            override fun onAdClicked(adInfo: TPAdInfo) {
                ShowDataTool.showLog("插屏广告被点击: eCPM=${adInfo.ecpm}")
                adLimiter.recordClick()
                wasAdClicked = true
            }

            // 广告展示
            override fun onAdImpression(adInfo: TPAdInfo) {
                wasAdClicked = false
                ShowDataTool.showLog("插屏广告展示: eCPM=${adInfo.ecpm}")
                adLimiter.recordShow()
                resetAdStatus()
                adInfo.let { TtPoint.postAdData(it) }
                AppPointData.showSuccessPoint()
                isAdDataAvailable = false
            }

            // 广告加载失败
            override fun onAdFailed(error: TPAdError) {
                ShowDataTool.showLog("插屏广告加载失败: ${error.errorMsg}")
                resetAdStatus()
                TtPoint.postPointData(
                    false,
                    "getfail",
                    "string1",
                    error.errorMsg
                )
                isAdDataAvailable = false
            }

            // 广告被关闭
            override fun onAdClosed(adInfo: TPAdInfo) {
                ShowDataTool.showLog("插屏广告被关闭: eCPM=${adInfo.ecpm}")
                DrinkStartApp.closeAllActivities()
            }

            // 广告视频播放错误
            override fun onAdVideoError(adInfo: TPAdInfo, error: TPAdError) {
                resetAdStatus()
                ShowDataTool.showLog("插屏广告视频播放错误: ${error.errorMsg}")
                TtPoint.postPointData(
                    false,
                    "showfailer",
                    "string3",
                    error.errorMsg
                )
            }

            // 视频开始播放
            override fun onAdVideoStart(adInfo: TPAdInfo) {
                // 可以添加视频开始的处理逻辑
            }

            // 视频播放结束
            override fun onAdVideoEnd(adInfo: TPAdInfo) {
                // 可以添加视频结束的处理逻辑
            }
        }
    }

    /**
     * 加载广告
     */
    private fun loadAd() {
        // 检查广告展示频率限制
        if (!adLimiter.canShowAd()) {
            ShowDataTool.showLog("广告频率受限，跳过加载")
            return
        }

        val currentTime = System.currentTimeMillis()

        // 检查是否有可用的缓存广告
        if (isAdDataAvailable && (currentTime - lastAdLoadTime < AD_CACHE_DURATION_MS)) {
            ShowDataTool.showLog("使用缓存广告，无需重新加载")
            return
        }

        // 检查是否需要重置广告状态
        if (currentTime - lastAdLoadTime >= AD_CACHE_DURATION_MS) {
            resetAdStatus()
        }

        // 检查是否正在加载广告
        if (isLoading) {
            ShowDataTool.showLog("广告正在加载中，请等待")
            return
        }

        // 开始加载广告
        isLoading = true
        ShowDataTool.showLog("开始加载新广告")
        interstitialAd.loadAd()
        TtPoint.postPointData(false, "reqadvertise")

        // 设置加载超时处理
        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoading && !isAdDataAvailable) {
                ShowDataTool.showLog("广告加载超时，尝试重新加载")
                loadAd()
            }
        }, AD_LOAD_TIMEOUT_MS)
    }

    /**
     * 重置广告状态
     */
    fun resetAdStatus() {
        isLoading = false
        lastAdLoadTime = 0
        isAdDataAvailable = false
    }

    /**
     * 检查并在条件满足时展示广告
     */
    private fun checkAndShowAdIfPossible() {
        // 检查设备是否处于锁屏或息屏状态
        if (TranplusConfig.isDeviceLocked()) {
            ShowDataTool.showLog("设备处于锁屏或息屏状态，不展示广告")
            return
        }

        // 发送设备解锁状态事件
        TtPoint.postPointData(false, "isunlock")

        // 获取广告配置
        val adConfig = ShowDataTool.getAdminData() ?: return

        // 检查安装时间
        val installDuration = DrinkStartApp.getInstallDurationSeconds()
        val initialDelay = adConfig.adOperations.scheduling.detection.initialDelaySec
        val adFrequency = adConfig.adOperations.scheduling.display.frequencySec

        // 检查是否满足首次安装延迟要求
        if (isBeforeInitialDelay(installDuration, initialDelay)) {
            return
        }

        // 检查广告展示间隔
        if (isAdFrequencyTooShort(adFrequency)) {
            return
        }

        // 检查广告展示频率限制
        if (!adLimiter.canShowAd(true)) {
            ShowDataTool.showLog("广告展示频率受限")
            return
        }

        ShowDataTool.showLog("准备展示广告")
        showAdAndTrackEvent()
    }

    /**
     * 检查是否在初始安装延迟期内
     */
    private fun isBeforeInitialDelay(installDuration: Long, initialDelay: Int): Boolean {
        try {
            if (installDuration < initialDelay) {
                ShowDataTool.showLog("安装时间不足${initialDelay}秒，不展示广告")
                TtPoint.postPointData(false, "ispass", "string", "firstInstallation")
                return true
            }
        } catch (e: Exception) {
            ShowDataTool.showLog("检查安装时间异常: ${e.message}")
        }
        return false
    }

    /**
     * 检查广告展示间隔是否过短
     */
    private fun isAdFrequencyTooShort(minInterval: Int): Boolean {
        try {
            val timeSinceLastAd = (System.currentTimeMillis() - TranplusConfig.adShowTime) / 1000
            if (timeSinceLastAd < minInterval) {
                ShowDataTool.showLog("距上次广告展示不足${minInterval}秒，不展示广告")
                TtPoint.postPointData(false, "ispass", "string", "Interval")
                return true
            }
        } catch (e: Exception) {
            ShowDataTool.showLog("检查广告间隔异常: ${e.message}")
        }
        return false
    }

    /**
     * 展示广告并跟踪事件
     */
    private fun showAdAndTrackEvent() {
        TtPoint.postPointData(false, "ispass", "string", "")

        CoroutineScope(Dispatchers.Main).launch {
            // 关闭所有活动
            DrinkStartApp.closeAllActivities()

            // 延迟一段时间再继续
            delay(ACTIVITY_CLOSE_DELAY_MS)

            // 记录广告失败次数
            incrementAdFailureCount()
            TWMain.dliGuso(120388)
            // 发送广告开始事件
            TtPoint.postPointData(false, "callstart")
        }
    }

    /**
     * 增加广告失败计数
     */
    private fun incrementAdFailureCount() {
        val currentFailCount = localStorage.isAdFailCount
        localStorage.isAdFailCount = currentFailCount + 1
        ShowDataTool.showLog("跳转体外计数增加: ${currentFailCount + 1}")
    }
}