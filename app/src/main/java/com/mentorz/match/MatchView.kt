package com.mentorz.match

/**
 * Created by umesh on 23/07/17.
 */
interface MatchView {
    fun showProgress()
    fun hideProgress()
    fun onBeMentorSuccess() {

    }

    fun onMentorCreated() {

    }

    fun onBeMentorFail() {

    }

    fun setMentorListAdapter(userList: List<UserListItem?>?) {

    }

    fun noMentorFound() {

    }

    fun updateMentorListAdapter(userList: List<UserListItem?>?) {

    }

    fun dataSetChanged() {

    }

    fun onSessionExpired()
    fun networkError()
}