package com.mentorz.match

import com.google.gson.annotations.SerializedName
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

data class UserListItem(

        @field:SerializedName("user_id")
        val userId: Long? = null,

        @field:SerializedName("user_profile")
        var userProfile: UserProfile? = null,
        @field:SerializedName("experties")
        var expertises: MutableList<ExpertiseItem>? = null,

        var request: Int = Request.SEND_REQUEST,

        var rating: Float = 0.0F,
        var isFollowing: Boolean? = false




) :ViewType {
    var viewType: Int? = AdapterConstants.ITEMS
        set(value) {
            field = value
        }

    override fun getViewType(): Int {
        return  viewType!!
    }


override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as UserListItem
        if (userId != other.userId) {
            return false
        }
        return true

    }

fun getExpertiseInString(): String {
        var expertise = ""
        if (expertises == null) {
            return expertise
        }
        loop@ for (i in expertises!!.indices) {
            expertise += expertises!![i].expertise + ", "
        }
        return expertise.removeSuffix(", ")
    }

object Request {
        val REQUEST_SENT = 2
        val SEND_REQUEST = 1
        val ALREADY_YOUR_MENTOR = 3
    }


}