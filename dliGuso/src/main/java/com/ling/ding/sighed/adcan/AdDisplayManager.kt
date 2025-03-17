package com.ling.ding.sighed.adcan


import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@Keep
class AdDisplayManager(
    private val adLoader: AdLoader,
    private val adLimiter: AdLimiter,
    private val adConfigManager: AdConfigManager
) {
    fun showAd() {
        if (!adLimiter.canShowAd()) return
        if (adConfigManager.isDeviceLocked()) return

        CoroutineScope(Dispatchers.Main).launch {
            adLoader.loadAd()
        }
    }
}