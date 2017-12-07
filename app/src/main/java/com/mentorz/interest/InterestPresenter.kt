package com.mentorz.interest

/**
 * Created by umesh on 22/07/17.
 */
interface InterestPresenter {
    fun getInterests()
    fun getInterestByParentId(parentId: Int)
    fun updateInterest(interestList: MutableList<InterestsItem>)
    fun getUserInterests()
}
