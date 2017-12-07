package com.mentorz.messages

import com.google.gson.annotations.SerializedName
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.UserProfile
import com.mentorz.match.adapter.ViewType


data class RequestListItem(

	@field:SerializedName("user_id")
	val userId: Long? = null,

	@field:SerializedName("request_time_stamp")
	val requestTimeStamp: Long? = null,

	@field:SerializedName("user_profile")
	val userProfile: UserProfile? = null
):ViewType{
	override fun getViewType(): Int {
		return AdapterConstants.ITEMS
	}
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other?.javaClass != javaClass) return false
		other as RequestListItem
		if (userId != other.userId) {
			return false
		}
		return true

	}

	override fun hashCode(): Int {
		return userId!!.toInt()
	}
	var isMentorRequest: Boolean? = false
}