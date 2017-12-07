package com.mentorz.fragments.signupvia

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.*
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
import com.mentorz.R
import com.mentorz.fragments.BaseFragment
import com.mentorz.fragments.FragmentFactory
import com.mentorz.fragments.login.LoginFragment
import com.mentorz.fragments.signup.SignupFragment
import com.mentorz.utils.Constant
import kotlinx.android.synthetic.main.fragment_signup_via.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by aMAN GUPTA on 10/7/17.
 */

class SignupViaFragment : BaseFragment(), View.OnClickListener, SignupViaView {
    val presenter = SignupViaPresenterImpl(this)
    var callbackmanager: CallbackManager? = null

    override fun networkError() {
        super.networkError()
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ctv_login -> {
                FragmentFactory.replaceFragment(LoginFragment(), R.id.container, context)
            }
            R.id.ctv_email -> {
                FragmentFactory.replaceFragment(SignupFragment(), R.id.container, context)
            }
            R.id.ctv_linkedin -> {
                signUpViaLinkedIn()
            }
            R.id.ctv_facebook -> {
                signUpViaFacebook()
            }
        }
    }

    private fun signUpViaFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, mutableListOf("public_profile", "email"))
        progress.visibility = View.VISIBLE
    }

    val linkedinUrl: String = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address)"

    private fun signUpViaLinkedIn() {
        progress.visibility = View.VISIBLE
        LISessionManager.getInstance(activity.applicationContext).init(activity, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                val apiHelper = APIHelper.getInstance(FacebookSdk.getApplicationContext())
                apiHelper.getRequest(activity, linkedinUrl, object : ApiListener {
                    override fun onApiSuccess(result: ApiResponse) {
                        try {
                            showResult(result.responseDataAsJson)
                            progress.visibility = View.GONE

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onApiError(error: LIApiError) {
                        Log.d("aaa", "error")
                        progress.visibility = View.GONE

                    }
                })
            }

            override fun onAuthError(error: LIAuthError) {
                Log.d("aaa", "error")
                progress.visibility = View.GONE
                // Handle authentication errors
            }
        }, true)
    }

    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackmanager?.onActivityResult(requestCode, resultCode, data)
        LISessionManager.getInstance(activity.applicationContext).onActivityResult(activity, requestCode, resultCode, data)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_signup_via, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        ctv_login.setOnClickListener(this)
        ctv_email.setOnClickListener(this)
        ctv_linkedin.setOnClickListener(this)
        ctv_facebook.setOnClickListener(this)
        callbackmanager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackmanager, object : FacebookCallback<LoginResult> {
            override fun onError(error: FacebookException?) {
                Snackbar.make(root, getString(R.string.error), Snackbar.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }

            override fun onSuccess(result: LoginResult?) {
                val graphRequest: GraphRequest = GraphRequest.newMeRequest(result?.accessToken) { jsonObject, response ->
                    val bundle = Bundle()
                    try {
                        bundle.putString(Constant.SOCIAL_ID, jsonObject?.getString("id"))
                        bundle.putBoolean(Constant.IS_SOCIAL_SIGNUP, true)
                        bundle.putString(Constant.NAME, jsonObject?.getString("name"))
                        bundle.putString(Constant.EMAIL, jsonObject?.getString("email"))
                        bundle.putString(Constant.SOCIAL_SOURCE, Constant.SOCIAL_SOURCE_FB)
                    } catch (e: JSONException) {
                    } finally {
                        socialSignUp(bundle)
                        progress.visibility = View.GONE
                    }
                }
                graphRequest.parameters.putString("fields", "email,name,birthday,about")
                graphRequest.executeAsync()
            }

            override fun onCancel() {
                Snackbar.make(root, getText(R.string.canceled_by_user), Snackbar.LENGTH_SHORT).show()
                progress.visibility = View.GONE
            }

        })
    }

    private fun socialSignUp(bundle: Bundle) {
        val fragment = SignupFragment()
        fragment.arguments = bundle
        FragmentFactory.replaceFragment(fragment, R.id.container, context)
    }

    fun showResult(response: JSONObject) {
        val bundle = Bundle()
        try {
            bundle.putString(Constant.SOCIAL_ID, response.get("id").toString())
            bundle.putBoolean(Constant.IS_SOCIAL_SIGNUP, true)
            bundle.putString(Constant.NAME, response.get("firstName").toString() + " " + response.get("lastName").toString())
            bundle.putString(Constant.EMAIL, response.get("emailAddress").toString())
            bundle.putString(Constant.SOCIAL_SOURCE, Constant.SOCIAL_SOURCE_LINKED_IN)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socialSignUp(bundle)
        }

    }

}