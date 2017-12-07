package com.mentorz.feedback

/**
 * Created by umesh on 09/08/17.
 */
interface FeedbackView {
    fun onFeedbackSuccess()
    fun onFeedbackFail()
    fun showProgress()
    fun hideProgress()
    fun onEmptyDescription()
    fun onRatingNotSelected()
    fun onSessionExpired()
    fun networkError()
}