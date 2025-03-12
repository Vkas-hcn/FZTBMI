package com.ling.ding.sighed.txtdata

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep


@Keep
class LocalStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var firstPoint: Boolean
        get() = sharedPreferences.getBoolean("firstPoint", false)
        set(value) = sharedPreferences.edit().putBoolean("firstPoint", value).apply()

    var adOrgPoint: Boolean
        get() = sharedPreferences.getBoolean("adOrgPoint", false)
        set(value) = sharedPreferences.edit().putBoolean("adOrgPoint", value).apply()

    var getlimit: Boolean
        get() = sharedPreferences.getBoolean("getlimit", false)
        set(value) = sharedPreferences.edit().putBoolean("getlimit", value).apply()

    var fcmState: Boolean
        get() = sharedPreferences.getBoolean("fcmState", false)
        set(value) = sharedPreferences.edit().putBoolean("fcmState", value).apply()

    var admindata: String
        get() = sharedPreferences.getString("admindata", "") ?: ""
        set(value) = sharedPreferences.edit().putString("admindata", value).apply()

    var refdata: String
        get() = sharedPreferences.getString("refdata", "") ?: ""
        set(value) = sharedPreferences.edit().putString("refdata", value).apply()

    var appiddata: String
        get() = sharedPreferences.getString("appiddata", "") ?: ""
        set(value) = sharedPreferences.edit().putString("appiddata", value).apply()

    var IS_INT_JSON: String
        get() = sharedPreferences.getString("IS_INT_JSON", "") ?: ""
        set(value) = sharedPreferences.edit().putString("IS_INT_JSON", value).apply()

    var isAdFailCount: Int
        get() = sharedPreferences.getInt("isAdFailCount", 0)
        set(value) = sharedPreferences.edit().putInt("isAdFailCount", value).apply()

    var lastReportTime: Long
        get() = sharedPreferences.getLong("lastReportTime", 0L)
        set(value) = sharedPreferences.edit().putLong("lastReportTime", value).apply()

    var hourlyShowCount: Int
        get() = sharedPreferences.getInt("hourlyShowCount", 0)
        set(value) = sharedPreferences.edit().putInt("hourlyShowCount", value).apply()

    var currentHour: Int
        get() = sharedPreferences.getInt("currentHour", 0)
        set(value) = sharedPreferences.edit().putInt("currentHour", value).apply()

    var dailyShowCount: Int
        get() = sharedPreferences.getInt("dailyShowCount", 0)
        set(value) = sharedPreferences.edit().putInt("dailyShowCount", value).apply()

    var clickCount: Int
        get() = sharedPreferences.getInt("clickCount", 0)
        set(value) = sharedPreferences.edit().putInt("clickCount", value).apply()

    var lastDate: String
        get() = sharedPreferences.getString("lastDate", "") ?: ""
        set(value) = sharedPreferences.edit().putString("lastDate", value).apply()
}


