package com.mentorz.expertise

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.fragments.FragmentFactory
import com.mentorz.model.UserType
import com.mentorz.sinchvideo.BaseActivity
import com.mentorz.utils.Global
import kotlinx.android.synthetic.main.activity_expertise.*

class ExpertiseActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    Global.defaultExpertiseMap = Global.tempExpertiseMap
                    setResult(Activity.RESULT_CANCELED, null)
                    finish()
                }
            }


        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expertise)
        setSupportActionBar(toolbar)
        txtTitle.text = getString(R.string.my_expertise)
        imgBack.setOnClickListener(this)
        txtDone.setOnClickListener(this)
        val userType = intent.getIntExtra("user_type", UserType.DEFAULT)
        if (userType == UserType.FRIEND) {
            txtTitle.text = getString(R.string.mentor_expertise)
            txtDone.visibility = View.GONE
            FragmentFactory.replaceFragment(MentorExpertiseFragment(), R.id.frameContainer, this@ExpertiseActivity)

        } else if (userType == UserType.SELF) {
            txtTitle.text = getString(R.string.my_expertise)
            txtDone.visibility = View.GONE
            FragmentFactory.replaceFragment(MentorExpertiseFragment(), R.id.frameContainer, this@ExpertiseActivity)

        } else {
            txtTitle.text = getString(R.string.my_expertise)
            txtDone.visibility = View.VISIBLE
            FragmentFactory.addFragment(ExpertiseFragment.getInstance(), R.id.frameContainer, this@ExpertiseActivity)

        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            Global.defaultExpertiseMap = Global.tempExpertiseMap
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
    }

}
