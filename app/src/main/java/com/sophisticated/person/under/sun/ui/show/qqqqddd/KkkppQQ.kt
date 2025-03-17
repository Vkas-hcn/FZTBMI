package com.sophisticated.person.under.sun.ui.show.qqqqddd

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sophisticated.person.under.sun.R
import kotlinx.coroutines.*
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ling.ding.sighed.txtbean.EnvironmentManager
import com.ling.ding.sighed.txtdata.DrinkConfigData
import com.sophisticated.person.under.sun.databinding.ActivityKpBinding
import com.sophisticated.person.under.sun.ui.show.main.MainActivity
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo
import com.sophisticated.person.under.sun.utils.ImageRotationUtil
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.base.bean.TPBaseAd
import com.tradplus.ads.open.splash.SplashAdListener
import com.tradplus.ads.open.splash.TPSplash

class KkkppQQ : AppCompatActivity() {
    private lateinit var binding: ActivityKpBinding
    val rotationUtil = ImageRotationUtil()
    private var showJob: Job? = null
    private var mTPSplash: TPSplash? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback {
        }
        initAd()
    }

    override fun onResume() {
        super.onResume()
        showOpenAd()
    }
    // Usage example
    private fun startProgressBar() {
       rotationUtil.rotateImageSmoothly(
            imageView = binding.imgBar,
            duration = 1000,
            clockwise = true
        )
    }

    private fun toMain() {
        navigateTo<MainActivity>(finishCurrent = true)
    }
    // 初始化广告位
    private fun initAd() {
        startProgressBar()
        if (mTPSplash == null) {
            mTPSplash = TPSplash(this, EnvironmentManager.getCurrentConfig().openid)
            mTPSplash!!.setAdListener(object : SplashAdListener() {
                override fun onAdLoaded(tpAdInfo: TPAdInfo, tpBaseAd: TPBaseAd?) {
                    Log.e("MainActivityFFF", "开屏广告加载成功")
                }

                override fun onAdLoadFailed(tpAdError: TPAdError) {
                    Log.e("MainActivityFFF", "开屏广告加载失败=${tpAdError.errorCode}--${tpAdError.errorMsg}")
                    toMain()
                }

                override fun onAdClosed(tpAdInfo: TPAdInfo) {
                    Log.e("MainActivityFFF", "开屏广告关闭")
                    binding.ceis.removeAllViews()
                    toMain()
                }
            })
        }
        mTPSplash?.loadAd(null)
    }

    private fun showOpenAd() {
        showJob?.cancel()
        showJob = lifecycleScope.launch {
            try {
                withTimeout(10000L) {
                    while (isActive && !mTPSplash?.isReady!!) {
                        delay(500L)
                    }
                    mTPSplash?.showAd(binding.ceis)
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("MainActivityFFF", "开屏广告显示超时")
                toMain()
            }
        }
    }
}