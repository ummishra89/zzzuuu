package com.mentorz.expertise

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by umesh on 24/07/17.
 */
interface MentorExpertiseResponseListener : SessionExpiredListener, NetworkErrorListener {
    fun onMentorExpertiseSuccess(userId: Long?, response: Response){

    }
    fun onMentorExpertiseFail(){

    }
    fun onMentorExpertiseNotFound() {

    }

    fun onNoMentors() {

    }

}