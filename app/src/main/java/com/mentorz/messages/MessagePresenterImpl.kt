package com.mentorz.messages

import android.text.TextUtils
import com.mentorz.listener.AddMenteeResponseListener
import com.mentorz.listener.CancelPendingRequestResponseListener
import com.mentorz.listener.GetPendingRequestResponseListener
import com.mentorz.listener.RejectPendingRequestResponseListener
import com.mentorz.model.ProfileImage
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 12/08/17.
 */
class MessagePresenterImpl(view:MessageView) : GetPendingRequestResponseListener, CancelPendingRequestResponseListener, SignedUrlListener, RejectPendingRequestResponseListener, AddMenteeResponseListener {

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun onRejectRequestSuccess() {
        view.hideProgress()
        view.onRejectRequestSuccess()
    }

    override fun onRejectRequestFail() {
        view.hideProgress()
        view.onRejectRequestFail()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        when (model) {
            is ProfileImage? -> {
                pendingRequestList?.forEach {
                    if (it.userId!! == (model as ProfileImage).userId) {
                        it.userProfile?.lresId = url
                    }
                }

            }
        }
        view.onProfileImageSuccess()
    }

    override fun signedUrlFail() {
    }

    override fun onCancelRequestSuccess() {
        view.hideProgress()
        view.onCancelRequestSuccess()
    }

    override fun onCancelRequestFail() {
        view.hideProgress()
        view.onCancelRequestFail()
    }

    override fun onGetPendingRequestSuccess(requestList: MutableList<RequestListItem>) {
        view.hideProgress()
        pendingRequestList = requestList
        pendingRequestList!!.forEach {
            if (!TextUtils.isEmpty(it.userProfile?.lresId)) {
                val model = ProfileImage()
                model.userId = it.userId
                SignedUrlRequester(FileType.FILE, this, model, it.userProfile?.lresId).execute()
            }
        }
        view.setPendingRequestAdapter(pendingRequestList!!)
    }

    override fun pendingRequestNotFound() {
        view.hideProgress()
        view.pendingRequestNotFound()
    }

    override fun onSessionExpired() {
        view.hideProgress()
        view.onSessionExpired()
    }

    override fun onGetPendingRequestFail() {
        view.hideProgress()
        view.onPendingRequestFail()
    }

    fun getPendingRequest(pageNo:Int,isRefresh:Boolean){
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetPendingRequestRequester(pageNo,this,this).execute()
    }
    fun cancelRequest(requestListItem: RequestListItem){
        view.showProgress()
        CancelPendingRequestRequester(requestListItem.userId!!,this).execute()
    }
    fun rejectRequest(requestListItem: RequestListItem){
        view.showProgress()
        RejectPendingRequestRequester(requestListItem.userId!!,this).execute()
    }
    fun acceptRequest(requestListItem: RequestListItem){
        view.showProgress()
        AddMenteeRequester(requestListItem.userId!!,this).execute()
    }

    override fun onAcceptRequestSuccess() {
        view.hideProgress()
        view.onAcceptRequestSuccess()
    }

    override fun onAcceptRequestFail() {
        view.hideProgress()
        view.onAcceptRequestFail()

    }
    var view:MessageView = view
    var pendingRequestList:MutableList<RequestListItem>?=null
}