package com.mentorz.fragments.changepassword

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.activities.HomeActivity
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.activities.getstarted.GetStartedActivity
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.login.LoginFragment
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import com.mentorz.fragments.FragmentFactory
import kotlinx.android.synthetic.main.fragment_change_password.*

/**
 * Created by aMAN GUPTA on 12/7/17.
 */
class ChangePasswordFragment : BaseFragment(), ChangePasswordView, View.OnClickListener, View.OnTouchListener {

    override fun onSessionExpired() {
        activity?.runOnUiThread {
            DialogUtils.
                    showDialog(activity, "", getString(R.string.session_expired_message), getString(R.string.ok), View.OnClickListener {
                        DialogUtils.dismiss()
                        startActivity(Intent(activity, AuthenticationActivity::class.java))
                        activity.finish()
                    })
        }
    }

    override fun openGetStaredActivity() {
        activity.runOnUiThread {
            val intent = Intent(activity, GetStartedActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
    }

    override fun loginSuccess() {
        val intent: Intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

    override fun invalidVerificationCode() {
        activity.showSnackBar(root, getString(R.string.either_verification_code_is_expired_or_not_valid))
    }

    override fun enterVerificationCode() {
        Snackbar.make(root, getString(R.string.please_enter_verification_code), Snackbar.LENGTH_SHORT).show()
    }

    override fun verificationCodeLength() {
        Snackbar.make(root, getString(R.string.verification_code_must_be_of), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterNewPassword() {
        Snackbar.make(root, getString(R.string.please_enter_new_password), Snackbar.LENGTH_SHORT).show()
    }

    override fun shortPassword() {
        Snackbar.make(root, getString(R.string.password_too_short), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterConfirmPassword() {
        Snackbar.make(root, getString(R.string.please_confirm_your_password), Snackbar.LENGTH_SHORT).show()
    }

    override fun passwordNotMatch() {
        Snackbar.make(root, getString(R.string.your_passwords_do_not_match), Snackbar.LENGTH_SHORT).show()
    }

    override fun validLogin() {
        presenter.loginApi(email, et_new_password.text.toString().trim())
    }

    val presenter: ChangePasswordPresenterImpl = ChangePasswordPresenterImpl(this)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_change_password, container, false)
    }

    var email = ""

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        if (arguments != null) {
            email = arguments.getString(Constant.EMAIL)
        }
        ctv_go_back.setOnClickListener(this)
        ctv_login.setOnClickListener(this)
        root.setOnTouchListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ctv_go_back -> {
                FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
            }
            R.id.ctv_login -> {
                presenter.validateLogin(et_verification_code.text.toString().trim(), et_new_password.text.toString().trim(), et_confirm_password.text.toString().trim())
            }
        }

    }

    override fun showProgressBar() {
        activity.runOnUiThread {
            progress.visibility = View.VISIBLE
        }
    }

    override fun hideProgressBar() {
        activity.runOnUiThread {
            progress.visibility = View.GONE
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        activity.hideKeyBoard()
        return true
    }
}