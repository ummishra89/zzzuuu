package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class DeviceInfo(

        @field:SerializedName("device_token")
        var deviceToken: String? = null,

        @field:SerializedName("device_type")
        val deviceType: String? = "ANDROID"
)