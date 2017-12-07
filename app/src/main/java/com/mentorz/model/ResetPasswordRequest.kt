package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(

        @field:SerializedName("password")
        var password: String? = "",

        @field:SerializedName("token")
        var token: String? = ""
)