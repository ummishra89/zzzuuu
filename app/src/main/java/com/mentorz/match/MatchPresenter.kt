package com.mentorz.match

/**
 * Created by umesh on 23/07/17.
 */
interface MatchPresenter {

    fun getMyMentee() {

    }

    fun getMentorRequests() {

    }

    fun sendRequest() {

    }

    fun addMentee() {

    }

    fun getPendingRequest() {

    }

    fun rejectPendingRequest() {

    }

    fun getMentorExpertise(mentorId: Long) {

    }

    fun beMentor(beMentorRequest: BeMentorRequest) {

    }
}