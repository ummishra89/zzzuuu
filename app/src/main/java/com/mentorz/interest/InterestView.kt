package com.mentorz.interest

/**
 * Created by umesh on 22/07/17.
 */
interface InterestView {
    fun showProgress()
    fun hideProgress()
    fun onUpdateInterestSuccess()
    fun onUpdateInterestFail()
    fun showEmptyInterestAlert()
    fun setInterestAdapter(interest: List<InterestsItem>?)
    fun onSessionExpired()
    fun networkError()

}