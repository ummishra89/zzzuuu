package com.mentorz.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.craterzone.logginglib.manager.LoggerManager
import com.mentorz.MentorzApplication
import com.mentorz.R
import com.mentorz.activities.authentication.AuthenticationActivity
import com.mentorz.activities.getstarted.GetStartedActivity
import com.mentorz.model.DeviceInfo
import com.mentorz.pubnub.PubNubManagerService
import com.mentorz.requester.UpdatePushTokenRequester
import com.mentorz.requester.UserProfileRequester
import com.mentorz.retrofit.listeners.UpdatePushTokenListener
import com.mentorz.utils.Prefs
import io.vrinda.kotlinpermissions.PermissionCallBack
import org.jetbrains.anko.doAsync
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity(), UpdatePushTokenListener {
    override fun onNetworkFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        printKeyHash()
      //  startService(Intent(applicationContext, PubNubManagerService::class.java))


       // checkForWritePermission()
      /*  val mydir = getDir("Mentorz", Context.MODE_PRIVATE)
        if (!mydir.exists()) {
            mydir.mkdirs()
        }*/


        val tManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        requestPermissions(Manifest.permission.READ_PHONE_STATE, object : PermissionCallBack {

            @SuppressLint("MissingPermission")
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Call permissions", "Granted")
                MentorzApplication.instance?.prefs?.setIMEI(tManager.deviceId)
                MentorzApplication.instance?.prefs?.setUdId(getId());
                doInBackground()
            }

            fun getId(): String {
                return Settings.System.getString(this@SplashActivity.getContentResolver(), Settings.Secure.ANDROID_ID)
            }
            override fun permissionDenied() {
                super.permissionDenied()
                finish()
                Log.v("Call permissions", "Denied")
            }
        })

        if(MentorzApplication.instance?.prefs?.isLogin()!! && !MentorzApplication.instance?.prefs?.getBoolean(Prefs.Key.IS_PUSH_TOKEN_UPDATED,false)!!){
            val deviceInfo = DeviceInfo()
            deviceInfo.deviceToken = MentorzApplication.instance?.prefs?.getDeviceToken()
            UpdatePushTokenRequester(deviceInfo,this).execute()
        }


    }

    fun doInBackground() {
        doAsync {
            someDelay()
            runOnUiThread {
                if (!MentorzApplication.instance?.prefs?.isLogin()!!) {
                    val intent = Intent(applicationContext, AuthenticationActivity::class.java)
                    startActivity(intent)
                } else {
                    if (MentorzApplication.instance?.prefs?.hasValuesInterests()!!) {
                        UserProfileRequester().execute()
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(intent)

                    } else {
                        val intent = Intent(applicationContext, GetStartedActivity::class.java)
                        startActivity(intent)

                    }
                }
                finish()

            }
        }
    }

    private fun someDelay() {
        Thread.sleep(2000)
    }

    private fun printKeyHash() {
        // Add code to print out the key hash
        try {
            val info = packageManager.getPackageInfo(packageName, GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: NameNotFoundException) {
            Log.e("KeyHash:", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", e.toString())
        }

    }

    fun checkForWritePermission(){
        val permissions: Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permissions[0], object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                Log.v("Storage permissions", "Granted")
                LoggerManager.getInstance(this@SplashActivity).startLogging();

            }

            override fun permissionDenied() {
                super.permissionDenied()
                Log.v("Storage permissions", "Denied")
            }
        })

    }
}
