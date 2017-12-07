package com.mentorz.activities.authentication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.fragments.loginsignup.LoginSignupFragment
import com.mentorz.fragments.FragmentFactory
import java.security.MessageDigest


/**
 * Created by aMAN GUPTA on 10/7/17.
 */
class AuthenticationActivity : BaseActivity(), AuthenticationView {
    val presenter = AuthenticationPresenterImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_authentication)
        FragmentFactory.replaceFragment(LoginSignupFragment(), R.id.container, this)
//        generateHashkey()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment: android.support.v4.app.Fragment? = supportFragmentManager.findFragmentById(R.id.container)
        fragment?.onActivityResult(requestCode, resultCode, data)

    }

}