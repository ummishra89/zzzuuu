package com.mentorz.listener

import com.mentorz.stories.PostListItem

/**
 * Created by umesh on 30/07/17.
 */
interface SharePostListener : SessionExpiredListener,NetworkErrorListener {
    fun onSharePostSuccess(postListItem: PostListItem)
    fun onSharePostFail()
}