package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class Value(

        @field:SerializedName("isMyMentor")
        val isMyMentor: Boolean? = null,

        @field:SerializedName("isAlreadySent")
        val isAlreadySent: Boolean? = null
)