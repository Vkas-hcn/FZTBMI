package com.ling.ding.sighed.netool

import android.annotation.SuppressLint
import android.util.Base64
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.NetTool.ResultCallback
import com.ling.ding.sighed.txtbean.EnvironmentManager
import com.ling.ding.sighed.txtmain.FirstRunFun
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets

class RequestManager {

    interface RequestCallback {
        fun onSuccess(responseBody: String, datetimeHeader: String?)
        fun onError(message: String)
    }

    fun executeRequest(
        isAdminPost: Boolean = false,
        url: String,
        requestData: String,
        callback: RequestCallback
    ) {
        var connection: HttpURLConnection? = null

        try {
            val (processedData, dt) = processRequestData(requestData)

            connection = (java.net.URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("dt", dt)
                connectTimeout = 60_000 // 60 seconds
                readTimeout = 60_000 // 60 seconds
                doOutput = true
            }
            if (isAdminPost) {
                connection.outputStream.use { outputStream ->
                    outputStream.write(processedData.toByteArray(StandardCharsets.UTF_8))
                }
            } else {
                val jsonBodyString = JSONObject(requestData).toString()
                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonBodyString.toByteArray(StandardCharsets.UTF_8))
                }
            }

            // Check response code
            if (connection.responseCode !in 200..299) {
                callback.onError("HTTP error: ${connection.responseCode}")
                if (isAdminPost &&connection.responseCode.toString()!=null) {
//                    AppPointData.getadmin("2", connection.responseCode.toString())
                }
                return
            }

            // Read and handle the response
            connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                val responseBody = reader.readText()
                callback.onSuccess(responseBody, connection.getHeaderField("dt"))
            }
        } catch (e: SocketTimeoutException) {
            callback.onError("Request timed out: ${e.message}")
            if (isAdminPost) {
//                AppPointData.getadmin("2", "timeout")
            }
        } catch (e: Exception) {
            callback.onError("Operation failed: ${e.message}")
            if (isAdminPost) {
//                AppPointData.getadmin("2", "timeout")
            }
        } finally {
            connection?.disconnect()
        }
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

}

