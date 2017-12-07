package com.mentorz.expertise

/**
 * Created by umesh on 23/07/17.
 */
interface ExpertisePresenter {

    fun getDefaultExpertise()
    fun getExpertiseByParentId(parentId: Int)
    fun getMyExpertise()
    fun updateMyExpertise(expertiseList: MutableList<ExpertiseItem>)
}