package com.mentorz.match

import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.expertise.MentorExpertiseResponseListener
import com.mentorz.expertise.Response
import com.mentorz.listener.MatchListener
import com.mentorz.model.ProfileImage
import com.mentorz.model.ProfileImageResponse
import com.mentorz.model.RequestStatusResponse
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.RatingListener
import com.mentorz.retrofit.listeners.RequestStatusListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 23/07/17.
 */
class MatchPresenterImpl(matchView: MatchView) : MatchPresenter, MatchListener, MentorExpertiseResponseListener, BeMentorListener, RatingListener, RequestStatusListener, ProfileImageListener, SignedUrlListener {
    override fun onNetworkFail() {
        matchView.hideProgress()
        matchView.networkError()
    }

    override fun onSessionExpired() {
        matchView.onSessionExpired()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        mentorList?.forEach {
            if (it?.userId!! == (model as ProfileImage).userId) {
                it.userProfile?.lresId = url

            }
        }
        matchView.dataSetChanged()

    }

    override fun signedUrlFail() {
    }

    override fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?) {

        if (!TextUtils.isEmpty(profileImageResponse.hresId)) {
            val model = ProfileImage()
            model.userId = userId
            SignedUrlRequester(FileType.FILE, this, model, profileImageResponse.hresId).execute()
        }
    }

    override fun profileImageFail() {

    }

    override fun onMentorCreated() {

    }

    override fun onNoMentors() {
        matchView.hideProgress()
        matchView.noMentorFound()
    }

    override fun onBeMentorSuccess() {
        matchView.hideProgress()
        matchView.onBeMentorSuccess()

    }

    override fun onBeMentorFail() {
        matchView.hideProgress()
        matchView.onBeMentorFail()
    }

    override fun getMentorExpertise(mentorId: Long) {
        MentorExpertiseRequester(mentorId, this, this).execute()
        // RatingRequester(mentorId, this).execute()
        RequestStatusRequester(mentorId, null, this).execute()
    }

    override fun requestStatusSuccess(response: RequestStatusResponse, model: Any?, mentorId: Long?) {
        mentorList?.forEach {
            if (it?.userId!! == mentorId) {
                if (response.value?.isMyMentor!!) {
                    it.request = UserListItem.Request.ALREADY_YOUR_MENTOR
                } else if (response.value.isAlreadySent!!) {
                    it.request = UserListItem.Request.REQUEST_SENT
                } else {
                    it.request = UserListItem.Request.SEND_REQUEST
                }
            }
        }
        matchView.updateMentorListAdapter(mentorList)

    }

    override fun requestStatusFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onRatingSuccess(rating: Float, mentorId: Long) {
        mentorList?.forEach {
            if (it?.userId!! == mentorId) {
                it.rating = rating
            }
        }
        matchView.updateMentorListAdapter(mentorList)

    }

    override fun onRatingFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onMentorExpertiseSuccess(userId: Long?, response: Response) {
        matchView.hideProgress()
        mentorList?.forEach {
            if (it?.userId!! == userId) {
                it.expertises = response.expertise as MutableList<ExpertiseItem>
            }

        }
        matchView.updateMentorListAdapter(mentorList)

    }

    override fun beMentor(beMentorRequest: BeMentorRequest) {
        matchView.showProgress()
        BeMentorRequester(beMentorRequest, this, this).execute()

    }

    override fun onMentorExpertiseFail() {
    }

    override fun onMentorsSuccess(userList: List<UserListItem?>?) {
        matchView.hideProgress()
        val list = userList!!.filter {
            it?.userId != MentorzApplication.instance?.prefs?.getUserId()
        }
        mentorList = list as MutableList<UserListItem?>
        matchView.setMentorListAdapter(list)
        list.forEach {
            getMentorExpertise(it?.userId!!)
            RatingRequester(it.userId, this).execute()
            ProfileImageRequester(it.userId, this).execute()

        }


    }

    override fun onMentorsFail() {
        matchView.hideProgress()
    }

    fun getMyMentors(mentorRequest: MyMentorRequest, pageNo: Int, isRefresh: Boolean) {
        if (!isRefresh && (pageNo == 0 && mentorList != null)) {
            matchView.setMentorListAdapter(mentorList!!)
            return
        } else if (pageNo == 0 && !isRefresh) {
            matchView.showProgress()
        }
        var offset: Int = 0
        if (mentorList != null && !mentorList!!.isEmpty()) {
            offset = mentorList!!.size;
        }
        MentorsRequester(offset, mentorRequest, this, this).execute()

        //  MentorsRequester(pageNo, mentorRequest, this, this).execute()
    }

    override fun getMyMentee() {
    }

    override fun getMentorRequests() {
    }

    override fun sendRequest() {
    }

    override fun addMentee() {
    }

    override fun getPendingRequest() {
    }

    override fun rejectPendingRequest() {
    }

    fun clearList() {
        mentorList = null
    }

    var matchView: MatchView = matchView

    companion object {
        var mentorList: MutableList<UserListItem?>? = null
    }


}