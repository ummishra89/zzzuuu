package com.mentorz.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.facebook.login.LoginManager
import com.linkedin.platform.LISessionManager
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.database.DbManager
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Global
import org.jetbrains.anko.doAsync

/**
 * Created by craterzone on 05/07/17.
 */
open class BaseFragment : Fragment() {

    protected val TAG: String = javaClass.simpleName
    open fun networkError(){
        activity?.runOnUiThread {
            DialogUtils.showDialog(activity,getString(R.string.alert),getString(R.string.network_not_available),getString(R.string.ok))
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DbManager.getInstance(MentorzApplication.applicationContext())
    }

    var db : DbManager? = null

    open fun onSessionExpired(){
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        Global.userInterests!!.clear()
                        Global.defaultInterestsMap?.clear()
                        Global.defaultExpertiseMap?.clear()
                        Global.myexpertises!!.clear()
                        MentorzApplication.instance?.prefs?.clear()
                        doAsync {
                            db?.clear()
                        }
                        LoginManager.getInstance().logOut()
                        LISessionManager.getInstance(context).clearSession();

                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finish()
                    })
        }
    }
}
