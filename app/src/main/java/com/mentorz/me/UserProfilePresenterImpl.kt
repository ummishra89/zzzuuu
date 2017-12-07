package com.mentorz.me

import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.listener.*
import com.mentorz.match.UserListItem
import com.mentorz.model.*
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.*
import com.mentorz.stories.PostListItem
import com.mentorz.stories.PostResponseListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 31/07/17.
 */
class UserProfilePresenterImpl(view: UserProfileView) : PostResponseListener, UserProfileListener, LikePostListener, CommentListener, ViewPostListener, AbusePostListener,UnFollowListener, SharePostListener, FollowListener, RatingListener, ProfileImageListener, SignedUrlListener, RequestStatusListener {
    override fun checkUnFollowingSuccess(model: Any?, isFollowing: Boolean) {
        when (model) {
            is PostListItem? -> {
                (model as PostListItem).isFollowing = isFollowing
            }
            is ProfileData? -> {
                (model as ProfileData).isFollowing = isFollowing
            }
        }
        view.isFollowingSuccess()
    }

    override fun onUnFollowSuccess(model: Any?) {
        when (model) {
            is PostListItem -> {
                model.isFollowing = false
                view.onUnFollowSuccess(model)
            }
            is ProfileData -> {
                model.isFollowing = false
                view.onUnFollowSuccess(model)
            }
        }
    }

    override fun onUnFollowFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkUnFollowingFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestStatusSuccess(response: RequestStatusResponse, model: Any?, mentorId: Long?) {

        if (model is ProfileData) {
            if (model?.userId!! == mentorId) {
                if (response.value?.isMyMentor!!) {
                    model.request = UserListItem.Request.ALREADY_YOUR_MENTOR
                } else if (response.value.isAlreadySent!!) {
                    model.request = UserListItem.Request.REQUEST_SENT
                } else {
                    model.request = UserListItem.Request.SEND_REQUEST
                }
            }
            view.dataSetChanged()
        }

    }

    override fun requestStatusFail() {
    }

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun signedUrlSuccess(fileType: String, url: String, model: Any?) {
        when (model) {
            is ProfileData? -> {
                when (fileType) {
                    FileType.VIDEO -> {
                        model?.userProfile?.videoBioHres = url
                    }

                }
            }
        }
        view.dataSetChanged()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        when (model) {
            is ProfileImage? -> {
                postList?.forEach {
                    if (it.userId!! == (model as ProfileImage).userId) {
                        it.profileImage = url
                    }
                }
            }
            is ProfileData? -> {
                (model as ProfileData).userProfile?.lresId = url
            }
            is PostContent -> {
                postList?.forEach {
                    if (it.postId!! == model.postId) {
                        it.contentImage = url
                    }
                }
            }
            is PostVideo -> {
                postList?.forEach {
                    if (it.postId!! == model.postId) {
                        it.contentVideo = url
                    }
                }
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

    override fun userProfileSuccess(userListItem: ProfileData) {
        view.hideProgress()
        if (!TextUtils.isEmpty(userListItem.userProfile?.lresId)) {
            SignedUrlRequester(FileType.FILE, this, userListItem, userListItem.userProfile?.lresId).execute()
        }
        if (!TextUtils.isEmpty(userListItem.userProfile?.videoBioHres)) {
            SignedUrlRequester(FileType.VIDEO, this, userListItem, userListItem.userProfile?.videoBioHres).execute()
        }
        RequestStatusRequester(userListItem.userId, userListItem, this).execute()
        CheckFollowingRequester(userListItem.userId, userListItem, this, this).execute()
        view.setUserProfile(userListItem)
    }

    override fun userProfileFail() {
        view.hideProgress()
    }

    fun getUserProfile(userId: Long, pageNo: Int, isRefresh: Boolean) {
        FriendProfileRequester(userId, this).execute()
    }
    fun getPost(userId: Long, pageNo: Int, isRefresh: Boolean) {

//        if (!isRefresh && (pageNo == 0 && !postList!!.isEmpty())) {
//            view.setPostAdapter(postList!!)
//            return
//        }
        if (isRefresh && pageNo == 0) {
            postList!!.clear()
        }
        FriendProfileRequester(userId, this).execute()
        PostRequester(userId, pageNo, this, this).execute()
    }
    fun getProfileByRequeater(userId: Long, pageNo: Int, isRefresh: Boolean) {
        FriendProfileRequester(userId, this).execute()

    }


    var view: UserProfileView = view
    override fun onRatingSuccess(rating: Float, mentorId: Long) {
        postList?.forEach {
            if (it.userId!! == mentorId) {
                it.rating = rating
            }
        }
        view.onRatingSuccess()
    }

    override fun onRateUserSuccess() {
        view.hideProgress()
        view.onRateUserSuccess()

    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun onRatingFail() {
        view.onRatingFail()
    }

    override fun checkFollowingSuccess(model: Any?, isFollowing: Boolean) {
        when (model) {
            is PostListItem? -> {
                (model as PostListItem).isFollowing = isFollowing
            }
            is ProfileData? -> {
                (model as ProfileData).isFollowing = isFollowing
            }
        }
        view.isFollowingSuccess()
    }

    override fun onFollowSuccess(model: Any?) {
        when (model) {
            is PostListItem -> {
                model.isFollowing = true
                view.onFollowSuccess(model)
            }
            is ProfileData -> {
                model.isFollowing = true
                view.onFollowSuccess(model)
            }
        }

    }

    override fun onFollowFail() {
        view.onFollowFail()
    }

    override fun checkFollowingFail() {
    }

    override fun onSharePostSuccess(postListItem: PostListItem) {
        view.onSharePostSuccess(postListItem)
    }

    override fun onSharePostFail() {
        view.onSharePostFail()
    }

    override fun noCommentFound() {
        view.hideProgress()
        if (commentList == null || commentList!!.isEmpty()) {
            view.noCommentFound()
        }
    }

    override fun commentOnPostSuccess(commentListItem: CommentListItem) {
        view.hideProgress()
        view.onPostCommentSuccess(commentListItem)

    }

    override fun onLikePostSuccess(postListItem: PostListItem) {
        view.onLikePostSuccess(postListItem)
    }

    override fun onViewPostSuccess(postListItem: PostListItem) {
        view.onViewPostSuccess(postListItem)
    }

    override fun onAbusePostSuccess() {
        view.hideProgress()
        view.onAbusePostSuccess()
    }

    override fun onLikePostFail() {
        view.onLikePostFail()
    }

    override fun onViewPostFail() {
    }

    override fun commentOnPostFail() {
        view.hideProgress()
        view.onPostCommentFail()
    }

    override fun onAbusePostFail() {
        view.hideProgress()
        view.onAbusePostFail()
    }


    override fun commentResponseSuccess(commentResponse: CommentResponse) {
        view.hideProgress()
        this.commentList = commentResponse.commentList as MutableList<CommentListItem>
        view.setCommentAdapter(this.commentList!!)
    }

    override fun commentResponseFail() {
        view.hideProgress()
    }

    override fun deleteCommentSuccess() {
        view.hideProgress()
        view.onDeleteCommentSuccess()
    }

    override fun deleteCommentFail() {
        view.hideProgress()
    }

    override fun noPostFound() {
        view.hideProgress()
        view.noPostFound()
    }

    override fun onPostSuccess(posts: List<PostListItem?>) {
        view.hideProgress()
        val tempPost = posts as MutableList<PostListItem>
        tempPost.forEach {
            if (it.userId != MentorzApplication.instance?.prefs?.getUserId()) {
                checkFollowing(it)
            }
            RatingRequester(it.userId, this).execute()
            ProfileImageRequester(it.userId, this).execute()
            if (it.content != null && it.content.mediaType != null) {
                if (it.content.mediaType.equals("VIDEO")) {
                    val model = PostVideo()
                    model.postId = it.postId
                    SignedUrlRequester(FileType.FILE, this, model, it.content.hresId).execute()
                }
                val model = PostContent()
                model.postId = it.postId
                SignedUrlRequester(FileType.FILE, this, model, it.content.lresId).execute()
            }
        }
        postList!!.addAll(tempPost)
        view.setPostAdapter(postList!!)
    }


    fun abusePost(postListItem: PostListItem, abuseType: String) {
        view.showProgress()
        AbusePostRequester(postListItem, abuseType, this, this).execute()
    }

    override fun onPostFail() {
        view.hideProgress()
    }

    fun likePost(postListItem: PostListItem) {
        LikeRequester(postListItem, this, this).execute()
    }

    fun sharePost(postListItem: PostListItem) {
        SharePostRequester(postListItem, this, this).execute()
    }

    fun viewPost(postListItem: PostListItem) {
        ViewPostRequester(postListItem, this, this).execute()
    }

    fun followUser(model: Any, userId: Long) {
        FollowRequester(model, userId, this, this).execute()
    }
    fun unFollowUser(model: Any, userId: Long) {
        UnFollowRequester(model, userId, this, this).execute()
    }

    fun checkFollowing(postListItem: PostListItem) {
        CheckFollowingRequester(postListItem.userId, postListItem, this, this).execute()
    }

    fun rateUser(ratingValue: Float, mentorId: Long, comment: String) {
        val rating: Rating = Rating()
        rating.rating = ratingValue
        rating.review = comment
        view.showProgress()
        RateUserRequester(mentorId, rating, this).execute()
    }

    var postList: MutableList<PostListItem>? = mutableListOf()
    var commentList: MutableList<CommentListItem>? = mutableListOf()


}