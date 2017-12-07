package com.mentorz.activities.getstarted

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.adapter.GetStartedPagerAdapter
import kotlinx.android.synthetic.main.activity_get_started.*


/**
 * Created by aMAN GUPTA on 21/7/17.
 */
class GetStartedActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_get_started)
        photos_viewpager.adapter = GetStartedPagerAdapter(supportFragmentManager)
        photos_tab_layout.setupWithViewPager(photos_viewpager, true)
    }

//    fun setIndicatorHeight(y: Float){
////        photos_tab_layout.y = y
//    }


}