package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.mentorz.match.UserProfile

data class LoginResponse(

        @field:SerializedName("email_id")
        var emailId: String? = null,

        @field:SerializedName("password")
        var password: String? = null,

        @field:SerializedName("user_id")
        var userId: Long = 0,

        @field:SerializedName("has_values")
        var hasValues: Boolean? = null,

        @field:SerializedName("has_interests")
        var hasInterests: Boolean? = null,

        @field:SerializedName("user_profile")
        var userProfile: UserProfile? = null,

        @field:SerializedName("auth_token")
        var authToken: String? = null
)