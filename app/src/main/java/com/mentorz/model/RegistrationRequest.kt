package com.mentorz.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by craterzone on 06/07/17.
 */
data class RegistrationRequest(
        @SerializedName("email_id")
        @Expose
        var emailId: String? = "",
        @SerializedName("password")
        @Expose
        var password: String? = "",
        @SerializedName("device_info")
        @Expose
        var deviceInfo: DeviceInfo? = null,
        @SerializedName("user_profile")
        @Expose
        var userProfile: UserProfileRegistration? = null
)