package com.ling.ding.sighed.netool

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import com.ling.ding.sighed.adtool.ShowDataTool
import org.json.JSONObject
import java.util.UUID
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.facebook.appevents.AppEventsLogger
import com.ling.ding.sighed.adtool.TranplusConfig
import com.ling.ding.sighed.txtdata.DrinkStartApp
import com.tradplus.ads.base.bean.TPAdInfo
import java.math.BigDecimal
import java.util.Currency

object AppPointData {
    fun showAppVersion(): String {
        return mainStart.packageManager.getPackageInfo(mainStart.packageName, 0).versionName ?: ""
    }

    private fun topJsonData(): JSONObject {
        return  JSONObject().apply {
            //os_version
            put("are", Build.VERSION.RELEASE)
            //android_id
            put("hesitant", FirstRunFun.localStorage.appiddata)
            //app_version
            put("supine", showAppVersion())
            //device_model-最新需要传真实值
            put("lullaby", Build.BRAND)
            //system_language//假值
            put("avow", "acsa_sdasl")
            //os
            put("mcgraw", "massive")
            //bundle_id
            put("landfill", mainStart.packageName)
            //distinct_id
            put("familial", FirstRunFun.localStorage.appiddata)
            //operator 传假值字符串
            put("overt", "ces2")
            //client_ts
            put("grip", System.currentTimeMillis())
            //log_id
            put("knapp", UUID.randomUUID().toString())
            //manufacturer
            put("route", Build.MANUFACTURER)
            //gaid
            put("aitken", "")

        }

    }

    fun upInstallJson(): String {
        return topJsonData().apply {
            //build
            put("machine", "build/${Build.ID}")

            //referrer_url
            put("prompt", FirstRunFun.localStorage.refdata)

            //user_agent
            put("donovan", "")

            //lat
            put("urushiol", "contempt")

            //referrer_click_timestamp_seconds
            put("heroic", 0)

            //install_begin_timestamp_seconds
            put("olivine", 0)

            //referrer_click_timestamp_server_seconds
            put("brigade", 0)

            //install_begin_timestamp_server_seconds
            put("mend", 0)

            //install_first_seconds
            put("truthful", getFirstInstallTime())

            //last_update_seconds
            put("hay", 0)

            put("raunchy","wastrel")
        }.toString()
    }

    fun upAdJson(adValue: TPAdInfo): String {
        return topJsonData().apply {
            //ad_pre_ecpm
            put("suckle", adValue.ecpm.toDouble() * 1000)
            //currency
            put("emilio", "USD")
            //ad_network
            put(
                "hibernia",
                adValue.adSourceName
            )
            //ad_source
            put("future", "Tradplus")
            //ad_code_id
            put("hoopla", adValue.tpAdUnitId)
            //ad_pos_id
            put("woeful", "int")
            //ad_rit_id
            put("ganglion", "")
            //ad_sense
            put("mu", "")
            //ad_format
            put("shook", adValue.format)
            put("raunchy", "such")
        }.toString()
    }

    fun upPointJson(name: String): String {
        return topJsonData().apply {
            put("raunchy", name)
        }.toString()
    }

    fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
    ): String {
        return topJsonData().apply {
            put("raunchy", name)
            put(name,JSONObject().apply {
                if (key1 != null) {
                    put(key1, keyValue1)
                }
                if (key2 != null) {
                    put(key2, keyValue2)
                }
            })
        }.toString()
    }
    private fun getFirstInstallTime(): Long {
        try {
            val packageInfo =
                mainStart.packageManager.getPackageInfo(mainStart.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }


     fun postAdValue(adValue: TPAdInfo) {
         val ecmVVVV = try {
             adValue.ecpm.toDouble() / 1000.0
         } catch (e: NumberFormatException) {
             ShowDataTool.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, using default value 0.0")
             0.0
         }
         val adRevenueData = AFAdRevenueData(
             adValue.adSourceName,
             MediationNetwork.TRADPLUS,
             "USD",
             ecmVVVV
         )
         val additionalParameters: MutableMap<String, Any> = HashMap()
         additionalParameters[AdRevenueScheme.AD_UNIT] = adValue.adSourceId
         additionalParameters[AdRevenueScheme.AD_TYPE] = "Interstitial"
         AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)

        val jsonBean = ShowDataTool.getAdminData()
        val data = jsonBean?.assetConfig?.identifiers?.facebook?.placementId?:""
        if (data.isBlank()) {
            return
        }
        if (data.isNotEmpty()) {
            try {
                AppEventsLogger.newLogger(mainStart).logPurchase(
                    BigDecimal(ecmVVVV.toString()),
                    Currency.getInstance("USD")
                )
            } catch (e: NumberFormatException) {
                ShowDataTool.showLog("Invalid ecpmPrecision value: ${adValue.ecpm}, skipping logPurchase")
            }
        }
    }


    fun getadmin(type: String, codeInt: String?) {
        var isuserData: String? = null
        if (codeInt == null) {
            isuserData = null
        } else if (codeInt != "200") {
            isuserData = codeInt
        } else if (type=="1") {
            isuserData = "a"
        } else {
            isuserData = "b"
        }

        TtPoint.postPointData(true, "getadmin", "getstring", isuserData)
    }


    fun showSuccessPoint() {
        val time = (System.currentTimeMillis() - TranplusConfig.showAdTime) / 1000.0 // 使用 1000.0 确保结果为 Double
        val formattedTime = "%.1f".format(time) // 保留一位小数
        TtPoint.postPointData(false, "show", "t", formattedTime.toDouble()) // 转换为 Double 类型
        TranplusConfig.showAdTime = 0
    }

    fun firstExternalBombPoint() {
        val ata = FirstRunFun.localStorage.firstPoint
        if (ata) {
            return
        }
        val instalTime = DrinkStartApp.getInstallDurationSeconds()
        TtPoint.postPointData(true, "first_start", "time", instalTime)
        FirstRunFun.localStorage.firstPoint = true
    }

    fun pointInstallAf(data: String) {
        val keyIsAdOrg = FirstRunFun.localStorage.adOrgPoint
        if (data.contains("non_organic", true) && !keyIsAdOrg) {
            TtPoint.postPointData(true, "non_organic")
            FirstRunFun.localStorage.adOrgPoint = true
        }
    }

    fun getLiMitData() {
        val getlimitState = FirstRunFun.localStorage.getlimit
        if (!getlimitState) {
            TtPoint.postPointData(false, "getlimit")
            FirstRunFun.localStorage.getlimit = true
        }
    }
}