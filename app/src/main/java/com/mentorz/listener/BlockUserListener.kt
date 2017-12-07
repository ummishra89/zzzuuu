package com.mentorz.listener

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 30/07/17.
 */
interface BlockUserListener : SessionExpiredListener ,NetworkErrorListener{
    fun onGetBlockedUserSuccess(blockedUserList:MutableList<UserListItem>)
    {

    }
    fun onGetBlockedUserFail(){

    }
    fun onBlockUserSuccess(mentorId :Long?){

    }
    fun onBlockUserFail()
    {

    }
    fun onUnBlockUserSuccess(){

    }
    fun onUnBlockUserFail(){

    }
    fun blockedUserNotFound(){

    }
}