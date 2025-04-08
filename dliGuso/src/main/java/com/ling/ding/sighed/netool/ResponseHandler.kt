package com.ling.ding.sighed.netool

import android.util.Log
import com.google.gson.Gson
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.txtbean.DliBean
import org.json.JSONObject

class ResponseHandler(private val dataProcessor: DataProcessor) {

    fun handleAdminResponse(responseBody: String, datetimeHeader: String?, callback: NetTool.ResultCallback) {
        try {
            val datetime = datetimeHeader
                ?: throw IllegalArgumentException("Missing datetime header")

            val finalData = dataProcessor.decryptResponse(responseBody, datetime)
            val jsonResponse = JSONObject(finalData)
            val jsonData = dataProcessor.parseAdminRefData(jsonResponse.toString())
            val adminBean = runCatching {
                Gson().fromJson(jsonData, DliBean::class.java)
            }.getOrNull()

            when {
                adminBean == null || adminBean.adOperations==null -> {
                    callback.onError("Invalid response format")
                    ShowDataTool.showLog("请求结果=$jsonData")
                    AppPointData.getadmin("8", null)
                }
                ShowDataTool.getAdminData() == null -> {
                    ShowDataTool.putAdminData(jsonData)
                    val type = adminBean.userManagement.profile.classification
                    Log.e("TAG", "handleAdminResponse:type=${type}")
                    AppPointData.getadmin(type, "200")
                    callback.onComplete(jsonData)
                }
                adminBean.userManagement.profile.classification == "1" -> {
                    ShowDataTool.putAdminData(jsonData)
                    AppPointData.getadmin("1", "200")
                    callback.onComplete(jsonData)
                }
                else -> {
                    AppPointData.getadmin("2", "200")
                    callback.onComplete(jsonData)
                }
            }
        } catch (e: Exception) {
            callback.onError("Response processing failed: ${e.message}")
            AppPointData.getadmin("4", "parse_error")
        }
    }
}
