package com.mentorz.gcm

import android.os.Bundle
import android.util.Log
import com.google.android.gms.gcm.GcmListenerService

/**
 * Created by umesh on 19/07/17.
 */
class MyGcmListenerService : GcmListenerService() {
    private val TAG: String = javaClass.simpleName
    override fun onMessageReceived(p0: String?, p1: Bundle?) {
        super.onMessageReceived(p0, p1)
        Log.d(TAG, "inside onMessageReceived")
    }
}