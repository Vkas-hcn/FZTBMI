package com.ling.ding.sighed.adcan


import androidx.annotation.Keep
import com.ling.ding.sighed.adtool.ShowDataTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@Keep
class AdScheduler(
    private val adDisplayManager: AdDisplayManager,
    private val adConfigManager: AdConfigManager
) {
    private var job: Job? = null

    fun startScheduler() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (adConfigManager.shouldResetAdFailCount()) {
                    break
                }
                adDisplayManager.showAd()
                delay(getAdInterval())
            }
        }
    }

    private fun getAdInterval(): Long {
        val adminData = ShowDataTool.getAdminData() ?: return 0L
        return adminData.adOperations.scheduling.detection.intervalSec.toLong() * 1000
    }

    fun stopScheduler() {
        job?.cancel()
    }
}