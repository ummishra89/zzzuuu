package com.mentorz.messages

import com.google.gson.annotations.SerializedName


data class PendingRequestResponse(

	@field:SerializedName("request_list")
	val requestList: List<RequestListItem?>? = null
)