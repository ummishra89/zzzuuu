package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class SendMentorRequest(

        @field:SerializedName("info")
        val info: String? = null
)