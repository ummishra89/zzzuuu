package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class MentorBioResponse(

        @field:SerializedName("price")
        val price: Int? = null,

        @field:SerializedName("organization")
        val organization: String? = null,

        @field:SerializedName("location")
        val location: String? = null,

        @field:SerializedName("designation")
        val designation: String? = null,

        @field:SerializedName("exp_years")
        val expYears: Int? = null
)