package com.mentorz.activities.tutorial

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.adapter.TutorialAdapter
import kotlinx.android.synthetic.main.activity_get_started.*

/**
 * Created by aMAN GUPTA on 21/7/17.
 */

class TutorialActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_tutorial)
        photos_viewpager.adapter = TutorialAdapter(supportFragmentManager)
        photos_tab_layout.setupWithViewPager(photos_viewpager, true)

    }
}