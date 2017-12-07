package com.mentorz.sinchvideo

import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Toast
import com.mentorz.R
import kotlinx.android.synthetic.main.main.*


class PlaceCallActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        callButton.isEnabled = false
        callButton.setOnClickListener(buttonClickListener)
        stopButton.setOnClickListener(buttonClickListener)
    }

    override fun onServiceConnected() {
        loggedInName.text = sinchServiceInterface.userName
        callButton.isEnabled = true
    }

    public override fun onDestroy() {
        if (sinchServiceInterface != null) {
            sinchServiceInterface.stopClient()
        }
        super.onDestroy()
    }

    private fun stopButtonClicked() {
        if (sinchServiceInterface != null) {
            sinchServiceInterface.stopClient()
        }
        finish()
    }

    fun callButtonClicked() {

        val userName = callName.text.toString()
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show()
            return
        }

        val call = sinchServiceInterface.callUserVideo(userName)
        val callId = call.callId

        val callScreen = Intent(this, CallScreenActivity::class.java)
        callScreen.putExtra(SinchService.CALL_ID, callId)
        startActivity(callScreen)
    }

    private val buttonClickListener = OnClickListener { v ->
        when (v.id) {
            callButton.id -> callButtonClicked()

            stopButton.id -> stopButtonClicked()
        }
    }
}
