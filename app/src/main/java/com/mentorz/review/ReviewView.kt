package com.mentorz.review

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 05/08/17.
 */
interface ReviewView {
    fun showProgress()
    fun hideProgress()
    fun setReviewAdapter(reviewList: MutableList<UserListItem>) {

    }

    fun onRatingResponseSuccess() {

    }

    fun noReviewFound()
    fun onSessionExpired()
    fun dataSetChanged()
    fun networkError()
}