package com.young.weexbase.widget

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.young.weexbase.R

object NotificationChannelCustom {
    fun registChannel(application: Application) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = "1"
            val name: CharSequence = application.getString(R.string.app_name)
            val description = application.getString(R.string.msg_new)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            mNotificationManager.createNotificationChannel(mChannel.apply {
                this.description = description
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            })
        }
    }
}