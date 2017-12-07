package com.mentorz.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.database.DbManager
import com.mentorz.fragments.FragmentFactory
import com.mentorz.me.UserProfileFragment
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_mentor_profile.*
import org.jetbrains.anko.doAsync

class MentorProfileActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                finish()
            }
        }
    }

    var pushType : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_profile)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener(this)
        pushType = intent.getStringExtra("push_type")
        if (!TextUtils.isEmpty(pushType)){
            doAsync {
                dbManager.setReadNotification(intent.getLongExtra("user_id", 0),pushType)
            }
        }
        FragmentFactory.replaceFragment(UserProfileFragment.getInstance(intent.getLongExtra("user_id", 0)), R.id.frameContainer, this)
    }
    var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())

}
