package com.ling.ding.sighed.uican

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
import com.ling.ding.sighed.netool.AppPointData.firstExternalBombPoint
import com.ling.ding.sighed.zmain.HFiveMain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class UiCanActivity : AppCompatActivity() {

    private lateinit var activityContext: Context
    private var adDelayDuration: Long = 0L
    private var isAdReady: Boolean = false
    private var adShowTime: Long = 0L
    private var showAdTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContext = this
        log("外弹页面展示")
        initializeActivity()
    }

    private fun initializeActivity() {
//        HFiveMain.hfAcc(activityContext)
        FirstRunFun.localStorage.isAdFailCount = 0
        handleNonH5Content()
    }



    private fun handleNonH5Content() {
        firstExternalBombPoint()
        TtPoint.postPointData(false, "starup", "time", adDelayDuration / 1000)
        // 检查广告是否准备好
        isAdReady = adShowFun.interstitialAd.isReady
        if (isAdReady) {
            handleAdReadyState()
        } else {
            finish()
        }
    }

    private fun handleAdReadyState() {
        adDelayDuration = generateRandomDelay()
        log("Advertisement display delay duration: $adDelayDuration")
        TtPoint.postPointData(false, "isready")
        lifecycleScope.launch {
            delay(adDelayDuration)
            TtPoint.postPointData(false, "delaytime", "time", adDelayDuration / 1000)

            setAdShowTimes()
            adShowFun.interstitialAd.showAd(this@UiCanActivity,"ces")

            delay(30000) // 等待 30 秒
            if (showAdTime > 0) {
                TtPoint.postPointData(false, "show", "t", "30")
                showAdTime = 0
            }
        }
    }

    private fun setAdShowTimes() {
        val currentTime = System.currentTimeMillis()
        adShowTime = currentTime
        showAdTime = currentTime
        TranplusConfig.showAdTime = currentTime
        TranplusConfig.adShowTime = currentTime
    }

    private fun generateRandomDelay(): Long {
        try {
            val adminData = ShowDataTool.getAdminData()
            val delayRange = adminData?.adOperations?.scheduling?.display?.delaySettings?.randomDelayMs
            val minDelay = delayRange?.min?.toLong() ?: 0L
            val maxDelay = delayRange?.max?.toLong() ?: 0L
            return Random.nextLong(minDelay, maxDelay + 1)
        } catch (e: Exception) {
            return Random.nextLong(2000, 3000)
        }
    }

    override fun onDestroy() {
        (window.decorView as ViewGroup).removeAllViews()
        super.onDestroy()
    }

    private fun log(message: String) {
        ShowDataTool.showLog(message)
    }

}