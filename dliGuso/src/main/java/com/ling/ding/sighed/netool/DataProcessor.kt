package com.ling.ding.sighed.netool

import android.annotation.SuppressLint
import android.util.Base64
import com.ling.ding.sighed.txtmain.FirstRunFun
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DataProcessor {

    @SuppressLint("HardwareIds")
    fun prepareRequestData(): String {
        return JSONObject().apply {
            put("euPPHfswBO", "com.fitbmiscore.lifehabitfit")
            put("BbsTBz", FirstRunFun.localStorage.appiddata)
            put("ThvBY", FirstRunFun.localStorage.refdata)
//            put("ThvBY", "organic")
            put("UPms", AppPointData.showAppVersion())
        }.toString()
    }

    private fun xorEncrypt(text: String, datetime: String): String {
        val cycleKey = datetime.toCharArray()
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % cycleKey.size].toInt()).toChar()
        }.joinToString("")
    }

    fun decryptResponse(responseBody: String, datetime: String): String {
        val decodedBytes = Base64.decode(responseBody, Base64.DEFAULT)
        val decodedStr = String(decodedBytes, StandardCharsets.UTF_8)
        return xorEncrypt(decodedStr, datetime)
    }

    fun parseAdminRefData(jsonString: String): String {
        return try {
            JSONObject(jsonString).getJSONObject("aRB").getString("conf")
        } catch (e: Exception) {
            ""
        }
    }
}
