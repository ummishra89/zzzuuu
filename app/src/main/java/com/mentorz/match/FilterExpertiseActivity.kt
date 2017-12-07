package com.mentorz.match

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.fragments.FragmentFactory
import com.mentorz.listener.OnUpdateToolBarListener
import com.mentorz.utils.Global
import kotlinx.android.synthetic.main.activity_interests.*

class FilterExpertiseActivity : BaseActivity(), View.OnClickListener, OnUpdateToolBarListener {

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            Global.defaultInterestsMap?.clear()
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }

    }

    override fun showDone() {
        txtDone.visibility = View.VISIBLE
    }

    override fun hideDone() {
        txtDone.visibility = View.GONE
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            imgBack.id -> {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    setResult(Activity.RESULT_CANCELED, Intent())
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)
        setSupportActionBar(toolbar)
        txtTitle.text = getString(R.string.mentor_expertise)
        imgBack.setOnClickListener(this)
        FragmentFactory.addFragment(FilterExpertiseFragment.getInstance(), R.id.frameContainer, this@FilterExpertiseActivity)
    }
}
