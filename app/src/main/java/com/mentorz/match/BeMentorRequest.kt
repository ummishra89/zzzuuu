package com.mentorz.match


import com.google.gson.annotations.SerializedName
import com.mentorz.expertise.ExpertiseItem

data class BeMentorRequest(

        @field:SerializedName("experties")
        var experties: List<ExpertiseItem?>? = null,

        @field:SerializedName("price")
        var price: Int? = 0,

        @field:SerializedName("organization")
        var organization: String? = null,

        @field:SerializedName("location")
        var location: String? = null,

        @field:SerializedName("designation")
        var designation: String? = null,

        @field:SerializedName("exp_years")
        var expYears: Int? = null
)