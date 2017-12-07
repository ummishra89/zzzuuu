package com.mentorz

import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.craterzone.logginglib.manager.LoggerManager
import com.google.gson.Gson
import com.mentorz.manager.PubNubManager
import com.mentorz.pubnub.PubNubManagerService
import com.mentorz.retrofit.MentorzApi
import com.mentorz.utils.Constant
import com.mentorz.utils.CustomJsonConverterFactory
import com.mentorz.utils.Prefs
import org.jetbrains.anko.runOnUiThread

import retrofit2.Retrofit

/**
 * Created by craterzone on 05/07/17.
 */
class MentorzApplication : MultiDexApplication() {

    val gson: Gson?
    val mentorzApi: MentorzApi?
    var prefs: Prefs? = null

    init {
        instance = this
        gson = Gson()

        //init retrofit


        val retrofit = Retrofit.Builder()
                .baseUrl(Constant.STAGING_SERVER_URL)
                .addConverterFactory(CustomJsonConverterFactory.create())
                .build()
        mentorzApi = retrofit.create(MentorzApi::class.java)


    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        LoggerManager.getInstance(this).startLogging();
        prefs = Prefs(MentorzApplication.applicationContext())
      //  PubNubManager.instance.initPubNub()

    }

    companion object {

        var instance: MentorzApplication? = null
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    public fun blockUser(userId : Long?){
        this.runOnUiThread {
            val intent = Intent()
            intent.action = Constant.ACTION_BLOCK
            intent.putExtra("user_id", userId)
            applicationContext.sendBroadcast(intent)
        }
    }

}