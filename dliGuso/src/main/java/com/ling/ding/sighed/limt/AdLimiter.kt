package com.ling.ding.sighed.limt

import android.util.Log
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.AppPointData
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtmain.FirstRunFun.localStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class AdLimiter {
    private val timeManager = TimeManager()

    // 广告展示限制检查（每小时/每天）
    fun canShowAd(isCanUp: Boolean = false): Boolean {
//        val jsonBean = ShowDataTool.getAdminData() ?: return false
//        val hourlyShowLimit = jsonBean.adOperations.constraints.impressions.hourly
//        val dailyShowLimit = jsonBean.adOperations.constraints.impressions.daily
//        val dailyClickLimit = jsonBean.adOperations.constraints.interactions.clicks.daily

        val hourlyShowLimit = 3
        val dailyShowLimit = 5
        val dailyClickLimit = 3

        timeManager.updateTime()

        val hourlyShow = localStorage.hourlyShowCount ?: 0
        val dailyShow = localStorage.dailyShowCount ?: 0
        val clickCount = localStorage.clickCount ?: 0

        val isHourlyLimitExceeded = hourlyShow >= hourlyShowLimit
        val isDailyLimitExceeded = dailyShow >= dailyShowLimit
        val isClickLimitExceeded = clickCount >= dailyClickLimit

        if (isDailyLimitExceeded) {
            Log.e("TAG", "ad: 天展示超限")
            if (isCanUp) {
                TtPoint.postPointData(false, "ispass", "string", "dayShowLimit")
                AppPointData.getLiMitData()
            }
            return false
        }

        if (isClickLimitExceeded) {
            Log.e("TAG", "ad: 天点击超限")
            if (isCanUp) {
                TtPoint.postPointData(false, "ispass", "string", "dayClickLimit")
                AppPointData.getLiMitData()
            }
            return false
        }

        if (isHourlyLimitExceeded) {
            Log.e("TAG", "ad: 小时展示超限")
            if (isCanUp) {
                TtPoint.postPointData(false, "ispass", "string", "hourShowLimit")
            }
            return false
        }

        return true
    }

    // 记录广告展示
    fun recordShow() {
        timeManager.updateTime()
        localStorage.hourlyShowCount = (localStorage.hourlyShowCount ?: 0) + 1
        localStorage.dailyShowCount = (localStorage.dailyShowCount ?: 0) + 1
    }

    // 记录广告点击
    fun recordClick() {
        localStorage.clickCount = (localStorage.clickCount ?: 0) + 1
    }

    private class TimeManager {
        private val currentDateFormatter = DateTimeFormatter.BASIC_ISO_DATE
        private val currentTimeFormatter = DateTimeFormatter.ofPattern("HH")

        private var lastCheckedDate: String = getCurrentDate()
        private var lastCheckedHour: String = getCurrentHour()

        fun updateTime() {
            val currentDate = getCurrentDate()
            val currentHour = getCurrentHour()

            if (lastCheckedDate != currentDate) {
                resetDailyData()
                lastCheckedDate = currentDate
            }

            if (lastCheckedHour != currentHour) {
                resetHourlyData()
                lastCheckedHour = currentHour
            }
        }

        private fun resetDailyData() {
            localStorage.dailyShowCount = 0
            localStorage.clickCount = 0
            localStorage.hourlyShowCount = 0
        }

        private fun resetHourlyData() {
            localStorage.hourlyShowCount = 0
        }

        private fun getCurrentDate(): String {
            return LocalDate.now().format(currentDateFormatter)
        }

        private fun getCurrentHour(): String {
            return LocalTime.now().format(currentTimeFormatter)
        }
    }
}


