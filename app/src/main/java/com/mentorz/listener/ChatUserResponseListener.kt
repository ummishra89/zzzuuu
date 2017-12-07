package com.mentorz.listener

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 30/07/17.
 */
interface ChatUserResponseListener : SessionExpiredListener,NetworkErrorListener {
    fun chatUserListSuccess(userList: MutableList<UserListItem>)
    fun chatUserFail()
    fun noChatUserFound()
}