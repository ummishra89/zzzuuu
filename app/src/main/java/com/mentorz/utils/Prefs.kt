package com.mentorz.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by craterzone on 05/07/17.
 */
class Prefs(context: Context) {

    private val context_: Context?
    private var sharedPref: SharedPreferences?

    init {
        context_ = context
        sharedPref = context_.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPref!!.getString(key, defaultValue)
    }

    fun putString(key: String, value: String?): Unit {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref!!.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long?): Unit {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putLong(key, value!!)
        editor.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPref!!.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean): Unit {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPref!!.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int?): Unit {
        val editor: SharedPreferences.Editor = sharedPref!!.edit()
        editor.putInt(key, value!!)
        editor.apply()
    }

    fun getUserName(): String {
        return getString(Key.USER_NAME, "")
    }

    fun getUserId(): Long {
        return getLong(Key.USER_ID, 0)
    }

    fun getAuthToken(): String {
        return getString(Key.AUTH_TOKEN, "")
    }

    fun getDeviceToken(): String {
        return getString(Key.DEVICE_TOKEN, "")
    }

    fun isSocialLogin(): Boolean {
        return getBoolean(Key.IS_SOCIAL_LOGIN, false)
    }

    fun isLogin(): Boolean {
        return getBoolean(Key.IS_LOGIN, false)
    }

    fun clear() {
        val deviceToken = getString(Key.DEVICE_TOKEN, "")
        sharedPref?.edit()?.clear()?.apply()
        putString(Key.DEVICE_TOKEN, deviceToken)
    }

    fun setValue() {
        putBoolean(Key.HAS_VALUES, true)
    }

    fun setIMEI(imei: String) {
        putString(Key.IMEI, imei)
    }

    fun getIMEI(): String {
        return getString(Key.IMEI, "")
    }
    fun setUdId(udId: String) {
        putString(Key.UDID, udId)
    }

    fun getUdId(): String {
        return getString(Key.UDID, "")
    }

    fun setInterest() {
        putBoolean(Key.HAS_INTERESTS, true)
    }

    fun hasValuesInterests(): Boolean {
        return getBoolean(Key.HAS_VALUES, false) && getBoolean(Key.HAS_INTERESTS, false)
    }

    fun getBasicInfo(): String {
        return getString(Key.BASIC_INFO, "")
    }

    fun getOccupation(): String {
        return getString(Key.OCCUPATION, "")
    }

    fun getProfilePictureLres(): String {
        return getString(Key.PROFILE_PICTURE_LRES, "")
    }


    fun getProfileVideoHres(): String {
        return getString(Key.PROFILE_VIDEO_HRES, "")
    }

    fun getProfileVideoLres(): String {
        return getString(Key.PROFILE_VIDEO_LRES, "")
    }

    object Key {
        val USER_NAME = "user_name"
        val USER_ID = "user_id"
        val AUTH_TOKEN = "auth_token"
        val EMAIL = "email"
        val BIRTH_DATE = "birth_date"
        val IS_LOGIN = "is_login"
        val DEVICE_TOKEN = "device_token"
        val HAS_INTERESTS = "has_interests"
        val HAS_VALUES = "has_values"
        val BASIC_INFO = "basic_info"
        val PROFILE_PICTURE_HRES = "profile_picture_hres"
        val PROFILE_PICTURE_LRES = "profile_picture_lres"
        val PROFILE_VIDEO_HRES = "profile_video_hres"
        val PROFILE_VIDEO_LRES = "profile_video_lres"
        val OCCUPATION = "occupation"
        val ORGANIZATION = "organization"
        val LOCATION = "location"
        val EXPERIENCE = "experience"
        val IS_SOCIAL_LOGIN = "is_social_login"
        val IMEI = "imei"
        val UDID = "udid"
        val IS_PUSH_TOKEN_UPDATED = "is_push_token_update"
    }
}