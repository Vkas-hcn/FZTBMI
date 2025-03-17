package com.ling.ding.sighed.txtmain

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.ling.ding.sighed.txtdata.DrinkStartApp
import com.ling.ding.sighed.adtool.TranplusUtils
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.AppPointData
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtbean.EnvironmentManager
import com.ling.ding.sighed.txtdata.DrinkConfigData
import com.ling.ding.sighed.txtdata.LocalStorage
import com.ling.ding.sighed.zmain.HFiveMain
import com.ling.ding.sighed.zmain.TWMain
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.tradplus.ads.open.TradPlusSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

object FirstRunFun {
    lateinit var localStorage: LocalStorage
    lateinit var mainStart: Application
    var isVps: Boolean = true
    val adShowFun = TranplusUtils()
    fun init(application: Application, mustXSData: Boolean) {
        if (!isMainProcess(application)) {
            return
        }
        ShowDataTool.showLog("San MainStart init")
        EnvironmentManager.switchEnvironment("TEST")
        localStorage = LocalStorage(application)
        mainStart = application
        isVps = mustXSData
        val path = "${mainStart.applicationContext.dataDir.path}/gujkdl"
        File(path).mkdirs()
        ShowDataTool.showLog(" 文件名=: $path")
        HFiveMain.hfApp(application)
        val lifecycleObserver = IntetNetSHow()
        application.registerActivityLifecycleCallbacks(lifecycleObserver)
        initialize()
        getAndroidId()
        DrinkStartApp.startService()
        noShowICCC()
        RefDataFun.launchRefData()
        sessionUp()
        initAppsFlyer()
        getFcmFun()
    }
    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(context)
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses
            ?.firstOrNull { it.pid == pid }
            ?.processName
    }
    private fun noShowICCC() {
        CoroutineScope(Dispatchers.Main).launch {
            val isaData = ShowDataTool.getAdminData()
            if (isaData == null || isaData.userManagement.profile.classification != "1") {
                ShowDataTool.showLog("不是A方案显示图标")
                TWMain.dliGuso(6125)
            }
        }
    }

    fun canIntNextFun() {
        adShowFun.startAdMonitoring()
    }

    fun initialize() {
        TradPlusSdk.setTradPlusInitListener {
        }
        TradPlusSdk.initSdk(mainStart, EnvironmentManager.getCurrentConfig().appid)
    }

    @SuppressLint("HardwareIds")
    fun getAndroidId() {
        val adminData = localStorage.appiddata
        if (adminData.isEmpty()) {
            val androidId =
                Settings.Secure.getString(mainStart.contentResolver, Settings.Secure.ANDROID_ID)
            if (!androidId.isNullOrBlank()) {
                localStorage.appiddata = androidId
            } else {
                localStorage.appiddata = UUID.randomUUID().toString()
            }
        }
    }

    fun sessionUp() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                TtPoint.postPointData(false, "session_up")
                delay(1000 * 60 * 15)
            }
        }
    }

    fun initAppsFlyer() {
        val appsFlyer = AppsFlyerLib.getInstance()
        val config = EnvironmentManager.getCurrentConfig()

        ShowDataTool.showLog("AppsFlyer-id: $${config.appsflyId}")

        appsFlyer.init(config.appsflyId, object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionDataMap: MutableMap<String, Any>?) {
                // 安全类型转换+默认值处理
                val status = (conversionDataMap?.get("af_status") as? String) ?: "no_data"
                ShowDataTool.showLog("AppsFlyer: $status")
                AppPointData.pointInstallAf(status)
                conversionDataMap?.forEach { (key, value) ->
                    ShowDataTool.showLog(
                        "AppsFlyer-all: key=$key | value=${
                            value.toString().take(200)
                        }"
                    )
                }
            }

            override fun onConversionDataFail(p0: String?) {
                ShowDataTool.showLog("AppsFlyer: onConversionDataFail: $p0")
            }

            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                ShowDataTool.showLog("AppsFlyer: onAppOpenAttribution: ${p0?.toLogFormat()}")
            }

            override fun onAttributionFailure(p0: String?) {
                ShowDataTool.showLog("AppsFlyer: onAttributionFailure: $p0")
            }

        }, mainStart)

        with(appsFlyer) {
            setCustomerUserId(localStorage.appiddata)
            start(mainStart)
            logEvent(mainStart, "403_bmi_install", hashMapOf<String, Any>().apply {
                put("customer_user_id", localStorage.appiddata)
                put("app_version", AppPointData.showAppVersion())
                put("os_version", Build.VERSION.RELEASE)
                put("bundle_id", mainStart.packageName)
                put("language", "asc_wds")
                put("platform", "raincoat")
                put("android_id", localStorage.appiddata)
            })
        }
    }

    private fun Map<*, *>.toLogFormat(): String = entries.joinToString(" | ") {
        "${it.key}=${it.value.toString().take(50)}"
    }

    fun getFcmFun() {
        if (!isVps) return
        if (localStorage.fcmState) return
        runCatching {
            Firebase.messaging.subscribeToTopic(DrinkConfigData.fffmmm)
                .addOnSuccessListener {
                    localStorage.fcmState = true
                    ShowDataTool.showLog("Firebase: subscribe success")
                }
                .addOnFailureListener {
                    ShowDataTool.showLog("Firebase: subscribe fail")
                }
        }
    }

}