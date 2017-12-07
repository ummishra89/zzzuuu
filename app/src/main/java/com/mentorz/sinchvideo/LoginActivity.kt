package com.mentorz.sinchvideo


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.mentorz.R
import com.sinch.android.rtc.SinchError
import kotlinx.android.synthetic.main.sinch_login.*

class LoginActivity : BaseActivity(), SinchService.StartFailedListener {


    var mSpinner: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sinch_login)

        loginButton.isEnabled = false
        loginButton.setOnClickListener { loginClicked() }
    }

    override fun onServiceConnected() {
        loginButton.isEnabled = true
        sinchServiceInterface.setStartListener(this)
    }

    override fun onPause() {
        if (mSpinner != null) {
            mSpinner?.dismiss()
        }
        super.onPause()
    }

    override fun onStartFailed(error: SinchError) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
        if (mSpinner != null) {
            mSpinner?.dismiss()
        }
    }

    override fun onStarted() {
        openPlaceCallActivity()
    }

    private fun loginClicked() {
        val userName = loginName.text.toString()

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
            return
        }

        if (!sinchServiceInterface.isStarted) {
            sinchServiceInterface.startClient(userName)
            showSpinner()
        } else {
            openPlaceCallActivity()
        }
    }

    private fun openPlaceCallActivity() {
        val mainActivity = Intent(this, PlaceCallActivity::class.java)
        startActivity(mainActivity)
    }

    private fun showSpinner() {
        mSpinner = ProgressDialog(this)
        mSpinner?.setTitle("Logging in")
        mSpinner?.setMessage("Please wait...")
        mSpinner?.show()
    }
}
