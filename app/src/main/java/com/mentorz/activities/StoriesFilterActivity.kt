package com.mentorz.activities

import android.os.Bundle
import com.mentorz.R
import com.mentorz.fragments.FragmentFactory
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.stories.StoriesFilterFragment
import kotlinx.android.synthetic.main.activity_stories_filter.*

class StoriesFilterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stories_filter)
        setSupportActionBar(toolbar)
        FragmentFactory.replaceFragment(StoriesFilterFragment(), R.id.frameContainer, this)
        imgBack.setOnClickListener {
            finish()
        }
    }
}
