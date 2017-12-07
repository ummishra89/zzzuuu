package com.mentorz.interest

import com.google.gson.annotations.SerializedName
import com.mentorz.requester.InterestRequester
import com.mentorz.requester.UpdateInterestRequester
import com.mentorz.requester.UserInterestRequester
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.DefaultInterestListener
import com.mentorz.utils.Global

/**
 * Created by umesh on 22/07/17.
 */
class InterestPresenterImpl(interestView: InterestView) : InterestPresenter, DefaultInterestListener, DefaultChildInterestListener, UpdateInterestListener, UserInterestListener {

    override fun onNetworkFail() {
        interestView.hideProgress()
        interestView.networkError()
    }

    override fun onSessionExpired() {
        interestView.onSessionExpired()
    }

    override fun getUserInterests() {
        val userInterestRequester: UserInterestRequester = UserInterestRequester(this, this)
        userInterestRequester.execute()
    }

    override fun onUserInterestSuccess(valuesResponse: InterestResponse) {


        Global.userInterests = valuesResponse.interests
        val tempList = mutableListOf<InterestsItem>()
        tempList.addAll(valuesResponse.interests!!)
        tempList
                .filter { Global.userInterests!!.contains(it) && it.hasChildren!! }
                .forEach { removeAllChild(it) }

    }

    fun removeAllChild(interestItem: InterestsItem) {
        val list = Global.userInterests!!.filter {
            it.parentId == interestItem.interestId
        }
        for (item in list) {
            if (item.hasChildren!!) {
                removeAllChild(item)
                Global.userInterests!!.remove(item)
            } else {
                Global.userInterests!!.remove(item)
            }
        }
    }

    override fun onUserInterestFail() {
        interestView.hideProgress()

    }

    override fun onUpdateInterestSuccess(mentorzApiResponse: MentorzApiResponse) {
        interestView.hideProgress()
        if (userInterest != null) {
            Global.userInterests = userInterest
            //dbManager.updateInterest(userInterest!!)
            interestView.onUpdateInterestSuccess()
        }
    }

    override fun onUpdateInterestFail() {
        interestView.hideProgress()
        interestView.onUpdateInterestFail()
    }


    override fun updateInterest(interestList: MutableList<InterestsItem>) {

        if (interestList.isEmpty()) {
            interestView.showEmptyInterestAlert()
            return
        }
        interestList.forEach {
            it.isUpdated = null
        }
        userInterest = interestList
        interestView.showProgress()
        val updateInterestRequest = UpdateInterestRequest(interestList)
        val updateInterestRequester = UpdateInterestRequester(updateInterestRequest, this, this)
        updateInterestRequester.execute()
    }

    override fun onDefaultChildInterestSuccess(interestResponse: InterestResponse) {
        interestView.hideProgress()
        // dbManager.insertInterest(interestResponse.interests!!)
        interestView.setInterestAdapter(interestResponse.interests)
    }

    override fun onDefaultChildInterestFail() {
    }

    override fun getInterestByParentId(parentId: Int) {

        /*var childInterest = dbManager.getInterestByParentId(parentId)
        if(childInterest.isEmpty()){
            interestView.showProgress()
            val interestRequester:InterestRequester = InterestRequester(parentId,this,this)
            interestRequester.execute()

        }
        else{
            interestView.setInterestAdapter(childInterest)
        }*/
        interestView.showProgress()
        val interestRequester: InterestRequester = InterestRequester(parentId, this, this)
        interestRequester.execute()


    }

    override fun onDefaultInterestSuccess(interestResponse: InterestResponse) {
        //dbManager.insertInterest(interestResponse.interests!!)
        interestView.hideProgress()
        interestView.setInterestAdapter(interestResponse.interests)

    }

    override fun onDefaultInterestFail() {
        interestView.hideProgress()

    }

    var interestView: InterestView = interestView
    var userInterest: MutableList<InterestsItem>? = null

    //  var dbManager = DbManager.getInstance(MentorzApplication.applicationContext())

    override fun getInterests() {
        interestView.showProgress()
        val interestRequester: InterestRequester = InterestRequester(this, this)
        interestRequester.execute()
    }

    data class UpdateInterestRequest(

            @field:SerializedName("interests")
            var values: List<InterestsItem>? = null

    )


}