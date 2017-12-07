package com.mentorz.match

import com.google.gson.annotations.SerializedName

/**
 * Created by umesh on 24/07/17.
 */
data class MentorResponse(
        @field:SerializedName("user_list")
        val userList: MutableList<UserListItem>? = null)
