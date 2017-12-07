package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class RatingResponse(

        @field:SerializedName("rating")
        val rating: Float? = null
)