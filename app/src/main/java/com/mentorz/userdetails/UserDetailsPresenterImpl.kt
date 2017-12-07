package com.mentorz.userdetails

import android.text.TextUtils
import com.mentorz.expertise.MentorExpertiseResponseListener
import com.mentorz.listener.ChatUserResponseListener
import com.mentorz.match.UserListItem
import com.mentorz.model.ProfileImage
import com.mentorz.model.ProfileImageResponse
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.RatingListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 21/08/17.
 */
class UserDetailsPresenterImpl(userDetailsView: UserDetailsView) : UserDetailsResponseListener, ChatUserResponseListener, RatingListener, ProfileImageListener, SignedUrlListener, MentorExpertiseResponseListener {

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        userList?.forEach {
            if (it.userId!! == (model as ProfileImage).userId) {
                it.userProfile?.lresId = url

            }
        }
        view.dataSetChanged()
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

    override fun onRatingSuccess(rating: Float, mentorId: Long) {
        userList?.forEach {
            if (it.userId!! == mentorId) {
                it.rating = rating
            }
        }
        view.updateAdapter()
    }

    override fun onRatingFail() {

    }

    override fun noChatUserFound() {
        view.hideProgress()
        view.noUserFound()
    }

    override fun chatUserListSuccess(userList: MutableList<UserListItem>) {
        view.hideProgress()
        this.userList=userList
        userList.forEach {
            RatingRequester(it.userId!!, this).execute()
            ProfileImageRequester(it.userId, this).execute()
        }
        view.setChatUserAdapter(userList)
    }

    override fun chatUserFail() {
        view.hideProgress()
    }

    override fun onSessionExpired() {
        view.hideProgress()
    }

    override fun onFollowersSuccess(userList: MutableList<UserListItem>) {
        view.hideProgress()
        this.userList=userList
        userList.forEach {
            RatingRequester(it.userId!!, this).execute()
            ProfileImageRequester(it.userId, this).execute()
        }
        view.setFollowersAdapter(userList)
    }

    override fun onFollowersFail() {
        view.hideProgress()
    }

    override fun onFollowingSuccess(userList: MutableList<UserListItem>) {
        view.hideProgress()
        this.userList=userList
        userList.forEach {
            RatingRequester(it.userId!!, this).execute()
            ProfileImageRequester(it.userId, this).execute()
        }
        view.setFollowingAdapter(userList)
    }

    override fun onFollowingFail() {
        view.hideProgress()
    }

    override fun onMenteeSuccess(userList: MutableList<UserListItem>) {
        view.hideProgress()
        this.userList=userList
        userList.forEach {
            RatingRequester(it.userId!!, this).execute()
            ProfileImageRequester(it.userId, this).execute()
        }
        view.setMenteeAdapter(userList)
    }

    override fun onMenteeFail() {
        view.hideProgress()
    }


    override fun onMentorsSuccess(userList: MutableList<UserListItem>) {
        view.hideProgress()
        this.userList=userList
        userList.forEach {
            RatingRequester(it.userId!!, this).execute()
            ProfileImageRequester(it.userId, this).execute()
        }
        view.setMentorsAdapter(userList)
    }

    override fun onMentorsFail() {
        view.hideProgress()
    }

    override fun noUserFound() {
        view.hideProgress()
        view.noUserFound()
    }
    fun getFollowers(friendId:Long,pageNo:Int,isRefresh:Boolean){
          if (pageNo == 0&&!isRefresh) {
             view.showProgress()
          }
        GetFollowersRequester(friendId,pageNo,this).execute()
    }
    fun getChatUsers(friendId:Long,pageNo:Int,isRefresh:Boolean){
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetChatUserRequester(friendId,pageNo,this).execute()

    }
    fun getFollowing(friendId:Long,pageNo:Int,isRefresh:Boolean){
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetFollowingRequester(friendId,pageNo,this).execute()
    }
    fun getMentors(friendId:Long,pageNo:Int,isRefresh:Boolean){
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetMentorRequester(friendId,pageNo,this).execute()
    }
    fun getMentee(friendId:Long,pageNo:Int,isRefresh:Boolean){
        if (pageNo == 0&&!isRefresh) {
            view.showProgress()
        }
        GetMenteeRequester(friendId,pageNo,this).execute()
    }
    var view =userDetailsView
    var userList:MutableList<UserListItem>?=null
}