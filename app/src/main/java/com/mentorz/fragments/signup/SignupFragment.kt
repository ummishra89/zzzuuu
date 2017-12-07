package com.mentorz.fragments.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.mentorz.R
import com.mentorz.activities.getstarted.GetStartedActivity
import com.mentorz.customviews.CustomAlertDialog
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.login.LoginFragment
import com.mentorz.utils.Constant
import com.mentorz.fragments.FragmentFactory
import kotlinx.android.synthetic.main.fragment_signup.*
import java.util.*

/**
 * Created by aMAN GUPTA on 10/7/17.
 */
class SignupFragment : BaseFragment(), View.OnClickListener, SignupView, CustomAlertDialog.CustomAlertDialogListener, View.OnTouchListener {
    override fun loginErrorAccountFrozen() {
        loginErrorAccountFrozen()
    }

    override fun networkError() {
        super.networkError()
    }
    override fun openGetStaredActivity() {
        activity.runOnUiThread {
            val intent = Intent(activity, GetStartedActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
    }

    override fun socialRegistrationSuccess() {
        activity.runOnUiThread {
            val intent = Intent(activity, GetStartedActivity::class.java)
            startActivity(intent)
            activity.finish()
        }
    }

    override fun alreadyRegister() {
        activity.showSnackBar(root, getString(R.string.you_are_already_register))
    }

    override fun onButtonClickListener() {
        FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
    }

    override fun validSignUp() {
        activity.runOnUiThread {
            val dialog: CustomAlertDialog = CustomAlertDialog(context, getString(R.string.almost_there), getString(R.string.go_to_your_inbox), getString(R.string.ok), this)
            dialog.show()
        }
    }

    val presenter = SignupPresenterImpl(this)
    var birthDay = ""
    var isSocialSignUp = false
    var socialId = ""
    var name = ""
    var email = ""
    var socialSource = ""

    override fun enterName() {
        Snackbar.make(root, getString(R.string.please_enter_your_name), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterEmail() {
        Snackbar.make(root, getString(R.string.please_enter_email), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterValidEmail() {
        Snackbar.make(root, getString(R.string.please_enter_a_valid_email), Snackbar.LENGTH_SHORT).show()
    }

    override fun enterPassword() {
        Snackbar.make(root, getString(R.string.please_enter_a_password), Snackbar.LENGTH_SHORT).show()
    }

    override fun shortPassword() {
        Snackbar.make(root, getString(R.string.password_too_short), Snackbar.LENGTH_SHORT).show()
    }

    val date = DatePickerDialog.OnDateSetListener { p0, p1, p2, p3 ->
        et_birthday.setText("$p3/$p2/$p1")
        val calender = GregorianCalendar(p1, p2, p3)
        birthDay = calender.timeInMillis.toString()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ctv_login -> {
                FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
            }
            R.id.ctv_signup -> {
                presenter.validateSignUp(et_name.text.toString().trim(), et_email.text.toString().trim(), et_password.text.toString().trim(), et_headline.text.toString().trim(),et_phone.text.toString().trim(), isSocialSignUp, socialId, socialSource)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (arguments != null) {
            isSocialSignUp = arguments.getBoolean(Constant.IS_SOCIAL_SIGNUP, false)
            socialId = arguments.getString(Constant.SOCIAL_ID, "")
            name = arguments.getString(Constant.NAME, "")
            socialSource = arguments.getString(Constant.SOCIAL_SOURCE, "")
            email = arguments.getString(Constant.EMAIL, "social_id@mentorz.com")


        }
        return inflater!!.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        et_name.setText(name)
        et_email.setText(email)
        if(et_email.text.toString().equals("social_id@mentorz.com")) {
            et_email.isEnabled = false
        }
        ctv_login.setOnClickListener(this)
        ctv_signup.setOnClickListener(this)
        val minimumDate = Calendar.getInstance()
        minimumDate.set(Calendar.YEAR, 1900)
        minimumDate.set(Calendar.MONTH, Calendar.JANUARY)
        minimumDate.set(Calendar.DAY_OF_MONTH, 1)

        et_birthday.setOnClickListener {
            val calender: Calendar = Calendar.getInstance()

          var datePicketDialog = DatePickerDialog(context, date, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH))
            datePicketDialog.getDatePicker().maxDate=System.currentTimeMillis()
            datePicketDialog.getDatePicker().minDate=minimumDate.timeInMillis
            datePicketDialog.show()

        }
        if (!TextUtils.isEmpty(email)) {
            et_email.isEnabled = false
        }
        if (isSocialSignUp) {
            et_password.visibility = View.GONE
        }
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