package com.ling.ding.sighed.zallsi.recofzt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import java.util.concurrent.TimeUnit

@Keep
class FzoretMRe: BroadcastReceiver() {
    private var lastStartTime: Long = 0
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("f")) {
            val eIntent = intent.getParcelableExtra<Parcelable>("f") as Intent?
            if (eIntent != null) {
                try {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastStartTime > TimeUnit.SECONDS.toMillis(1)) {
                        Log.e("TAG", "onReceive: BroadcastReceiver")
                        context.startActivity(eIntent)
                        lastStartTime = currentTime
                    }
                    return
                } catch (e: Exception) {
                }
            }
        }
    }
}