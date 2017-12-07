package com.mentorz.fragments.changepassword

import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.model.DeviceInfo
import com.mentorz.model.LoginRequest
import com.mentorz.model.LoginResponse
import com.mentorz.model.ResetPasswordRequest
import com.mentorz.requester.LoginRequester
import com.mentorz.requester.ResetPasswordRequester
import com.mentorz.retrofit.listeners.LoginListener
import com.mentorz.retrofit.listeners.ResetPasswordListener
import com.mentorz.utils.Prefs

/**
 * Created by aMAN GUPTA on 12/7/17.
 */
class ChangePasswordPresenterImpl(view: ChangePasswordView) : ChangePasswordPresenter, LoginListener, ResetPasswordListener {
    override fun loginErrorAccountFrozen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkFail() {
        view.hideProgressBar()
        view.networkError()
    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun resetPasswordSuccess() {
        view.validLogin()
    }

    override fun resetPasswordFail() {
        view.invalidVerificationCode()
        view.hideProgressBar()
    }

    override fun loginSuccess(loginResponse: LoginResponse?) {
        view.hideProgressBar()
        MentorzApplication.instance?.prefs?.putLong(Prefs.Key.USER_ID, loginResponse?.userId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.AUTH_TOKEN, loginResponse?.authToken)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.EMAIL, loginResponse?.emailId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, loginResponse?.userProfile?.name)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, loginResponse?.userProfile?.videoBioHres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, loginResponse?.userProfile?.videoBioLres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_HRES, loginResponse?.userProfile?.hresId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, loginResponse?.userProfile?.lresId)
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_LOGIN, true)
        if (loginResponse?.userProfile?.birthDate != null) {
            MentorzApplication.instance?.prefs?.putString(Prefs.Key.BIRTH_DATE, loginResponse?.userProfile!!.birthDate)
        }
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_LOGIN, true)
        if (loginResponse?.hasInterests!! && loginResponse.hasValues!!) {
            MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.HAS_INTERESTS, true)
            MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.HAS_VALUES, true)
            view.loginSuccess()
        } else {
            view.openGetStaredActivity()
        }
    }

    override fun loginUnauthorised() {
        view.hideProgressBar()
    }

    override fun validateLogin(verificationCode: String, newPassword: String, confirmPassword: String) {
        if (TextUtils.isEmpty(verificationCode)) {
            view.enterVerificationCode()
        } else if (verificationCode.length != 10) {
            view.verificationCodeLength()
        } else if (TextUtils.isEmpty(newPassword)) {
            view.enterNewPassword()
        } else if (newPassword.length < 6) {
            view.shortPassword()
        } else if (TextUtils.isEmpty(confirmPassword)) {
            view.enterConfirmPassword()
        } else if (newPassword != confirmPassword) {
            view.passwordNotMatch()
        } else {
            val resetPasswordRequest = ResetPasswordRequest(newPassword, verificationCode)
            val resetPasswordRequester = ResetPasswordRequester(this, this, resetPasswordRequest)
            resetPasswordRequester.execute()
            view.showProgressBar()
        }
    }

    override fun loginApi(email: String, password: String) {
        val loginRequest = LoginRequest()
        loginRequest.emailId = email
        loginRequest.password = password
        loginRequest.deviceInfo = DeviceInfo()
        loginRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        val loginRequester = LoginRequester(this, this, loginRequest)
        loginRequester.execute()
    }

    val view: ChangePasswordView = view
}