package com.ling.ding.sighed.txtdata

import android.content.pm.PackageManager
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import com.ling.ding.sighed.txtmain.IntetNetSHow

object DrinkStartApp {
    var isServiceRunning = false

    // 初始化各管理器
    private val serviceManager by lazy { ServiceManager(mainStart) }
    // 代理方法调用
    fun startService() = serviceManager.startPeriodicService()
    fun closeAllActivities() = IntetNetSHow.closeAllActivities()
    private fun getInstallTime(): Long {
        return try {
            val packageInfo = mainStart.packageManager.getPackageInfo(mainStart.packageName, 0)
            packageInfo.firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }

    fun getInstallDurationSeconds(): Long {
        return (System.currentTimeMillis() - getInstallTime()) / 1000
    }
}