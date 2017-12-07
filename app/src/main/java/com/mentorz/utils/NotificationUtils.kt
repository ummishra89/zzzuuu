package com.mentorz.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.os.Build
import android.support.v7.app.NotificationCompat
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.CommentActivity
import com.mentorz.activities.HomeActivity
import com.mentorz.model.NotificationData
import com.mentorz.activities.MentorProfileActivity
import com.mentorz.activities.PostActivity
import com.mentorz.constants.PushType
import com.mentorz.sinchvideo.ChatActivity


/**
 * Created by umesh on 27/08/17.
 */
object NotificationUtils {

    var notificationCountListener: NotificationCountListener? = null

    fun registerNotificationCountListener(listener: NotificationCountListener) {
        notificationCountListener = listener
    }

    interface NotificationCountListener {
        fun onNotificationCountChange()
    }

    fun sendNotification(context: Context, title: String, body: String, notificationData: NotificationData) {
        val notification = NotificationCompat.Builder(context)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.p_n);
        } else {
            notification.setSmallIcon(R.mipmap.ic_launcher);
        }
        notification.priority = Notification.PRIORITY_MAX
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notification.setSound(uri)
        notification.setAutoCancel(true)

        if (notificationCountListener != null&&notificationData.pushType!=PushType.BLOCK) {
            notificationCountListener?.onNotificationCountChange()
        }

        when (notificationData.pushType) {

            PushType.BLOCK -> {
                val intent = Intent()
                intent.action = Constant.ACTION_BLOCK
                intent.putExtra("user_id", notificationData.userId)
                context.sendBroadcast(intent)

            }

            PushType.SEND_REQUEST -> {
                val intent = Intent(context, HomeActivity::class.java)
                intent.action = "OPEN_TAB_REQUEST";
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("1" + notificationData.userId.toString()).toInt(), notification.build())
                sendBroadcastForRequestRecived(context)
            }
            PushType.HAS_FOLLOWING -> {
                val intent = Intent(context, MentorProfileActivity::class.java)
                intent.putExtra("user_id", notificationData.userId)
                intent.putExtra("push_type", notificationData.pushType)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("2" + notificationData.userId.toString()).toInt(), notification.build())
            }
            PushType.ACCEPT_REQUEST -> {
                val intent1 = Intent()
                intent1.action =Constant.ACTION_REQUEST_ACCEPT
                intent1.putExtra("user_id",notificationData.userId)
                context.sendBroadcast(intent1)
                val intent = Intent(context, MentorProfileActivity::class.java)
                intent.putExtra("user_id", notificationData.userId)
                intent.putExtra("push_type", notificationData.pushType)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("3" + notificationData.userId.toString()).toInt(), notification.build())
            }
            PushType.LIKED_POST -> {
                val intent = Intent(context, PostActivity::class.java)
                intent.putExtra("post_id", notificationData.postId)
                intent.putExtra("push_type", notificationData.pushType)
                intent.putExtra("user_id", notificationData.userId)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("4" + notificationData.userId.toString()).toInt(), notification.build())
            }
            PushType.COMMENTED_ON_POST -> {
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("post_id", notificationData.postId)
                intent.putExtra("user_name", notificationData.userName)
                intent.putExtra("push_type", notificationData.pushType)
                intent.putExtra("user_id", notificationData.userId)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("5" + notificationData.userId.toString()).toInt(), notification.build())
            }
            PushType.SHARE -> {
                val intent = Intent(context, PostActivity::class.java)
                intent.putExtra("post_id", notificationData.postId)
                intent.putExtra("push_type", notificationData.pushType)
                intent.putExtra("user_id", notificationData.userId)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("6" + notificationData.userId.toString()).toInt(), notification.build())
            }

            PushType.CHAT_MESSAGE_RECEIVED -> {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("user_id", notificationData.userId)
                intent.putExtra("chat_id", notificationData.postId)
                intent.putExtra("push_type", notificationData.pushType)
                intent.putExtra("user_name", notificationData.userName)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pendingIntent)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(("7" + notificationData.userId.toString()).toInt(), notification.build())
            }
        }

    }

    fun sendBroadcastForRequestRecived(context: Context){
        val intent = Intent()
        intent.action = Constant.ACTION_REQUEST_RECEIVED
        context.sendBroadcast(intent)
    }
}