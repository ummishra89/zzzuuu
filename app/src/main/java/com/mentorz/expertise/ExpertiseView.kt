package com.mentorz.expertise

/**
 * Created by umesh on 23/07/17.
 */
interface ExpertiseView {

    fun showProgress()
    fun hideProgress()
    fun onMentorExpertiseNotFound() {

    }

    fun setExpertiseAdapter(expertise: List<ExpertiseItem>?) {

    }

    fun onUpdateExpertiseSuccess() {

    }

    fun onUpdateExpertiseFail() {

    }

    fun onMentorExpertiseSuccess() {

    }

    fun onMentorExpertiseFail() {

    }

    fun onSessionExpired()
    fun networkError()

}
