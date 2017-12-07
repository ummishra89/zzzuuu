package com.mentorz.fragments.loginsignup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.login.LoginFragment
import com.mentorz.fragments.signupvia.SignupViaFragment
import com.mentorz.fragments.FragmentFactory
import kotlinx.android.synthetic.main.fragment_login_signup.*

/**
 * Created by aMAN GUPTA on 10/7/17.
 */

class LoginSignupFragment : BaseFragment(), View.OnClickListener, LoginSignupView {

    val presenter = LoginSignupPresenterImpl(this)
    override fun networkError() {
        super.networkError()
    }
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ctv_login -> {
                FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
            }
            R.id.ctv_signup -> {
                FragmentFactory.replaceFragment(SignupViaFragment(), R.id.container, context)
            }
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctv_login.setOnClickListener(this)
        ctv_signup.setOnClickListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_login_signup, container, false)
    }
}