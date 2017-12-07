package com.mentorz.expertise

import com.mentorz.MentorzApplication
import com.mentorz.requester.ExpertiseRequester
import com.mentorz.requester.MentorExpertiseRequester
import com.mentorz.utils.Global

/**
 * Created by umesh on 23/07/17.
 */
class ExpertisePresenterImpl(view: ExpertiseView) : ExpertisePresenter, DefaultExpertiseListener, MentorExpertiseResponseListener, UpdateExpertiseListener {
    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun onUpdateExpertiseSuccess() {

    }

    override fun onUpdateExpertiseFail() {
    }


    override fun updateMyExpertise(expertiseList: MutableList<ExpertiseItem>) {

    }

    override fun onDefaultChildExpertiseSuccess(expertiseResponse: Response) {
        view.hideProgress()
        view.setExpertiseAdapter(expertiseResponse.expertise)

    }

    override fun onDefaultChildExpertiseFail() {
        view.hideProgress()
    }

    override fun onMentorExpertiseNotFound() {
        view.hideProgress()
        Global.myexpertises?.clear()
        view.onMentorExpertiseNotFound()
    }

    override fun getDefaultExpertise() {
        view.showProgress()
        val expertiseRequester: ExpertiseRequester = ExpertiseRequester(this, this)
        expertiseRequester.execute()
    }

    override fun getMyExpertise() {
       /* if(!Global.myexpertises!!.isEmpty()){
            view.onMentorExpertiseSuccess()
            return
        }
        */
        view.showProgress()
        val expertiseRequester: MentorExpertiseRequester = MentorExpertiseRequester(MentorzApplication.instance?.prefs?.getUserId(), this, this)
        expertiseRequester.execute()
    }
    override fun onMentorExpertiseSuccess(userId: Long?, response: Response) {
        view.hideProgress()
        view.onMentorExpertiseSuccess()

    }
    override fun onMentorExpertiseFail() {
        view.hideProgress()
        view.onMentorExpertiseFail()
    }

    override fun getExpertiseByParentId(parentId: Int) {
        view.showProgress()
        val expertiseRequester: ExpertiseRequester = ExpertiseRequester(parentId, this, this)
        expertiseRequester.execute()
    }

    override fun onDefaultExpertiseSuccess(expertiseResponse: Response) {
        view.hideProgress()
        view.setExpertiseAdapter(expertiseResponse.expertise)

    }

    override fun onDefaultExpertiseFail() {
        view.hideProgress()

    }

    var view: ExpertiseView = view


}