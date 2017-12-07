package com.mentorz.me

import com.mentorz.model.CommentListItem
import com.mentorz.model.ProfileData
import com.mentorz.stories.PostListItem

/**
 * Created by umesh on 31/07/17.
 */
interface UserProfileView {
    fun hideProgress() {

    }

    fun showProgress() {

    }

    fun setUserProfile(userListItem: ProfileData)
    fun setPostAdapter(postList: List<PostListItem?>)

    fun noPostFound() {

    }

    fun onLikePostSuccess(postListItem: PostListItem) {

    }

    fun onLikePostFail() {

    }

    fun onSharePostSuccess(postListItem: PostListItem) {

    }

    fun onSharePostFail() {

    }

    fun onViewPostSuccess(postListItem: PostListItem) {

    }

    fun isFollowingSuccess() {

    }

    fun onFollowSuccess(model: Any?) {

    }

    fun onUnFollowSuccess(model: Any?) {

    }
    fun onFollowFail() {

    }

    fun onAbusePostSuccess() {

    }

    fun onAbusePostFail() {

    }

    fun setCommentAdapter(commentList: MutableList<CommentListItem>) {

    }

    fun onDeleteCommentSuccess() {

    }

    fun onPostCommentSuccess(commentListItem: CommentListItem) {

    }

    fun onPostCommentFail() {

    }

    fun noCommentFound() {

    }

    fun onRatingSuccess() {

    }
    fun onRatingFail(){

    }

    fun dataSetChanged() {

    }

    fun onSessionExpired()
    fun onRateUserSuccess()
    fun networkError()
}