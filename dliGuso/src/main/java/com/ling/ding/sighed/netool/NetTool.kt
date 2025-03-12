package com.ling.ding.sighed.netool

import android.annotation.SuppressLint
import android.util.Base64
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.adtool.ShowDataTool
import com.google.gson.Gson
import com.ling.ding.sighed.txtbean.DliBean
import com.ling.ding.sighed.txtbean.EnvironmentManager
import org.json.JSONObject
import java.net.HttpURLConnection
import java.nio.charset.StandardCharsets
import java.net.SocketTimeoutException
import java.util.concurrent.Executors

object NetTool {
    interface ResultCallback {
        fun onComplete(result: String)
        fun onError(message: String)
    }

    private val threadPool = Executors.newFixedThreadPool(4)

    fun executeAdminRequest(callback: ResultCallback) {
        val requestData = prepareRequestData()
        ShowDataTool.showLog("executeAdminRequest=${EnvironmentManager.getCurrentConfig().adminUrl}")
        TtPoint.postPointData(false, "reqadmin")

        threadPool.execute {
            var connection: HttpURLConnection? = null
            try {
                val (processedData, datetime) = processRequestData(requestData)
                val url = EnvironmentManager.getCurrentConfig().adminUrl
                connection = (java.net.URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("datetime", datetime)
                    connectTimeout = 60_000 // 60 seconds
                    readTimeout = 60_000 // 60 seconds
                    doOutput = true
                }

                // Write the request body
                connection.outputStream.use { outputStream ->
                    outputStream.write(processedData.toByteArray(StandardCharsets.UTF_8))
                }

                // Check response code
                if (connection.responseCode !in 200..299) {
                    callback.onError("HTTP error: ${connection.responseCode}")
                    AppPointData.getadmin("5", connection.responseCode.toString())
                    return@execute
                }

                // Read and handle the response
                connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    val responseBody = reader.readText()
                    handleHttpAdminResponse(responseBody, connection.getHeaderField("datetime"), callback)
                }
            } catch (e: SocketTimeoutException) {
                callback.onError("Request timed out: ${e.message}")
                AppPointData.getadmin("6", "timeout")
            } catch (e: Exception) {
                callback.onError("Operation failed: ${e.message}")
                AppPointData.getadmin("7", "timeout")
            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun handleHttpAdminResponse(
        responseBody: String,
        datetimeHeader: String?,
        callback: ResultCallback
    ) {
        try {
            val datetime = datetimeHeader
                ?: throw IllegalArgumentException("Missing datetime header")

            // 解密处理
            val decodedBytes = Base64.decode(responseBody, Base64.DEFAULT)
            val decodedStr = String(decodedBytes, StandardCharsets.UTF_8)
            val finalData = xorEncrypt(decodedStr, datetime)

            // 解析数据
            val jsonResponse = JSONObject(finalData)
            val jsonData = parseAdminRefData(jsonResponse.toString())
            val adminBean = runCatching {
                Gson().fromJson(jsonData, DliBean::class.java)
            }.getOrNull()

            when {
                adminBean == null -> {
                    callback.onError("Invalid response format")
                    ShowDataTool.showLog("请求结果=$jsonData")
                    AppPointData.getadmin("8", null)
                }
                ShowDataTool.getAdminData() == null -> {
                    ShowDataTool.putAdminData(jsonData)
                    val type = adminBean.userManagement.profile.classification
                    AppPointData.getadmin(type, "success")
                    callback.onComplete(jsonData)
                }
                adminBean.userManagement.profile.classification == "1" -> {
                    ShowDataTool.putAdminData(jsonData)
                    AppPointData.getadmin("1", "success")
                    callback.onComplete(jsonData)
                }
                else -> {
                    AppPointData.getadmin("2", "success")
                    callback.onComplete(jsonData)
                }
            }
        } catch (e: Exception) {
            callback.onError("Response processing failed: ${e.message}")
            AppPointData.getadmin("4", "parse_error")
        }
    }

    fun executePutRequest(body: Any, callback: ResultCallback) {
        threadPool.execute {
            var connection: HttpURLConnection? = null
            try {
                val jsonBody = JSONObject(body.toString()).toString()
                val url = EnvironmentManager.getCurrentConfig().upUrl
                connection = (java.net.URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    connectTimeout = 60_000 // 60 seconds
                    readTimeout = 60_000 // 60 seconds
                    doOutput = true
                }

                // Write the request body
                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonBody.toByteArray(StandardCharsets.UTF_8))
                }

                // Check response code
                if (connection.responseCode !in 200..299) {
                    callback.onError("HTTP error: ${connection.responseCode}")
                    return@execute
                }

                // Read the response
                connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    val responseBody = reader.readText()
                    callback.onComplete(responseBody)
                }
            } catch (e: Exception) {
                callback.onError("Put request failed: ${e.message}")
            } finally {
                connection?.disconnect()
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun prepareRequestData(): String {
        return JSONObject().apply {
            put("zDAIGEAbLI", "com.water.note.mate.fresh.leaf")
            put("lpOMVKK", FirstRunFun.localStorage.appiddata)
            put("uXyEpJnj", FirstRunFun.localStorage.refdata)
//            put("uXyEpJnj", "organic111")
            put("ztjxIrQALy", AppPointData.showAppVersion())
        }.toString()
    }

    private fun processRequestData(rawData: String): Pair<String, String> {
        val datetime = System.currentTimeMillis().toString()
        val encrypted = xorEncrypt(rawData, datetime)
        return Base64.encodeToString(encrypted.toByteArray(), Base64.NO_WRAP) to datetime
    }

    private fun xorEncrypt(text: String, datetime: String): String {
        val cycleKey = datetime.toCharArray()
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % cycleKey.size].toInt()).toChar()
        }.joinToString("")
    }

    private fun parseAdminRefData(jsonString: String): String {
        return try {
            JSONObject(jsonString).getJSONObject("KOAtQ").getString("conf")
        } catch (e: Exception) {
            ""
        }
    }
}




