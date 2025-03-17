package com.ling.ding.sighed.txtmain

import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.NetTool
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtmain.FirstRunFun.localStorage
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.hours


object RefDataFun {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var adminScheduler: AdminTaskScheduler
    private val referrerManager by lazy { InstallReferrerManager(mainStart) }

    fun launchRefData() {
        adminScheduler = AdminTaskScheduler(ioScope)
        when {
            localStorage.refdata.isNotEmpty() -> handleExistingRefData()
            else -> startRefMonitoring()
        }
    }

    private fun handleExistingRefData() {
        startOneTimeAdminData()
        localStorage.IS_INT_JSON.takeIf { it.isNotEmpty() }?.let {
            TtPoint.postInstallData()
        }
    }

    private fun startRefMonitoring() {
        ioScope.launch {
            while (localStorage.refdata.isEmpty()) {
                fetchReferrerData()
                delay(10_100)
            }
        }
    }

    private suspend fun fetchReferrerData() {
        runCatching {
            referrerManager.fetchInstallReferrer()?.let { referrer ->
                localStorage.refdata = referrer
                withContext(Dispatchers.Main) {
                    TtPoint.postInstallData()
                }
                startOneTimeAdminData()
            }
        }.onFailure { e ->
            ShowDataTool.showLog("Referrer error: ${e.message}")
        }
    }

    private fun startOneTimeAdminData() {
        when (localStorage.admindata.isEmpty()) {
            true -> TtPoint.onePostAdmin()
            false -> TtPoint.twoPostAdmin()
        }
        scheduleAdminTasks()
    }

    private fun scheduleAdminTasks() {
        adminScheduler.scheduleHourly {
            TtPoint.netTool.executeAdminRequest(object : NetTool.ResultCallback {
                override fun onComplete(result: String) {
                    ShowDataTool.showLog("定时请求Admin success: ${result}")
                }
                override fun onError(message: String) {
                    ShowDataTool.showLog("定时请求Admin failed: ${message}")
                }
            })
        }
    }

    fun cleanup() {
        ioScope.coroutineContext.cancelChildren()
        adminScheduler.cancelAll()
    }
    // 定时任务调度器
    private class AdminTaskScheduler(
        private val ioScope: CoroutineScope
    ) {
        private var hourlyJob: Job? = null
        private val requestInterval = 1.hours

        fun scheduleHourly(block: suspend () -> Unit) {
            hourlyJob?.cancel()
            hourlyJob = ioScope.launch {
                while (isActive) {
                    delay(requestInterval)
                    block()
                }
            }
        }

        fun cancelAll() {
            hourlyJob?.cancel()
        }
    }
}