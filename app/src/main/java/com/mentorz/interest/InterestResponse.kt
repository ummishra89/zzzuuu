package com.mentorz.interest

import com.google.gson.annotations.SerializedName

data class InterestResponse(

        @field:SerializedName("interests")
        val interests: MutableList<InterestsItem>? = null
)