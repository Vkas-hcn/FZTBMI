package com.ling.ding.sighed.txtmain


import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.netool.TtPoint
import com.ling.ding.sighed.txtdata.DrinkConfigData
import com.ling.ding.sighed.txtdata.DrinkStartApp
import com.ling.ding.sighed.txtdata.DrinkStartApp.isServiceRunning
import com.ling.ding.sighed.txtmain.FirstRunFun.mainStart
import com.ling.ding.sighed.macui.GuSoActivity
import com.ling.ding.sighed.zallsi.serfzoret.FzoretFS

@Keep
class IntetNetSHow : Application.ActivityLifecycleCallbacks {
    companion object{
        private val activities = ArrayList<Activity>()
        fun isHaveActivity(): Boolean {
            return activities.isNotEmpty()
        }
        fun getActivity(): ArrayList<Activity> {
            return activities
        }
        fun closeAllActivities() {
            activities.forEach {
                it.finishAndRemoveTask()
            }
            activities.clear()
        }
    }


    fun xiug(activity: Activity){
        if (activity is GuSoActivity) {
            return
        }
        if (activity.javaClass.name.contains(DrinkConfigData.startPack2)) {
            ShowDataTool.showLog("onActivityStarted=${activity.javaClass.name}")
            val anTime = DrinkStartApp.getInstallDurationSeconds()
            TtPoint.postPointData(false, "session_front", "time", anTime)
        }
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.removeAll { it == activity }
    }


    private fun addFun(activity: Activity){
        addActivity(activity)
        ShowDataTool.showLog("TxtNiFS onStartCommand-0=${isServiceRunning}")
        if (!isServiceRunning) {
            ContextCompat.startForegroundService(
                mainStart,
                Intent( mainStart, FzoretFS::class.java)
            )
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addFun(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        xiug(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
    }
}
