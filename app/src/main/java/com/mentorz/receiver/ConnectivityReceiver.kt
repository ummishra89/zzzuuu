package com.mentorz.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mentorz.manager.PubNubManager
import com.mentorz.pubnub.PubNubManagerService

/**
 * Created by craterzone on 11/3/2017.
 */
class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, PubNubManagerService::class.java))
    }
}