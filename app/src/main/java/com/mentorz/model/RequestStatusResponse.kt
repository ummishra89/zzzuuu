package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class RequestStatusResponse(

        @field:SerializedName("value")
        val value: Value? = null
)