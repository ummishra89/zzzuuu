package com.mentorz.listener

import com.mentorz.stories.PostListItem

/**
 * Created by umesh on 30/07/17.
 */
interface ViewPostListener : SessionExpiredListener,NetworkErrorListener {
    fun onViewPostSuccess(postListItem: PostListItem)
    fun onViewPostFail()
}