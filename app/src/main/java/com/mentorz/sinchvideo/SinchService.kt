package com.mentorz.sinchvideo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.sinch.android.rtc.*
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.video.VideoController
import com.sinch.android.rtc.video.VideoScalingType

/**
 * Created by craterzone on 17/07/17.
 */
class SinchService : Service() {


    private val APP_KEY = "27e3a364-9d07-4060-8e27-2b5d89ec6e33"
    private val APP_SECRET = "4K/VtQHQXUaU83CTR06lLA=="
    private val ENVIRONMENT = "sandbox.sinch.com"

    companion object {

        val CALL_ID = "CALL_ID"
    }

    internal val TAG = SinchService::class.java.simpleName

    private val mSinchServiceInterface = SinchServiceInterface()
    private var mSinchClient: SinchClient? = null
    private var mUserId: String? = null

    private var mListener: StartFailedListener? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        if (mSinchClient != null && mSinchClient!!.isStarted) {
            mSinchClient!!.terminate()
        }
        super.onDestroy()
    }


    private fun start(userName: String) {
        if (mSinchClient == null) {
            mUserId = userName
            mSinchClient = Sinch.getSinchClientBuilder().context(applicationContext).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build()

            mSinchClient!!.setSupportCalling(true)
            mSinchClient!!.startListeningOnActiveConnection()
            mSinchClient!!.setSupportPushNotifications(true)
            mSinchClient!!.setSupportManagedPush(true)
            mSinchClient!!.setSupportActiveConnectionInBackground(true)

            mSinchClient!!.addSinchClientListener(MySinchClientListener())
            mSinchClient!!.callClient.addCallClientListener(SinchCallClientListener())
            mSinchClient!!.videoController.setResizeBehaviour(VideoScalingType.ASPECT_FILL)
            mSinchClient!!.start()
        }
    }

    private fun stop() {
        if (mSinchClient != null) {
            mSinchClient!!.terminate()
            mSinchClient = null
        }
    }

    private fun isStarted(): Boolean {
        return mSinchClient != null && mSinchClient!!.isStarted
    }

    override fun onBind(intent: Intent): IBinder? {
        return mSinchServiceInterface
    }

    inner class SinchServiceInterface : Binder() {

        fun callUserVideo(userId: String): Call {
            return mSinchClient!!.callClient.callUserVideo(userId)
        }

        fun relayRemotePushNotifPayload(intent: Intent?): NotificationResult? {
            return mSinchClient?.relayRemotePushNotificationPayload(intent)
        }

        val userName: String?
            get() = mUserId

        val isStarted: Boolean
            get() = this@SinchService.isStarted()

        fun startClient(userName: String) {
            start(userName)
        }

        fun stopClient() {
            stop()
        }

        fun setStartListener(listener: StartFailedListener) {
            mListener = listener
        }

        fun getCall(callId: String): Call {
            return mSinchClient!!.callClient.getCall(callId)
        }

        val videoController: VideoController?
            get() {
                if (!isStarted) {
                    return null
                }
                return mSinchClient!!.videoController
            }

        val audioController: AudioController?
            get() {
                if (!isStarted) {
                    return null
                }
                return mSinchClient!!.audioController
            }
    }

    interface StartFailedListener {

        fun onStartFailed(error: SinchError)

        fun onStarted()
    }

    private inner class MySinchClientListener : SinchClientListener {

        override fun onClientFailed(client: SinchClient, error: SinchError) {
            if (mListener != null) {
                mListener!!.onStartFailed(error)
            }
            mSinchClient!!.terminate()
            mSinchClient = null
        }

        override fun onClientStarted(client: SinchClient) {
            Log.d(TAG, "SinchClient started")
            if (mListener != null) {
                mListener!!.onStarted()
            }
        }

        override fun onClientStopped(client: SinchClient) {
            Log.d(TAG, "SinchClient stopped")
        }

        override fun onLogMessage(level: Int, area: String, message: String) {
            when (level) {
                Log.DEBUG -> Log.d(area, message)
                Log.ERROR -> Log.e(area, message)
                Log.INFO -> Log.i(area, message)
                Log.VERBOSE -> Log.v(area, message)
                Log.WARN -> Log.w(area, message)
            }
        }

        override fun onRegistrationCredentialsRequired(client: SinchClient,
                                                       clientRegistration: ClientRegistration) {
        }
    }

    private inner class SinchCallClientListener : CallClientListener {

        override fun onIncomingCall(callClient: CallClient, call: Call) {
            Log.d(TAG, "Incoming call")
            val intent = Intent(this@SinchService, IncomingCallScreenActivity::class.java)
            intent.putExtra(CALL_ID, call.callId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@SinchService.startActivity(intent)
        }

    }

}