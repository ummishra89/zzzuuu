package com.mentorz.listener

import com.mentorz.match.UserListItem

/**
 * Created by umesh on 23/07/17.
 */
interface MatchListener : SessionExpiredListener ,NetworkErrorListener{
    fun onMentorsSuccess(list: List<UserListItem?>?)
    fun onMentorsFail()
    fun onNoMentors()
}