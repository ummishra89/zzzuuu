package com.mentorz.sinchvideo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mentorz.R
import com.mentorz.activities.HomeActivity
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.video.VideoCallListener
import kotlinx.android.synthetic.main.incoming.*

class IncomingCallScreenActivity : BaseActivity() {
    private var mCallId: String? = null
    private var mAudioPlayer: AudioPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incoming)

        answerButton.setOnClickListener(mClickListener)
        declineButton.setOnClickListener(mClickListener)

        mAudioPlayer = AudioPlayer(this)
        mAudioPlayer!!.playRingtone()
        mCallId = intent.getStringExtra(SinchService.CALL_ID)
    }

    override fun onServiceConnected() {
        val call = sinchServiceInterface.getCall(mCallId!!)
        if (call != null) {
            call.addCallListener(SinchCallListener())
            remoteUser.text = call.remoteUserId

        } else {
            Log.e(TAG, "Started with invalid callId, aborting")
            finish()
        }
    }

    private fun answerClicked() {
        mAudioPlayer!!.stopRingtone()
        val call = sinchServiceInterface.getCall(mCallId!!)
        if (call != null) {
            call.answer()
            val intent = Intent(this, CallScreenActivity::class.java)
            intent.putExtra(SinchService.CALL_ID, mCallId)
            startActivity(intent)
        } else {
            finish()
        }
    }

    private fun declineClicked() {
        mAudioPlayer!!.stopRingtone()
        val call = sinchServiceInterface.getCall(mCallId!!)
        call.hangup()
        val intent: Intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private inner class SinchCallListener : VideoCallListener {

        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.d(TAG, "Call ended, cause: " + cause.toString())
            mAudioPlayer!!.stopRingtone()
            finish()
        }

        override fun onCallEstablished(call: Call) {
            Log.d(TAG, "Call established")
        }

        override fun onCallProgressing(call: Call) {
            Log.d(TAG, "Call progressing")
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM
        }

        override fun onVideoTrackAdded(call: Call) {
            // Display some kind of icon showing it's a video call
        }
    }

    private val mClickListener = View.OnClickListener { v ->
        when (v.id) {
            answerButton.id -> answerClicked()
            declineButton.id -> declineClicked()
        }
    }

    companion object {

        internal val TAG = IncomingCallScreenActivity::class.java.simpleName
    }
}
