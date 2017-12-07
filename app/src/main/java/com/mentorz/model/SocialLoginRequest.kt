package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.mentorz.match.UserProfile

data class SocialLoginRequest(

        @field:SerializedName("social_id")
        var socialId: String? = "",

        @field:SerializedName("device_info")
        var deviceInfo: DeviceInfo? = null,

        @field:SerializedName("user_profile")
        var userProfile: UserProfile? = null,

        @field:SerializedName("social_source")
        var socialSource: String? = ""
)