package com.ling.ding.sighed.adtool

import android.util.Log
import com.google.gson.Gson
import com.ling.ding.sighed.txtbean.DliBean
import com.ling.ding.sighed.txtmain.FirstRunFun

object ShowDataTool {
    private const val LOG_TAG = "Drink"
    fun showLog(data: String) {
        if (!FirstRunFun.isVps) {
            Log.e(LOG_TAG, data)
        }
    }

    fun getAdminData(): DliBean? {
//        FirstRunFun.localStorage.admindata = DrinkConfigData.data_can
        val adminData = FirstRunFun.localStorage.admindata
        return try {
            val adminBean = Gson().fromJson(adminData, DliBean::class.java)
            if (adminBean != null && isValidAdminBean(adminBean)) adminBean else null
        } catch (e: Exception) {
            showLog("Failed to parse admin data: ${e.message}")
            null
        }
    }

    private fun isValidAdminBean(bean: DliBean): Boolean {
        return bean.userManagement?.profile?.classification != null
    }


    fun putAdminData(adminBean: String) {
        FirstRunFun.localStorage.admindata = adminBean
//        FirstRunFun.localStorage.admindata = DrinkConfigData.data_can
    }
}