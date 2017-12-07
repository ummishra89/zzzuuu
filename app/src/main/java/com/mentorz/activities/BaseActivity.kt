package com.mentorz.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.utils.DialogUtils
import io.vrinda.kotlinpermissions.PermissionsActivity

/**
 * Created by craterzone on 05/07/17.
 */

open class BaseActivity : PermissionsActivity() {
    protected val TAG: String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }
   open fun networkError(){
       runOnUiThread {

           DialogUtils.showDialog(this,getString(R.string.alert),getString(R.string.network_not_available),getString(R.string.ok))
       }

    }
    open fun onSessionExpired(){
        runOnUiThread {
            DialogUtils.
                    showDialog(this, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(this, AuthenticationActivity::class.java))
                        finish()
                    })
        }
    }
}