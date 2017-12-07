package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.mentorz.match.UserProfile
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

data class GetProfileResponse(

        @field:SerializedName("experties")
        val experties: List<ExpertiesItem?>? = null,

        @field:SerializedName("user_id")
        val userId: Long? = null,

        @field:SerializedName("user_profile")
        val userProfile: UserProfile? = null
) : ViewType {
    override fun getViewType(): Int {
        return AdapterConstants.HEADER
    }
}