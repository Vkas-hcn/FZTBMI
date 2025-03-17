package com.ling.ding.sighed.adcan

import androidx.annotation.Keep

@Keep
class AdLimiter {
    private var clickCount = 0
    private var showCount = 0

    fun recordClick() {
        clickCount++
    }

    fun recordShow() {
        showCount++
    }

    fun canShowAd(checkFrequency: Boolean = false): Boolean {
        // 根据业务逻辑实现广告展示的频率限制
        return true // 示例返回值
    }
}