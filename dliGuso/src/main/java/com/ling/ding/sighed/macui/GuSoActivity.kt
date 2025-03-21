package com.ling.ding.sighed.macui

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.txtmain.FirstRunFun.adShowFun
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.adtool.TranplusConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


/**
 * 广告页面活动
 * 负责管理弹出式广告的展示流程
 */
class GuSoActivity : AppCompatActivity() {

    private lateinit var adDisplayJob: Job
    private var adDelayDuration: Long = 0L
    private var isAdReady: Boolean = false

    // 时间常量
    private companion object {
        private const val AD_MONITORING_TIMEOUT_MS = 30000L
        private const val DEFAULT_MIN_DELAY_MS = 2000L
        private const val DEFAULT_MAX_DELAY_MS = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logMessage("广告页面创建")

        initializeAdPage()
    }

    /**
     * 初始化广告页面
     */
    private fun initializeAdPage() {
        // 初始化H5容器
//        HFiveMain.hfAcc(this)

        // 重置广告失败计数
        FirstRunFun.localStorage.isAdFailCount = 0

        // 准备广告内容
        prepareAdvertisement()
    }

    /**
     * 准备广告展示
     */
    private fun prepareAdvertisement() {
        // 记录首次外部弹窗事件
//        firstExternalBombPoint()
//
        // 生成随机延迟时间
        adDelayDuration = calculateRandomDelay()

        // 记录广告启动事件
        TtPoint.postPointData(false, "starup", "time", adDelayDuration / 1000)

        // 检查广告是否准备好
        checkAdAvailability()
    }

    /**
     * 检查广告是否可用
     */
    private fun checkAdAvailability() {
        isAdReady = adShowFun.getAdInstatnce().isReady

        if (isAdReady) {
            // 广告准备好，继续展示流程
            scheduleAdDisplay()
        } else {
            // 广告未准备好，结束活动
            logMessage("广告未准备好，结束活动")
            finish()
        }
    }

    /**
     * 计算广告展示随机延迟时间
     *
     * @return 延迟时间（毫秒）
     */
    private fun calculateRandomDelay(): Long {
        try {
            val adminData = ShowDataTool.getAdminData() ?: throw Exception("管理数据为空")

            // 获取延迟设置范围
            val delaySettings =
                adminData.adOperations.scheduling.display.delaySettings.randomDelayMs
            val minDelay = delaySettings.min.toLong()
            val maxDelay = delaySettings.max.toLong()

            val randomDelay = Random.nextLong(minDelay, maxDelay + 1)
            logMessage("随机延迟时间: $randomDelay ms")

            return randomDelay

        } catch (e: Exception) {
            // 出现异常时使用默认延迟范围
            logMessage("使用默认延迟范围: $DEFAULT_MIN_DELAY_MS - $DEFAULT_MAX_DELAY_MS ms")
            return Random.nextLong(DEFAULT_MIN_DELAY_MS, DEFAULT_MAX_DELAY_MS)
        }
    }

    /**
     * 安排广告展示任务
     */
    private fun scheduleAdDisplay() {
        logMessage("广告准备就绪，延迟展示: $adDelayDuration ms")
        TtPoint.postPointData(false, "isready")

        adDisplayJob = lifecycleScope.launch {
            // 等待指定的延迟时间
            delay(adDelayDuration)

            // 记录延迟结束事件
            TtPoint.postPointData(false, "delaytime", "time", adDelayDuration / 1000)

            // 更新广告展示时间
            updateAdTimestamps()

            // 展示广告
            displayAdvertisement()

            // 监控广告展示
            monitorAdDisplay()
        }
    }

    /**
     * 更新广告展示时间戳
     */
    private fun updateAdTimestamps() {
        val currentTime = System.currentTimeMillis()
        TranplusConfig.showAdTime = currentTime
        TranplusConfig.adShowTime = currentTime
        logMessage("更新广告时间戳: $currentTime")
    }

    /**
     * 展示广告
     */
    private fun displayAdvertisement() {
        logMessage("开始展示广告")
        adShowFun.getAdInstatnce().showAd(this@GuSoActivity, "show")
    }

    /**
     * 监控广告展示状态
     */
    private suspend fun monitorAdDisplay() {
        // 设置监控超时
        delay(AD_MONITORING_TIMEOUT_MS)

        // 检查广告是否已展示
        if (TranplusConfig.showAdTime > 0) {
            TtPoint.postPointData(false, "show", "t", "30")
            TranplusConfig.showAdTime = 0
            logMessage("广告展示监控完成 (30秒)")
        }
    }

    /**
     * 记录日志
     *
     * @param message 日志消息
     */
    private fun logMessage(message: String) {
        ShowDataTool.showLog("[GuSoActivity] $message")
    }

    override fun onDestroy() {
        (this.window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }
}