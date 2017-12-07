package com.mentorz.gcm

import android.content.Intent
import com.google.android.gms.iid.InstanceIDListenerService

/**
 * Created by umesh on 19/07/17.
 */
class MyInstanceIDListenerService : InstanceIDListenerService() {
    override fun onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        val intent = Intent(this, RegistrationIntentService::class.java)
        startService(intent)
    }
}