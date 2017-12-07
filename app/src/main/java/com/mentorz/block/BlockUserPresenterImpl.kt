package com.mentorz.block

import android.text.TextUtils
import com.mentorz.listener.BlockUserListener
import com.mentorz.match.UserListItem
import com.mentorz.model.ProfileImage
import com.mentorz.requester.BlockUnBlockRequester
import com.mentorz.requester.GetBlockedUserRequester
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 12/08/17.
 */
class BlockUserPresenterImpl(view:BlockUserView) : BlockUserListener, SignedUrlListener {

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        when (model) {
            is ProfileImage? -> {
                blockedUserList?.forEach {
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

    override fun onSessionExpired() {
        view.hideProgress()

    }

    override fun onGetBlockedUserSuccess(blockedUserList: MutableList<UserListItem>) {
        view.hideProgress()
        this.blockedUserList =blockedUserList
        this.blockedUserList !!.forEach {
            if (!TextUtils.isEmpty(it.userProfile?.lresId)) {
                val model = ProfileImage()
                model.userId = it.userId
                SignedUrlRequester(FileType.FILE, this, model, it.userProfile?.lresId).execute()
            }
        }
        view.setBlockedUserAdapter(blockedUserList)
    }

    override fun onGetBlockedUserFail() {
        view.hideProgress()
    }

    override fun blockedUserNotFound() {
        view.hideProgress()
        view.blockedUserNotFound()
    }

    override fun onUnBlockUserSuccess() {
        view.hideProgress()
        view.onUnBlockUserSuccess()
    }

    override fun onUnBlockUserFail() {
        view.hideProgress()
        view.onUnBlockUserFail()

    }
    fun getBlockedUsers(pageNo:Int,isRefresh:Boolean){
         if (pageNo == 0&&!isRefresh) {
            view.showProgress()
         }
        GetBlockedUserRequester(pageNo,this,this).execute()
    }

    override fun onBlockUserSuccess(mentorId :Long?) {
        view.hideProgress()
        view.onBlockUserSuccess(mentorId)
    }

    override fun onBlockUserFail() {
        view.hideProgress()
        view.onBlockUserFail()

    }
    fun unBlockUser(userId:Long){
        view.showProgress()
        BlockUnBlockRequester(false,userId,this,this).execute()
    }
    fun blockUser(userId:Long){
        view.showProgress()
        BlockUnBlockRequester(true,userId,this,this).execute()
    }
    var view=view
    var blockedUserList:MutableList<UserListItem>?=null

}