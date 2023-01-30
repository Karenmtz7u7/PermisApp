package com.aplicacion.permisapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.icu.text.CaseMap.Title
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.aplicacion.permisapp.R

class NotificationHelper (base: Context) : ContextWrapper(base) {
    private val CHANNEL_ID = "com.aplicacion.permisapp"
    private val CHANNEL_NAME = "PermisApp"
    private var manager : NotificationManager? = null

    init{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createChannel(){
        val notificatonChanel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificatonChanel.enableLights(true)
        notificatonChanel.enableVibration(true)
        notificatonChanel.lightColor = Color.WHITE
        notificatonChanel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getMangger().createNotificationChannel(notificatonChanel)
    }

    fun getMangger(): NotificationManager {
        if(manager == null){
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        }
        return manager as NotificationManager
    }

    fun getNotification(title: String, body : String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setColor(Color.GREEN)
            .setSmallIcon(R.mipmap.ic_icon_icon)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }
}