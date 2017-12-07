package com.mentorz.gcm

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.utils.Prefs


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class RegistrationIntentService : IntentService("RegistrationIntentService") {
    override fun onHandleIntent(p0: Intent?) {
        val instanceID = InstanceID.getInstance(this)
        val token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.DEVICE_TOKEN, token)

    }


}
