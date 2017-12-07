package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.stories.PostListItem

/**
 * Created by craterzone on 05/09/17.
 */
interface PostByPostIdListener : SessionExpiredListener, NetworkErrorListener {
    fun onPostByPostIdSuccess(item:PostListItem)
    fun onPostByPostIdFail()
}