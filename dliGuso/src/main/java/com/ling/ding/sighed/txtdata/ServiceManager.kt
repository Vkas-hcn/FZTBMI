package com.ling.ding.sighed.txtdata

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.ling.ding.sighed.txtdata.DrinkStartApp.isServiceRunning
import com.ling.ding.sighed.zallsi.serfzoret.FzoretFS
import kotlinx.coroutines.*

class ServiceManager(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var serviceJob: Job? = null

    fun startPeriodicService() {
        stopPeriodicService()
        serviceJob = scope.launch {
            while (isActive) {
                if (!isServiceRunning && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, FzoretFS::class.java)
                    )
                }
                delay(1020)
            }
        }
    }

    fun stopPeriodicService() {
        serviceJob?.cancel()
        serviceJob = null
    }
}
