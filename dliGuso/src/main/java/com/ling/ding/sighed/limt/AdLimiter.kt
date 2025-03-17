package com.ling.ding.sighed.limt

import android.util.Log
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.AppPointData
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtdata.LocalStorage
import com.ling.ding.sighed.txtmain.FirstRunFun.localStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class AdLimiter(
    private val localStorage: LocalStorage,
    private val timeManager: TimeManager,
    private val rules: List<AdLimitRule>
) {

    fun canShowAd(isCanUp: Boolean = false): Boolean {
        timeManager.updateTime()

        val isLimitExceeded = rules.any { !it.checkLimit(isCanUp) }
        if (isLimitExceeded && isCanUp) {
            // 触发限制事件
            TtPoint.postPointData(false, "ispass", "string", "limitExceeded")
        }

        return !isLimitExceeded
    }

    fun recordShow() {
        timeManager.updateTime()
        rules.filterIsInstance<HourlyShowLimitRule>().forEach { it.recordEvent() }
        rules.filterIsInstance<DailyShowLimitRule>().forEach { it.recordEvent() }
    }

    fun recordClick() {
        rules.filterIsInstance<DailyClickLimitRule>().forEach { it.recordEvent() }
    }
}



