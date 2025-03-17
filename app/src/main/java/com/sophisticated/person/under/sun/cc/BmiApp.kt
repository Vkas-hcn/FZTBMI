package com.sophisticated.person.under.sun.cc

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.ling.ding.sighed.txtmain.FirstRunFun
import java.util.UUID

class BmiApp : Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        FirstRunFun.init(this, false)
        appContext = this
        sharedPreferences = this.getSharedPreferences("BmiRecords", Context.MODE_PRIVATE)
        val uuidData = sharedPreferences.getString("uuidData", null)
        if (uuidData == null) {
            sharedPreferences.edit().putString("uuidData", UUID.randomUUID().toString()).apply()
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

            override fun onActivityStarted(activity: Activity) {
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
            }
        })
    }
}
