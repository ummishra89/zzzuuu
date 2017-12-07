package com.mentorz.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by aMAN GUPTA on 13/7/17.
 */
data class LoginRequest(
        @SerializedName("email_id")
        @Expose
        var emailId: String? = null,
        @SerializedName("password")
        @Expose
        var password: String? = null,
        @SerializedName("device_info")
        @Expose
        var deviceInfo: DeviceInfo? = null
)