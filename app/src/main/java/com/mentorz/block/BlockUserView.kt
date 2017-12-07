package com.mentorz.block

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 12/08/17.
 */
interface BlockUserView {
    fun onBlockUserSuccess(mentorId :Long?){

    }
    fun onBlockUserFail(){

    }
    fun onUnBlockUserSuccess(){

    }
    fun onUnBlockUserFail(){

    }
    fun setBlockedUserAdapter(blockedUserList: MutableList<UserListItem>){

    }
    fun getBlockedUserFail(){

    }
    fun showProgress(){

    }
    fun hideProgress(){

    }
    fun onProfileImageSuccess(){

    }
    fun blockedUserNotFound(){

    }
    fun networkError()

}