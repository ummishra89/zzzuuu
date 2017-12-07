package com.mentorz.userdetails

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 21/08/17.
 */
interface UserDetailsView {
    fun showProgress()
    fun hideProgress()
    fun setFollowersAdapter(userList:MutableList<UserListItem>){

    }
    fun setFollowingAdapter(userList:MutableList<UserListItem>){

    }
    fun setMenteeAdapter(userList:MutableList<UserListItem>){

    }
    fun setMentorsAdapter(userList:MutableList<UserListItem>){

    }
    fun noUserFound(){

    }
    fun setChatUserAdapter(userList: MutableList<UserListItem>){

    }
    fun updateAdapter()
    fun dataSetChanged()
    fun networkError()
}