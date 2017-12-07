package com.mentorz.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.activities.values.ValuesActivity
import com.mentorz.block.BlockedUsersActivity
import com.mentorz.interest.InterestsActivity
import com.mentorz.sinchvideo.BaseActivity
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : BaseActivity(), View.OnClickListener {
    private val VALUES_REQUEST_CODE = 0
    private val INTERESTS_REQUEST_CODE = 1


    override fun onClick(p0: View?) {
        when (p0?.id) {
            lValues.id -> {
                val intent = Intent(this, ValuesActivity::class.java)
                startActivityForResult(intent, VALUES_REQUEST_CODE)
            }
            lInterests.id -> {
                val intent = Intent(this, InterestsActivity::class.java)
                startActivityForResult(intent, INTERESTS_REQUEST_CODE)
            }
            lBlockedUsers.id -> {
                val intent = Intent(this, BlockedUsersActivity::class.java)
                startActivityForResult(intent, VALUES_REQUEST_CODE)
            }
            imgBack.id -> {
                finish()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        setSupportActionBar(toolbar)
        imgBack.setOnClickListener(this)
        lValues.setOnClickListener(this)
        lInterests.setOnClickListener(this)
        lBlockedUsers.setOnClickListener(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
