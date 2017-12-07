package com.mentorz.stories

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by umesh on 30/07/17.
 */
interface PostResponseListener : SessionExpiredListener, NetworkErrorListener {
    fun onPostSuccess(postList: List<PostListItem?>)
    fun onPostFail()
    fun noPostFound()
}