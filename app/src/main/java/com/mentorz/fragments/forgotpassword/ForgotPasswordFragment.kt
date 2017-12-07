package com.mentorz.fragments.forgotpassword

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.mentorz.R
import com.mentorz.customviews.CustomAlertDialog
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.changepassword.ChangePasswordFragment
import com.mentorz.fragments.login.LoginFragment
import com.mentorz.utils.Constant
import com.mentorz.fragments.FragmentFactory
import kotlinx.android.synthetic.main.fragment_forgot_password.*

/**
 * Created by aMAN GUPTA on 10/7/17.
 */

class ForgotPasswordFragment : BaseFragment(), View.OnClickListener, ForgotPasswordView, CustomAlertDialog.CustomAlertDialogListener, View.OnTouchListener {
    override fun enterNotFount() {
        activity.showSnackBar(root, getString(R.string.please_enter_a_valid_email))
    }

    override fun onButtonClickListener() {
        val changePasswordFragment = ChangePasswordFragment()
        val bundle = Bundle()
        bundle.putString(Constant.EMAIL, et_email.text.toString().trim())
        changePasswordFragment.arguments = bundle
        FragmentFactory.replaceFragment(changePasswordFragment, R.id.container, context)
    }

    val presenter = ForgotPasswordPresenterImpl(this)

    override fun validEmail() {
        activity.runOnUiThread {
            val dialog: CustomAlertDialog = CustomAlertDialog(context, getString(R.string.message), getString(R.string.an_email_has_been_sent_to_your_email_id), getString(R.string.ok), this)
            dialog.show()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ctv_login -> {
                FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
            }
            R.id.submit -> {
                presenter.validateEmail(et_email.text.toString().trim())
            }
        }
    }

    override fun enterEmail() {
        Snackbar.make(root, getString(R.string.please_enter_email), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterValidEmail() {
        Snackbar.make(root, getString(R.string.please_enter_a_valid_email), Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        ctv_login.setOnClickListener(this)
        submit.setOnClickListener(this)
        root.setOnTouchListener(this)

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