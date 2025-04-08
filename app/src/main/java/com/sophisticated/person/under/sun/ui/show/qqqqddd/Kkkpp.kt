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
import com.sophisticated.person.under.sun.databinding.ActivityKpBinding
import com.sophisticated.person.under.sun.ui.show.main.MainActivity
import com.sophisticated.person.under.sun.utils.BmiUtils.navigateTo
import com.sophisticated.person.under.sun.utils.ImageRotationUtil
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.base.bean.TPBaseAd
import com.tradplus.ads.open.splash.SplashAdListener
import com.tradplus.ads.open.splash.TPSplash

class Kkkpp : AppCompatActivity() {

    private lateinit var binding: ActivityKpBinding
    private val rotationUtil = ImageRotationUtil()
    private var showJob: Job? = null
    private var splashAd: TPSplash? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityKpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()

        setupBackPressed()

        initAdComponents()
    }

    override fun onResume() {
        super.onResume()
        showSplashAd()
    }


    private fun initAdComponents() {
        splashAd = createSplashAd()
        startProgressBar()
        splashAd?.loadAd(null)
    }

    private fun createSplashAd(): TPSplash {
        val adConfig = EnvironmentManager.getCurrentConfig()
        return TPSplash(this, adConfig.openid)
            .apply {
                setAdListener(object : SplashAdListener() {
                    override fun onAdLoaded(tpAdInfo: TPAdInfo, tpBaseAd: TPBaseAd?) {
                        Log.d("AdStatus", "Splash loaded successfully")
                    }

                    override fun onAdLoadFailed(tpAdError: TPAdError) {
                        handleAdFailureOrClose()
                    }

                    override fun onAdClosed(tpAdInfo: TPAdInfo) {
                        handleAdFailureOrClose()
                    }
                })
            }
    }

    private fun showSplashAd() {
        showJob?.cancel()
        showJob = lifecycleScope.launch {
            try {
                withTimeout(10_000) {
                    while (!splashAd?.isReady!!) delay(500L)
                    splashAd?.showAd(binding.ceis)
                }
            } catch (e: TimeoutCancellationException) {
                Log.w("AdTimeout", "Splash display timeout")
                handleAdFailureOrClose()
            }
        }
    }

    private fun startProgressBar() {
        rotationUtil.rotateImageSmoothly(
            imageView = binding.imgBar,
            duration = 1000,
            clockwise = true
        )
    }

    private fun handleAdFailureOrClose() {
        binding.ceis.removeAllViews()
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        navigateTo<MainActivity>(finishCurrent = true)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this) {}
    }
}
