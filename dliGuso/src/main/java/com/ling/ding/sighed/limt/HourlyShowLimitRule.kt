package com.ling.ding.sighed.limt

import com.ling.ding.sighed.netool.AppPointData
import com.ling.ding.sighed.txtdata.LocalStorage
import com.ling.ding.sighed.txtmain.FirstRunFun

class HourlyShowLimitRule(
    private val localStorage: LocalStorage,
    private val hourlyLimit: Int
) : AdLimitRule {

    override fun checkLimit(isCanUp: Boolean): Boolean {
        return (localStorage.hourlyShowCount ?: 0) < hourlyLimit
    }

    override fun reset() {
        localStorage.hourlyShowCount = 0
    }

    override fun recordEvent() {
        localStorage.hourlyShowCount = (localStorage.hourlyShowCount ?: 0) + 1
    }
}

class DailyShowLimitRule(
    private val localStorage: LocalStorage,
    private val dailyLimit: Int
) : AdLimitRule {

    override fun checkLimit(isCanUp: Boolean): Boolean {
        val state = (localStorage.dailyShowCount ?: 0) < dailyLimit
        if (!state && isCanUp) {
            AppPointData.getLiMitData()
        }
        return (localStorage.dailyShowCount ?: 0) < dailyLimit
    }

    override fun reset() {
        localStorage.dailyShowCount = 0
        FirstRunFun.localStorage.getlimit = false
    }

    override fun recordEvent() {
        localStorage.dailyShowCount = (localStorage.dailyShowCount ?: 0) + 1
    }
}

class DailyClickLimitRule(
    private val localStorage: LocalStorage,
    private val dailyClickLimit: Int
) : AdLimitRule {

    override fun checkLimit(isCanUp: Boolean): Boolean {
        val state = (localStorage.clickCount ?: 0) < dailyClickLimit
        if (!state && isCanUp) {
            AppPointData.getLiMitData()
        }
        return (localStorage.clickCount ?: 0) < dailyClickLimit
    }

    override fun reset() {
        localStorage.clickCount = 0
        FirstRunFun.localStorage.getlimit = false
    }

    override fun recordEvent() {
        localStorage.clickCount = (localStorage.clickCount ?: 0) + 1
    }
}
