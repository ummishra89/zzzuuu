package com.mentorz.listener

import com.mentorz.stories.PostListItem

/**
 * Created by umesh on 30/07/17.
 */
interface LikePostListener : SessionExpiredListener,NetworkErrorListener {
    fun onLikePostSuccess(postListItem: PostListItem)
    fun onLikePostFail()
}