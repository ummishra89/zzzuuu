package com.mentorz.gcm

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.sinchvideo.IncomingCallScreenActivity
import com.mentorz.sinchvideo.SinchService
import com.sinch.android.rtc.NotificationResult
import com.sinch.android.rtc.SinchHelpers


/**
 * Created by umesh on 19/07/17.
 */

class GcmIntentService : IntentService("GcmIntentService"), ServiceConnection {
    private var mIntent: Intent? = null
    override fun onServiceDisconnected(p0: ComponentName?) {
    }

    override fun onServiceConnected(p0: ComponentName?, iBinder: IBinder?) {
        if (mIntent == null) {
            return
        }
        if (SinchHelpers.isSinchPushIntent(mIntent)) {
            val sinchService = iBinder as SinchService.SinchServiceInterface
            if (sinchService != null) {
                Toast.makeText(MentorzApplication.applicationContext(), "notification", Toast.LENGTH_SHORT).show()
                val intent = mIntent
                sinchService.startClient(MentorzApplication.instance?.prefs?.getUserId().toString())
                val result: NotificationResult? = sinchService.relayRemotePushNotifPayload(intent)

                val callId = result?.callResult?.callId
                Log.d("GcmIntentService", "call id:" + callId)
                // handle result, e.g. show a notification or similar
                //val mCallId = mIntent?.getStringExtra("CALL_ID")


                val notification = NotificationCompat.Builder(MentorzApplication.applicationContext())
                        .setSmallIcon(R.drawable.notification_icon_background)
                        .setContentTitle("title")
                        .setContentText("message")

                val callIntent: Intent = Intent(MentorzApplication.applicationContext(), IncomingCallScreenActivity::class.java)

                callIntent.putExtra(SinchService.CALL_ID, callId)
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // startActivity(callIntent)
                val pendingIntent: PendingIntent = PendingIntent.getActivity(MentorzApplication.applicationContext(), 0, callIntent, 0)

                notification.setContentIntent(pendingIntent)
                val notificationManager = MentorzApplication.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                //notificationManager.notify(123, notification.build())
            }
        }
        GcmBroadcastReceiver.completeWKfullIntent(mIntent)
        mIntent = null
    }

    override fun onHandleIntent(intent: Intent?) {
        mIntent = intent
        if (SinchHelpers.isSinchPushIntent(intent)) {
            applicationContext.bindService(Intent(this, SinchService::class.java), this, Context.BIND_AUTO_CREATE)
        } else {
            GcmBroadcastReceiver.completeWKfullIntent(intent)
        }
    }


}