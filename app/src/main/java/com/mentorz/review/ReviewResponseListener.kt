package com.mentorz.review

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.match.UserListItem

/**
 * Created by umesh on 05/08/17.
 */
interface ReviewResponseListener : SessionExpiredListener, NetworkErrorListener {
    fun onReviewResponseSuccess(reviewList: MutableList<UserListItem>)
    fun onReviewResponseFail()
    fun reviewNoFound()
}