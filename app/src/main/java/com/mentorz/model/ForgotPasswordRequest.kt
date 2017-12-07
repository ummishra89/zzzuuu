package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(

        @field:SerializedName("email_id")
        val emailId: String? = ""
)