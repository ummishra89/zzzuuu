package com.mentorz.token

import com.mentorz.MentorzApplication

/**
 * Created by craterzone on 27/07/17.
 */
object TokenGenerator {
    fun generateToken(): String {
        val time = System.currentTimeMillis()
        return time.toString() + "-" + MentorzApplication.instance?.prefs?.getDeviceToken() + "-" + MentorzApplication.instance?.prefs?.getUserId() + "-" + (Math.random() * 117).toString()
    }
}