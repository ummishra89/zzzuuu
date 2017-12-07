package com.mentorz.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.mentorz.R
import com.mentorz.activities.BaseActivity
import com.mentorz.activities.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {


    private var presenter: LoginPresenter? = null
    override fun networkError() {
        super.networkError()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button.setOnClickListener(this)
        presenter = LoginPresenterImpl(this)
    }


    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress?.visibility = View.GONE
    }

    override fun setUsernameError() {
        username?.error = "Envalid User Name"
    }

    override fun setPasswordError() {
        password?.error = "Invalid Password"
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun onClick(v: View) {
        presenter?.validateCredentials(username?.text.toString(), password?.text.toString())
    }

}
