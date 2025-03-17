package com.ling.ding.sighed.adcan


import androidx.annotation.Keep
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial
@Keep
class AdLoader(private val adConfigManager: AdConfigManager) {
    private var interstitialAd: TPInterstitial? = null
    private var lastAdLoadTime: Long = 0
    private var isLoading = false

    fun initializeAd() {
        val idBean = ShowDataTool.getAdminData() ?: return
        val campaignId = idBean.assetConfig.identifiers.campaignId
        interstitialAd = TPInterstitial(FirstRunFun.mainStart, campaignId)
        interstitialAd?.setAdListener(object : InterstitialAdListener {
            override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                lastAdLoadTime = System.currentTimeMillis()
                isLoading = false
            }

            override fun onAdFailed(tpAdError: TPAdError) {
                isLoading = false
            }

            override fun onAdClicked(tpAdInfo: TPAdInfo) {}
            override fun onAdImpression(tpAdInfo: TPAdInfo) {}
            override fun onAdClosed(tpAdInfo: TPAdInfo) {}
            override fun onAdVideoError(tpAdInfo: TPAdInfo, tpAdError: TPAdError) {}
            override fun onAdVideoStart(tpAdInfo: TPAdInfo) {}
            override fun onAdVideoEnd(tpAdInfo: TPAdInfo) {}
        })
    }

    fun loadAd() {
        if (isLoading) return
        isLoading = true
        interstitialAd?.loadAd()
    }
}