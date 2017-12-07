package com.mentorz.activities

import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_my_expertise.*

class MyExpertiseActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_expertise)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener(this)
    }
}
