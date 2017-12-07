package com.mentorz.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mentorz.MentorzApplication
import com.mentorz.constants.PushType
import com.mentorz.database.DbManager
import com.mentorz.model.Notification
import com.mentorz.model.NotificationData
import com.mentorz.utils.NotificationUtils
import org.jetbrains.anko.doAsync


/**
 * Created by craterzone on 17/07/17.
 */
class MentorzFirebaseMessaingService : FirebaseMessagingService() {
    private val TAG: String = javaClass.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: " + remoteMessage!!.from)


        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification.body)
        }

    }

    var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())
    override fun handleIntent(p0: Intent?) {
        val notificationData = Gson().fromJson(p0?.extras?.get("gcm.notification.data").toString(), NotificationData::class.java)
        var notification : Notification? = null
        if (notificationData!!.pushType == PushType.BLOCK){
            notification = Notification(p0?.extras?.get("gcm.notification.body").toString(), p0?.extras?.get("google.sent_time").toString().toLong(), notificationData.userId!!, 0, notificationData.pushType!!, "")
            dbManager.deleteNotification(notification!!.userId)
        }
        else if (notificationData!!.postId == null){
        notification = Notification(p0?.extras?.get("gcm.notification.body").toString(), p0?.extras?.get("google.sent_time").toString().toLong(), notificationData.userId!!, 0, notificationData.pushType!!, notificationData.userName!!)
        }else{
        notification = Notification(p0?.extras?.get("gcm.notification.body").toString(), p0?.extras?.get("google.sent_time").toString().toLong(), notificationData.userId!!, notificationData.postId!!, notificationData.pushType!!, notificationData.userName!!)
        }
       // doAsync {
            if(!notification!!.pushType.equals(PushType.BLOCK,true)){
                dbManager.insertNotification(notification)
            }

       // }
        NotificationUtils.sendNotification(applicationContext, "Mentorz", p0?.extras?.get("gcm.notification.body").toString(), notificationData)
    }

}