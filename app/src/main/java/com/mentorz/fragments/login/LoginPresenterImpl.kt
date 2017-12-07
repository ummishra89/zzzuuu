package com.mentorz.fragments.login

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.system.Os.read
import android.text.TextUtils
import android.util.Log
import com.mentorz.MentorzApplication
import com.mentorz.model.DeviceInfo
import com.mentorz.model.LoginRequest
import com.mentorz.model.LoginResponse
import com.mentorz.model.SocialLoginRequest
import com.mentorz.requester.LoginRequester
import com.mentorz.requester.ProfileImageRequester
import com.mentorz.requester.SocialLoginRequester
import com.mentorz.retrofit.listeners.LoginListener
import com.mentorz.retrofit.listeners.SocialLoginListener
import com.mentorz.utils.DialogUtils
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import retrofit2.http.Url
import java.lang.System.out
import java.net.URL
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import com.craterzone.logginglib.manager.LoggerManager
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Utils
import java.io.*
import java.util.logging.Logger


/**
 * Created by aMAN GUPTA on 11/7/17.
 */

class LoginPresenterImpl(view: LoginView) : LoginPresenter, LoginListener, SocialLoginListener, FileUploadListener {
    override fun onSessionExpired() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fileUploadSuccess(token: String, fileType: String) {
        view.updateProfileImage(token, fileType)    }

    override fun fileUploadFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, loginResponse?.userProfile?.name)

        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, loginResponse?.userProfile?.videoBioHres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, loginResponse?.userProfile?.videoBioLres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_HRES, loginResponse?.userProfile?.hresId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, loginResponse?.userProfile?.lresId)




        MentorzApplication.instance?.prefs?.putString(Prefs.Key.BASIC_INFO, loginResponse?.userProfile?.basicInfo)
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

        view.hideProgressBar()
    }




    fun uploadSocialProfileImage(filePathInStorage:String){
        Environment.getDataDirectory().absolutePath+"/hello/image.png"

    }
    override fun SocialLoginUnauthorized() {
        view.socialLoginFail()
        view.hideProgressBar()
    }

    override fun loginSuccess(loginResponse: LoginResponse?) {
        login(loginResponse)
        view.hideProgressBar()
    }

    private fun login(loginResponse: LoginResponse?) {
        view.hideProgressBar()
        MentorzApplication.instance?.prefs?.putLong(Prefs.Key.USER_ID, loginResponse?.userId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.AUTH_TOKEN, loginResponse?.authToken)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.EMAIL, loginResponse?.emailId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.USER_NAME, loginResponse?.userProfile?.name)

//        if (!TextUtils.isEmpty(loginResponse?.userProfile?.hresId)){
//            SignedUrlRequester(FileType.PROFILE_PICTURE,null,loginResponse?.userProfile?.hresId)
//        }
//        if (!TextUtils.isEmpty(loginResponse?.userProfile?.videoBioLres)){
//            SignedUrlRequester(FileType.PROFILE_VIDEO_THUMBNAIL,null,loginResponse?.userProfile?.videoBioLres)
//        }
//        if (!TextUtils.isEmpty(loginResponse?.userProfile?.videoBioHres)){
//            SignedUrlRequester(FileType.PROFILE_VIDEO,null,loginResponse?.userProfile?.videoBioHres)
//        }

        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, loginResponse?.userProfile?.videoBioHres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, loginResponse?.userProfile?.videoBioLres)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_HRES, loginResponse?.userProfile?.hresId)
        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, loginResponse?.userProfile?.lresId)

        MentorzApplication.instance?.prefs?.putString(Prefs.Key.BASIC_INFO, loginResponse?.userProfile?.basicInfo)
        MentorzApplication.instance?.prefs?.putBoolean(Prefs.Key.IS_LOGIN, true)
        if (loginResponse?.userProfile?.birthDate != null) {
            MentorzApplication.instance?.prefs?.putString(Prefs.Key.BIRTH_DATE, loginResponse?.userProfile?.birthDate)
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
        view.loginFail()
        view.hideProgressBar()
    }


    override fun loginApi(email: String, password: String) {
        view.showProgressBar()
        val loginRequest = LoginRequest()
        loginRequest.emailId = email
        loginRequest.password = password
        loginRequest.deviceInfo = DeviceInfo()
        loginRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        val loginRequester = LoginRequester(this, this, loginRequest)
        loginRequester.execute()
    }

    override fun socialLoginApi(socialLoginRequest: SocialLoginRequest) {
        view.showProgressBar()
        socialLoginRequest.deviceInfo = DeviceInfo()
        socialLoginRequest.deviceInfo?.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
        val socialLoginRequester = SocialLoginRequester(this, this, socialLoginRequest)
        socialLoginRequester.execute()
    }

    val view: LoginView = view
    override fun validateLogin(email: String, password: String) {
        if (TextUtils.isEmpty(email)) {
            view.enterEmail()
        } else if (!isValidEmailAddress(email)) {
            view.enterValidEmail()
        } else if (TextUtils.isEmpty(password)) {
            view.enterPassword()
        } else if (password.length < 6) {
            view.shortPassword()
        } else {
            view.validLogin()
        }

    }

    fun isValidEmailAddress(email: String): Boolean {
        val ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }
}