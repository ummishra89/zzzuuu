package com.mentorz.userdetails

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener
import com.mentorz.match.UserListItem

/**
 * Created by umesh on 21/08/17.
 */
interface UserDetailsResponseListener :SessionExpiredListener, NetworkErrorListener {
    fun onFollowersSuccess(userList:MutableList<UserListItem>){

    }
    fun onFollowersFail(){

    }
    fun onFollowingSuccess(userList:MutableList<UserListItem>){

    }
    fun onFollowingFail(){

    }
    fun onMentorsSuccess(userList:MutableList<UserListItem>){

    }
    fun onMentorsFail(){

    }
    fun onMenteeSuccess(userList:MutableList<UserListItem>){

    }
    fun onMenteeFail(){

    }
    fun noUserFound(){

    }
}