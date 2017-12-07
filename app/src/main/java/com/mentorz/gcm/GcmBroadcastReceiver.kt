package com.mentorz.gcm

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import android.util.Log


/**
 * Created by umesh on 19/07/17.
 */
class GcmBroadcastReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("GcmBroadcastReceiver", "inside onMessageReceived")

        val comp = ComponentName(context?.packageName, GcmIntentService::class.java.name)
        WakefulBroadcastReceiver.startWakefulService(context, intent?.setComponent(comp))
        resultCode = Activity.RESULT_OK
    }

    companion object {
        fun completeWKfullIntent(intent: Intent?) {
            completeWakefulIntent(intent)
        }
    }

}