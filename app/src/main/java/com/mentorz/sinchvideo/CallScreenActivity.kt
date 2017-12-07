package com.mentorz.sinchvideo

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import com.mentorz.R
import com.sinch.android.rtc.AudioController
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import com.sinch.android.rtc.calling.CallState
import com.sinch.android.rtc.video.VideoCallListener
import kotlinx.android.synthetic.main.callscreen.*
import java.util.*

class CallScreenActivity : BaseActivity() {

    private var mAudioPlayer: AudioPlayer? = null
    private var mTimer: Timer? = null
    private var mDurationTask: UpdateCallDurationTask? = null

    private var mCallId: String? = null
    private var mCallStart: Long = 0
    private var mAddedListener = false
    private var mVideoViewsAdded = false
    private var audioController: AudioController? = null


    private inner class UpdateCallDurationTask : TimerTask() {

        override fun run() {
            this@CallScreenActivity.runOnUiThread { updateCallDuration() }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart)
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME)
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.callscreen)

        mAudioPlayer = AudioPlayer(this@CallScreenActivity)

        hangupButton.setOnClickListener { endCall() }
        speaker.setOnClickListener {
            if (speaker.isSelected) {
                speaker.isSelected = false
                audioController?.disableSpeaker()
            } else {
                speaker.isSelected = true
                audioController?.enableSpeaker()
            }
        }
        rotateCamera.setOnClickListener {
            rotateCamera.isSelected = !rotateCamera.isSelected
            sinchServiceInterface.videoController?.toggleCaptureDevicePosition()
        }
        muteButton.setOnClickListener {
            if (!muteButton.isSelected) {
                audioController?.mute()
            } else {
                audioController?.unmute()
            }
            muteButton.isSelected = !muteButton.isSelected
        }

        mCallId = intent.getStringExtra(SinchService.CALL_ID)
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis()
        }
    }

    public override fun onServiceConnected() {
        val call = sinchServiceInterface.getCall(mCallId!!)
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(SinchCallListener())
                mAddedListener = true
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.")
            finish()
        }

        updateUI()
    }

    private fun updateUI() {
        if (sinchServiceInterface == null) {
            return  // early
        }

        val call = sinchServiceInterface.getCall(mCallId!!)
        if (call != null) {
            remoteUser!!.text = call.remoteUserId
            callState!!.text = call.state.toString()
            if (call.state === CallState.ESTABLISHED) {
                addVideoViews()
            }
        }
    }

    public override fun onStop() {
        super.onStop()
        mDurationTask!!.cancel()
        mTimer!!.cancel()
        removeVideoViews()
    }

    public override fun onStart() {
        super.onStart()
        mTimer = Timer()
        mDurationTask = UpdateCallDurationTask()
        mTimer!!.schedule(mDurationTask, 0, 500)
        updateUI()
    }

    override fun onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private fun endCall() {
        mAudioPlayer!!.stopProgressTone()
        sinchServiceInterface.getCall(mCallId!!).hangup()
        finish()
    }

    private fun formatTimespan(timespan: Long): String {
        val totalSeconds = timespan / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    private fun updateCallDuration() {
        if (mCallStart > 0) {
            callDuration!!.text = formatTimespan(System.currentTimeMillis() - mCallStart)
        }
    }

    private fun addVideoViews() {
        if (mVideoViewsAdded || sinchServiceInterface == null) {
            return  //early
        }

        val vc = sinchServiceInterface.videoController
        if (vc != null) {
            localVideo.addView(vc.localView)
            localVideo.setOnClickListener { vc.toggleCaptureDevicePosition() }
            remoteVideo.addView(vc.remoteView)
            mVideoViewsAdded = true
        }
    }

    private fun removeVideoViews() {
        if (sinchServiceInterface == null) {
            return  // early
        }

        val vc = sinchServiceInterface.videoController
        if (vc != null) {

            remoteVideo.removeView(vc.remoteView)
            localVideo.removeView(vc.localView)
            mVideoViewsAdded = false
        }
    }

    private inner class SinchCallListener : VideoCallListener, CallListener {

        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.d(TAG, "Call ended. Reason: " + cause.toString())
            mAudioPlayer!!.stopProgressTone()
            volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
            val endMsg = "Call ended: " + call.details.toString()
           // Toast.makeText(this@CallScreenActivity, endMsg, Toast.LENGTH_LONG).show()

            endCall()
        }

        override fun onCallEstablished(call: Call) {
            Log.d(TAG, "Call established")
            mAudioPlayer!!.stopProgressTone()
            callState!!.text = call.state.toString()
            volumeControlStream = AudioManager.STREAM_VOICE_CALL
            audioController = sinchServiceInterface.audioController
            audioController?.disableSpeaker()
            mCallStart = System.currentTimeMillis()
            Log.d(TAG, "Call offered video: " + call.details.isVideoOffered)
        }

        override fun onCallProgressing(call: Call) {
            Log.d(TAG, "Call progressing")
            mAudioPlayer!!.playProgressTone()
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM
        }

        override fun onVideoTrackAdded(call: Call) {
            Log.d(TAG, "Video track added")
            addVideoViews()
        }
    }

    companion object {

        internal val TAG = CallScreenActivity::class.java.simpleName
        internal val CALL_START_TIME = "callStartTime"
        internal val ADDED_LISTENER = "addedListener"
    }
}
