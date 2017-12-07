package com.mentorz.feedback

import com.mentorz.requester.FeedbackRequester

/**
 * Created by umesh on 09/08/17.
 */
class FeedbackPresenterImpl(feedbackView: FeedbackView) : FeedbackResponseListener {
    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    var view: FeedbackView = feedbackView
    override fun onFeedbackSuccess() {
        view.hideProgress()
        view.onFeedbackSuccess()
    }

    override fun onFeedbackFail() {
        view.hideProgress()
        view.onFeedbackFail()

    }

    fun sendFeedback(scaleCount: Int, description: String) {
        if (isScaleCountNotSelected(scaleCount)) {
            return
        } else if (isEmpty(description)) {
            return
        }
        view.showProgress()
        FeedbackRequester(description, scaleCount, this, this).execute()
    }

    fun isEmpty(description: String): Boolean {
        if (description.isEmpty()) {
            view.onEmptyDescription()
            return true
        }
        return false
    }

    fun isScaleCountNotSelected(scaleCount: Int): Boolean {
        if (scaleCount == 0) {
            view.onRatingNotSelected()
            return true
        }
        return false
    }
}