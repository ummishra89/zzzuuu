package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.mentorz.expertise.ExpertiseItem
import com.mentorz.match.UserProfile
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

/**
 * Created by umesh on 31/07/17.
 */
data class ProfileData(

        @field:SerializedName("user_id")
        var userId: Long? = null,

        @field:SerializedName("user_profile")
        var userProfile: UserProfile? = null,

        @field:SerializedName("experties")
        var expertises: MutableList<ExpertiseItem>? = null,
        var request: Int = 0,
        var isFollowing: Boolean? = null

) : ViewType {
    override fun getViewType(): Int {
        return AdapterConstants.HEADER
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as ProfileData
        if (userId != other.userId) {
            return false
        }
        return true

    }


}