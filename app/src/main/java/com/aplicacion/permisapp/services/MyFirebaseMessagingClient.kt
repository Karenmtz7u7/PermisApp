package com.aplicacion.permisapp.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingClient : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val title = data["title"]
        val body = data["body"]

        if(!title.isNullOrBlank() && !body.isNullOrBlank()){
            showNotification(title, body)
        }
    }
    
    private fun showNotification(title:String, body: String){
        val helper = NotificationHelper(baseContext)
        val builder = helper.getNotification(title, body)
        helper.getMangger().notify(100, builder.build())
    }
    
}