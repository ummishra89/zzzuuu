package com.mentorz.userdetails

import android.os.Bundle
import com.mentorz.R
import com.mentorz.fragments.FragmentFactory
import com.mentorz.model.UserType
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener {
            finish()
        }
        val userType = intent.getIntExtra("user_type",0)
        when(userType){
            UserType.FOLLOWERS->{
                txtTitle.text=getString(R.string.followers)
                FragmentFactory.replaceFragment(FollowersFragment(),R.id.frameContainer,this@UserDetailActivity)
            }
            UserType.MENTEE->{
                txtTitle.text=getString(R.string.mentees)
                FragmentFactory.replaceFragment(MenteeFragment(),R.id.frameContainer,this@UserDetailActivity)
            }
            UserType.MENTOR->{
                txtTitle.text=getString(R.string.mentors)
                FragmentFactory.replaceFragment(MentorsFragment(),R.id.frameContainer,this@UserDetailActivity)
            }
            UserType.FOLLOWING->{
                txtTitle.text=getString(R.string.followings)
                FragmentFactory.replaceFragment(FollowingFragment(),R.id.frameContainer,this@UserDetailActivity)
            }
        }
    }
}
