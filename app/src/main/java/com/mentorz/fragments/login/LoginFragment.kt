package com.mentorz.fragments.login


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.craterzone.logginglib.manager.LoggerManager
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.HomeActivity
import com.mentorz.activities.getstarted.GetStartedActivity
import com.mentorz.extensions.hideKeyBoard
import com.mentorz.extensions.showSnackBar
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.FragmentFactory
import com.mentorz.fragments.forgotpassword.ForgotPasswordFragment
import com.mentorz.fragments.signupvia.SignupViaFragment
import com.mentorz.match.UserProfile
import com.mentorz.model.SocialLoginRequest
import com.mentorz.model.UpdateProfileRequest
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.requester.UpdateProfileRequester
import com.mentorz.retrofit.listeners.UpdateProfileListener
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import com.mentorz.uploadfile.FileUploadRequester
import com.mentorz.utils.Constant
import com.mentorz.utils.DialogUtils
import io.vrinda.kotlinpermissions.PermissionCallBack


import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.doAsync
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL


/**
 * A simple [Fragment] subclass.
 */

class LoginFragment : BaseFragment(), View.OnClickListener, LoginView, View.OnTouchListener, UpdateProfileListener, FileUploadListener {
    override fun fileUploadSuccess(token: String, fileType: String) {

    }

    override fun fileUploadFail() {

    }

    override fun onNetworkFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateProfileSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateProfileFail() {

    }

    override fun updateProfileImage(token: String, fileType: String) {

            activity.runOnUiThread {
                when (fileType) {
                    FileType.PROFILE_PICTURE -> {
                        updateProfileApi(token)
                        SignedUrlRequester(FileType.PROFILE_PICTURE, null, null, token).execute()
                    }


                }
            }

    }

    private fun updateProfileApi(token: String) {
        val updateProfileRequest = UpdateProfileRequest()
        updateProfileRequest.name = MentorzApplication.instance?.prefs?.getUserName()
        updateProfileRequest.basicInfo = MentorzApplication.instance?.prefs?.getBasicInfo()
        updateProfileRequest.hresId = token
        updateProfileRequest.lresId = token
        updateProfileRequest.videoBioLres = MentorzApplication.instance?.prefs?.getProfileVideoLres()
        updateProfileRequest.videoBioHres = MentorzApplication.instance?.prefs?.getProfileVideoHres()
        val updateProfileRequester = UpdateProfileRequester(this, this, updateProfileRequest)
        updateProfileRequester.execute()
    }
    override fun loginErrorAccountFrozen() {
        activity.runOnUiThread{
            DialogUtils.showDialog(activity,resources.getString(R.string.error),resources.getString(R.string.account_frozen_mgs),resources.getString(R.string.contact_us),resources.getString(R.string.ok),this@LoginFragment,this@LoginFragment)
        }

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

    override fun socialLoginFail() {

        activity.showSnackBar(root, getString(R.string.please_signup_then_you_can_login))
    }

    override fun loginSuccess() {
        LoggerManager.getInstance(activity).startLogging();
        downloadSocialProfileImage(MentorzApplication.instance?.prefs?.getProfilePictureLres())


    }



    override fun loginFail() {

        Snackbar.make(root, getString(R.string.please_verify_your_email), Snackbar.LENGTH_SHORT).show()
    }

    val presenter = LoginPresenterImpl(this)
    var callbackmanager: CallbackManager? = null
    val linkedinUrl: String = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url)"


    override fun validLogin() {
        hideKeyBoard()

        presenter.loginApi(et_email.text.toString().trim(), et_password.text.toString().trim())

    }

    override fun enterEmail() {
        activity.showSnackBar(root, getString(R.string.please_enter_email))
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
    fun sendEmail(){
        try {
            val email = Intent(Intent.ACTION_SEND)
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@mintorz.com"))
            email.putExtra(Intent.EXTRA_SUBJECT, "")
            email.putExtra(Intent.EXTRA_TEXT, "")
            email.type = "message/rfc822"
            startActivity(Intent.createChooser(email, "Choose an Email client :"))
        }
    catch (exception:Exception){

    }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ctv_button->{
                sendEmail();
                DialogUtils.dismiss()
            }
            R.id.ctv_button2->{
                DialogUtils.dismiss()
            }
            R.id.ctv_signup -> {
                FragmentFactory.replaceFragment(SignupViaFragment(), R.id.container, context)
            }
            R.id.ctv_forgot_password -> {
                FragmentFactory.replaceFragment(ForgotPasswordFragment(), R.id.container, context)
            }
            R.id.ctv_login -> {
                presenter.validateLogin(et_email.text.toString().trim(), et_password.text.toString().trim())
            }
            R.id.ctv_facebook -> {

                fbLogin()
            }
            R.id.ctv_linkedin -> {

                linkedinLogin()
            }

        }
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        checkForWritePermission(false)
        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        ctv_forgot_password.setOnClickListener(this)
        ctv_signup.setOnClickListener(this)
        ctv_login.setOnClickListener(this)
        ctv_facebook.setOnClickListener(this)
        ctv_linkedin.setOnClickListener(this)
        callbackmanager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackmanager, object : FacebookCallback<LoginResult> {
            override fun onError(error: FacebookException?) {
                Snackbar.make(root, getString(R.string.error), Snackbar.LENGTH_SHORT).show()

            }

            override fun onSuccess(result: LoginResult?) {
                val graphRequest: GraphRequest = GraphRequest.newMeRequest(result?.accessToken) { jsonObject, response ->
                    val socialLoginRequest: SocialLoginRequest = SocialLoginRequest()
                    socialLoginRequest.userProfile = UserProfile()
                    try {
                        socialLoginRequest.socialId = jsonObject?.getString("id")
                        socialLoginRequest.socialSource = Constant.SOCIAL_SOURCE_FB
                        socialLoginRequest.userProfile?.name = jsonObject?.getString("name")
                        if (jsonObject?.has("picture")!!) {
                            socialLoginRequest.userProfile?.hresId = JSONObject(JSONObject(jsonObject.getString("picture")).getString("data")).getString("url")
                            socialLoginRequest.userProfile?.lresId = JSONObject(JSONObject(jsonObject.getString("picture")).getString("data")).getString("url")
                        }
                        socialLoginRequest.userProfile?.birthDate = jsonObject.getString("birthday")

                    } catch (e: JSONException) {
                    } finally {
                        presenter.socialLoginApi(socialLoginRequest)

                    }
                }
                graphRequest.parameters.putString("fields", "email,name,birthday,about,picture.type(large)")
                graphRequest.executeAsync()
            }

            override fun onCancel() {
                Snackbar.make(root, getText(R.string.canceled_by_user), Snackbar.LENGTH_SHORT).show()

            }

        })
        root.setOnTouchListener(this)
    }

    private fun fbLogin() = LoginManager.getInstance().logInWithReadPermissions(this, mutableListOf("public_profile", "email"))

    private fun linkedinLogin() {
        LISessionManager.getInstance(getApplicationContext())
                .init(activity, buildScope(), object : AuthListener {
                    override fun onAuthSuccess() {

                        val apiHelper = APIHelper.getInstance(getApplicationContext())
                        apiHelper.getRequest(activity, linkedinUrl, object : ApiListener {
                            override fun onApiSuccess(result: ApiResponse) {
                                try {
                                    showResult(result.responseDataAsJson)

//                                    progress.dismiss()

                                } catch (e: Exception) {
                                    e.printStackTrace()

                                }

                            }

                            override fun onApiError(error: LIApiError) {
                                Log.d(TAG, "linkedin error:" + error.apiErrorResponse)
                            }
                        })
                    }

                    override fun onAuthError(error: LIAuthError) {
                        Log.d(TAG, "linkedin error:" + error.toString())

                    }
                }, true)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackmanager?.onActivityResult(requestCode, resultCode, data)
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(activity, requestCode, resultCode, data)
    }

    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS)
    }

    fun showResult(response: JSONObject) {
        val socialLoginRequest: SocialLoginRequest = SocialLoginRequest()
        socialLoginRequest.userProfile = UserProfile()
        try {
            socialLoginRequest.socialId = response.get("id").toString()
            socialLoginRequest.socialSource = Constant.SOCIAL_SOURCE_LINKED_IN
            socialLoginRequest.userProfile?.name = response.get("firstName").toString() + " " + response.get("lastName").toString()
            if (response.has("pictureUrl")) {
                socialLoginRequest.userProfile?.lresId = response.getString("pictureUrl")
                socialLoginRequest.userProfile?.hresId = response.getString("pictureUrl")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        presenter.socialLoginApi(socialLoginRequest)

    }

    override fun showProgressBar() {
        activity.runOnUiThread {
            if (progress != null)
                progress.visibility = View.VISIBLE
        }
    }

    override fun hideProgressBar() {
        activity.runOnUiThread {
            if (progress != null)
                progress.visibility = View.GONE
        }
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        activity.hideKeyBoard()
        return true
    }


    private fun checkForWritePermission(loginComplete :Boolean){

        val permissions: Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (ActivityCompat.checkSelfPermission(context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity,
                permissions,
                5);
    } else {
                LoggerManager.getInstance(activity).startLogging();
        Log.e("DB", "PERMISSION GRANTED");
    }


       // val permissions: Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)

       /* requestPermissions(permissions,5)
        requestPermissions(permissions, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                LoggerManager.getInstance(activity).startLogging();

            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Storage permissions", "Denied")
            }
        })*/

    }


  private fun downloadSocialProfileImage(url :String?){
        doAsync {
            try{
                val url = URL(url)
                val input : InputStream?= BufferedInputStream(url.openStream());
                val out : ByteArrayOutputStream = ByteArrayOutputStream();
                var buf : ByteArray = ByteArray(1024) ;
                var n : Int = input!!.read(buf)
                while (n!=-1)
                {
                    out.write(buf, 0, n);
                    n=input!!.read(buf)
                }
                out.close();
                input.close();
                var  response :ByteArray = out.toByteArray()
                val fos = FileOutputStream(""+LoggerManager.logFilePath()+"/profile_pic.png")
                fos.write(response)
                fos.close()

               // uploadImage(Uri.parse(LoggerManager.logFilePath()+"/profile_pic.png"), FileType.PROFILE_PICTURE)
            }catch (exception :Exception){
                Log.e("LoginPresenterImpl",""+exception.toString())
            }
            finally {
                switchActivity()

            }


        }
    }

    private fun switchActivity(){
        activity.runOnUiThread {
            val intent: Intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            activity.finish()

        }

    }

    private fun uploadImage(fileUri: Uri, fileType: String) {
        val filePath = fileUri.path
        FileUploadRequester(this, filePath, "image/png" , fileType).execute()
    }

}
