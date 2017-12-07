package com.mentorz.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.mentorz.R
import com.mentorz.fragments.ThankingOneFragment


/**
 * Created by aMAN GUPTA on 25/7/17.
 */
class ThankingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_thanking)
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()

        ft.setCustomAnimations(R.anim.fade_in,
                R.anim.fade_out)
        ft.replace(R.id.container, ThankingOneFragment())
        ft.commit()
    }
}