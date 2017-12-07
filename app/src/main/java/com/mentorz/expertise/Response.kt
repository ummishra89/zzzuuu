package com.mentorz.expertise


import com.google.gson.annotations.SerializedName

data class Response(

        @field:SerializedName("expertise")
        val expertise: MutableList<ExpertiseItem>? = null
)