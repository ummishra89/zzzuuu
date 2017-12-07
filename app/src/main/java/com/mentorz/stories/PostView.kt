package com.mentorz.stories

import com.mentorz.match.adapter.ViewType
import com.mentorz.model.CommentListItem

/**
 * Created by umesh on 30/07/17.
 */
interface PostView {
    fun hideProgress() {

    }

    fun showProgress() {

    }

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

    fun setPostAdapter(postList: MutableList<ViewType>) {

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

    fun dataSetChanged() {

    }

    fun onSessionExpired()
    fun networkError()
}