package com.mentorz.fcm

import android.content.Intent
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.mentorz.MentorzApplication
import com.mentorz.model.DeviceInfo
import com.mentorz.model.FirebaseToken
import com.mentorz.requester.UpdatePushTokenRequester
import com.mentorz.retrofit.listeners.UpdatePushTokenListener
import com.mentorz.utils.NotificationUtils
import com.mentorz.utils.Prefs

/**
 * Created by craterzone on 17/07/17.
 */
class MentorxFirebaseInstanceService : FirebaseInstanceIdService(), UpdatePushTokenListener {
    override fun onNetworkFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSessionExpired() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val refreshedToken: String? = FirebaseInstanceId.getInstance()?.token
        Log.d("MentorxFirebaseService", "Refreshed token: " + refreshedToken)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.DEVICE_TOKEN, refreshedToken)
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_PUSH_TOKEN_UPDATED,false);
        sendRegistrationToServer(refreshedToken);
    }

    private fun  sendRegistrationToServer(refreshedToken: String?) {
        val deviceInfo = DeviceInfo()
        deviceInfo.deviceToken = refreshedToken
        if(MentorzApplication.instance?.prefs?.isLogin()!!){
            UpdatePushTokenRequester(deviceInfo,this).execute()
        }
    }
}
