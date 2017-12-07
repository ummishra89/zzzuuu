package com.mentorz.fragments.signup

import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.match.UserProfile
import com.mentorz.model.*
import com.mentorz.requester.RegistrationRequester
import com.mentorz.requester.SocialLoginRequester
import com.mentorz.requester.SocialRegistrationRequester
import com.mentorz.retrofit.listeners.RegistrationListener
import com.mentorz.retrofit.listeners.SocialLoginListener
import com.mentorz.retrofit.listeners.SocialRegistrationListener
import com.mentorz.utils.Prefs

/**
 * Created by aMAN GUPTA on 11/7/17.
 */
class SignupPresenterImpl(view: SignupView) : SignupPresenter, RegistrationListener, SocialRegistrationListener, SocialLoginListener {
    override fun loginErrorAccountFrozen() {
        view.loginErrorAccountFrozen();
        view.hideProgressBar()
    }

    override fun onNetworkFail() {
        view.hideProgressBar()
        view.networkError()
    }
    override fun SocialLoginSuccess(loginResponse: LoginResponse?) {
        view.hideProgressBar()
        MentorzApplication.instance?.prefs?.putLong(Prefs.Key.USER_ID, loginResponse?.userId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.AUTH_TOKEN, loginResponse?.authToken)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.EMAIL, loginResponse?.emailId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, loginResponse?.userProfile?.videoBioHres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, loginResponse?.userProfile?.videoBioLres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_HRES, loginResponse?.userProfile?.hresId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, loginResponse?.userProfile?.lresId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, loginResponse?.userProfile?.name)
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_LOGIN, true)
        if (loginResponse?.userProfile?.birthDate != null) {
            MentorzApplication.instance?.prefs?.putString(Prefs.Key.BIRTH_DATE, loginResponse.userProfile!!.birthDate)
        }
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_LOGIN, true)
        if (loginResponse?.hasInterests!! && loginResponse.hasValues!!) {
            MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.HAS_INTERESTS, true)
            MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.HAS_VALUES, true)
            view.socialRegistrationSuccess()
        } else {
            view.openGetStaredActivity()
        }
    }

    override fun SocialLoginUnauthorized() {
        view.hideProgressBar()
    }

    override fun socialRegistrationSuccess() {
        val socialLoginRequest = SocialLoginRequest()
        socialLoginRequest.socialId = socialID
        socialLoginRequest.socialSource = socialSource
        socialLoginRequest.userProfile = UserProfile("", basicInfo, birthDay, name)
        socialLoginRequest.deviceInfo = DeviceInfo()
        socialLoginRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        val socialLoginRequester = SocialLoginRequester(this, this, socialLoginRequest)
        socialLoginRequester.execute()
    }

    override fun SocialRegistrationConflict() {
        view.alreadyRegister()
        view.hideProgressBar()
    }

    override fun registerSuccess() {
        view.validSignUp()
        view.hideProgressBar()
    }

    override fun registerConflict() {
        view.alreadyRegister()
        view.hideProgressBar()
    }

    val view: SignupView = view

    override fun validateSignUp(name: String, email: String, password: String, basicInfo: String, birthDay: String, isSocialSignup: Boolean, socialID: String, socialSource: String) {
        if (TextUtils.isEmpty(name)) {
            view.enterName()
        } else if (TextUtils.isEmpty(email)) {
            view.enterEmail()
        } else if (!isValidEmailAddress(email)) {
            view.enterValidEmail()
        } else if (TextUtils.isEmpty(password) && !isSocialSignup) {
            view.enterPassword()
        } else if (password.length < 6 && !isSocialSignup) {
            view.shortPassword()
        } else if (isSocialSignup) {
            socialSignup(name, email, basicInfo, birthDay, socialID, socialSource)
        } else {
            signup(name, email, password, basicInfo, birthDay)
        }
    }

    var name = ""
    var email = ""
    var basicInfo = ""
    var birthDay = ""
    var socialID = ""
    var socialSource = ""

    private fun socialSignup(name: String, email: String, basicInfo: String, birthDay: String, socialID: String, socialSource: String) {
        view.showProgressBar()
        this.name = name
        this.email = email
        this.basicInfo = basicInfo
        this.birthDay = birthDay
        this.socialID = socialID
        this.socialSource = socialSource
        val socialRegistrationRequest = SocialRegistrationRequest()
        socialRegistrationRequest.emailId = email
        socialRegistrationRequest.socialId = socialID
        socialRegistrationRequest.socialSource = socialSource
        socialRegistrationRequest.deviceInfo = DeviceInfo()
        socialRegistrationRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        socialRegistrationRequest.userProfile = UserProfile()
        socialRegistrationRequest.userProfile?.name = name
        socialRegistrationRequest.userProfile?.basicInfo = basicInfo
        socialRegistrationRequest.userProfile?.birthDate = birthDay
        val socialRegistrationRequester = SocialRegistrationRequester(this, this, socialRegistrationRequest)
        socialRegistrationRequester.execute()

    }

    private fun signup(name: String, email: String, password: String, basicInfo: String, birthDay: String) {
        view.showProgressBar()
        val registrationRequest = RegistrationRequest()
        registrationRequest.emailId = email
        registrationRequest.password = password
        val userProfileRegistration = UserProfileRegistration()
        userProfileRegistration.name = name
        userProfileRegistration.basicInfo = basicInfo
        userProfileRegistration.birthDate = birthDay
        registrationRequest.userProfile = userProfileRegistration
        registrationRequest.deviceInfo = DeviceInfo()
        registrationRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        val registrationRequester = RegistrationRequester(this, this, registrationRequest)
        registrationRequester.execute()
    }

    fun isValidEmailAddress(email: String): Boolean {
        val ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }
}