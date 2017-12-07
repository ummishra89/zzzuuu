package com.mentorz.stories

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.mentorz.MentorzApplication
import com.mentorz.listener.*
import com.mentorz.match.adapter.ViewType
import com.mentorz.model.*
import com.mentorz.receiver.BlockNotificationReceiver
import com.mentorz.requester.*
import com.mentorz.retrofit.listeners.ProfileImageListener
import com.mentorz.retrofit.listeners.RatingListener
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType

/**
 * Created by umesh on 30/07/17.
 */
class PostPresenterImpl(view: PostView) : PostResponseListener, LikePostListener, CommentListener, ViewPostListener, AbusePostListener, SharePostListener, FollowListener, RatingListener, ProfileImageListener, SignedUrlListener, UnFollowListener {
    override fun checkUnFollowingSuccess(model: Any?, isFollowing: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    override fun onNetworkFail() {
        view.hideProgress()
        view.networkError()
    }

    override fun onSessionExpired() {
        view.onSessionExpired()
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        when (model) {
            is ProfileImage? -> {
                postList?.forEach {

                    if (it is PostListItem && (it.userId!! == (model as ProfileImage).userId)) {
                        it.profileImage = url
                    }
                }
                commentList?.forEach {
                    if (it.userId!! == (model as ProfileImage).userId) {
                        it.lresId = url
                    }
                }
            }
            is PostContent -> {
                postList?.forEach {
                    if (it is PostListItem && (it.postId!! == model.postId)) {
                        it.contentImage = url
                    }
                }
            }
            is PostVideo -> {
                postList?.forEach {
                    if (it is PostListItem && (it.postId!! == model.postId)) {
                        it.contentVideo = url
                    }
                }
            }

        }
        view.dataSetChanged()
    }

    override fun signedUrlFail() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun profileImageSuccess(profileImageResponse: ProfileImageResponse, userId: Long?) {
        if (!TextUtils.isEmpty(profileImageResponse.lresId)) {
            val model = ProfileImage()
            model.userId = userId
            SignedUrlRequester(FileType.FILE, this, model, profileImageResponse.lresId).execute()
        }
    }

    override fun profileImageFail() {

    }

    override fun onRatingSuccess(rating: Float, mentorId: Long) {
        postList?.forEach {
            if ((it is PostListItem) && it.userId!! == mentorId) {
                it.rating = rating
            }
        }
        view.onRatingSuccess()
    }

    override fun onRatingFail() {
    }

    override fun checkFollowingSuccess(model: Any?, isFollowing: Boolean) {
        (model as PostListItem).isFollowing = isFollowing
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
        view.noCommentFound()
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
        commentList!!.forEach {
            ProfileImageRequester(it.userId, this).execute()
        }
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
        postList?.addAll(tempPost)
        view.setPostAdapter(postList!!)

    }

    fun postComment(userId: Long, comment: String, postId: Long) {
        view.showProgress()
        CommentRequester(userId, comment, postId, this, this).execute()
    }

    fun deleteComment(postId: Long, commentListItem: CommentListItem) {
        view.showProgress()
        DeleteCommentRequester(postId, commentListItem, this, this).execute()
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

    fun followUser(postListItem: PostListItem, userId: Long) {
        FollowRequester(postListItem, userId, this, this).execute()
    }

    fun unFollowUser(postListItem: PostListItem, userId: Long) {
        UnFollowRequester(postListItem, userId, this, this).execute()
    }

    fun checkFollowing(postListItem: PostListItem) {
        CheckFollowingRequester(postListItem.userId, postListItem, this, this).execute()
    }

    fun getPost(pageNo: Int, isRefresh: Boolean) {
        if (!isRefresh && (pageNo == 0 && !postList!!.isEmpty())) {
            view.setPostAdapter(postList!!)
            return
        } else if (pageNo == 0 && !isRefresh) {
            view.showProgress()
        }
        if (pageNo == 0) {
            postList?.clear()
        }
        BoardRequester(pageNo, this, this).execute()
    }

    fun getPostByInterest(interestList: MutableList<Int>, pageNo: Int, isRefresh: Boolean) {
        if (!isRefresh && (pageNo == 0 && !postList!!.isEmpty())) {
            view.setPostAdapter(postList!!)
            return
        } else if (pageNo == 0 && !isRefresh) {
            view.showProgress()
        }
        if (pageNo == 0) {
            postList?.clear()
        }

        GetPostByInterestRequester(pageNo, interestList, this, this).execute()
    }

    fun getComment(userId: Long, postId: Long, pageNo: Int) {
        if (commentList == null || commentList!!.isEmpty()) {
            view.showProgress()
        }
        GetCommentRequester(userId, pageNo, postId, this, this).execute()

    }

    var view: PostView = view
    var postList: MutableList<ViewType>? = mutableListOf()
    var commentList: MutableList<CommentListItem>? = mutableListOf()


}