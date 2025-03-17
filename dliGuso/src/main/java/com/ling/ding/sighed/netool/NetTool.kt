package com.ling.ding.sighed.netool

import android.annotation.SuppressLint
import android.util.Base64
import com.ling.ding.sighed.txtmain.FirstRunFun
import com.ling.ding.sighed.adtool.ShowDataTool
import com.google.gson.Gson
import com.ling.ding.sighed.txtbean.DliBean
import com.ling.ding.sighed.txtbean.EnvironmentManager
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors




class NetTool(private val requestManager: RequestManager, private val dataProcessor: DataProcessor, private val responseHandler: ResponseHandler) {

    interface ResultCallback {
        fun onComplete(result: String)
        fun onError(message: String)
    }

    private val threadPool = Executors.newFixedThreadPool(4)

    fun executeAdminRequest(callback: ResultCallback) {
        val requestData = dataProcessor.prepareRequestData()
        ShowDataTool.showLog("executeAdminRequest=${EnvironmentManager.getCurrentConfig().adminUrl}")
        TtPoint.postPointData(false, "reqadmin")

        threadPool.execute {
            requestManager.executeRequest(
                true,
                EnvironmentManager.getCurrentConfig().adminUrl,
                requestData,
                object : RequestManager.RequestCallback {
                    override fun onSuccess(responseBody: String, datetimeHeader: String?) {
                        responseHandler.handleAdminResponse(responseBody, datetimeHeader, callback)
                    }

                    override fun onError(message: String) {
                        callback.onError(message)
                    }
                }
            )
        }
    }

    fun executePutRequest(body: Any, callback: ResultCallback) {
        threadPool.execute {
            requestManager.executeRequest(
                false,
                EnvironmentManager.getCurrentConfig().upUrl,
                body.toString(),
                object : RequestManager.RequestCallback {
                    override fun onSuccess(responseBody: String, datetimeHeader: String?) {
                        callback.onComplete(responseBody)
                    }

                    override fun onError(message: String) {
                        callback.onError(message)
                    }
                }
            )
        }
    }
}







