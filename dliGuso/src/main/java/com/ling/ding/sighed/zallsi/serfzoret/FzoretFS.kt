package com.ling.ding.sighed.zallsi.serfzoret

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.widget.RemoteViews
import com.ling.ding.sighed.R
import com.ling.ding.sighed.adtool.ShowDataTool
import com.ling.ding.sighed.txtdata.DrinkStartApp.isServiceRunning

class FzoretFS : Service() {
    @SuppressLint("ForegroundServiceType", "RemoteViewLayout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ShowDataTool.showLog("TxtNiFS onStartCommand-1=${isServiceRunning}")
        if (!isServiceRunning) {
            isServiceRunning =true
            val channel = NotificationChannel("txtShow", "txtShow", NotificationManager.IMPORTANCE_MIN)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.enableVibration(false)
            (application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).run {
                if (getNotificationChannel(channel.toString()) == null) createNotificationChannel(channel)
            }
            runCatching {
                startForeground(
                    3432,
                    NotificationCompat.Builder(this, "txtShow").setSmallIcon(R.drawable.ces_show)
                        .setContentText("")
                        .setContentTitle("")
                        .setOngoing(true)
                        .setCustomContentView(RemoteViews(packageName, R.layout.layout_no))
                        .build()
                )
            }
            ShowDataTool.showLog("TxtNiFS onStartCommand-2=${isServiceRunning}")
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}
