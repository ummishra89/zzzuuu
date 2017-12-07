package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class ProfileImageResponse(

        @field:SerializedName("mentees")
        val mentees: Int? = null,

        @field:SerializedName("followers")
        val followers: Int? = null,

        @field:SerializedName("hres_id")
        val hresId: String? = null,

        @field:SerializedName("following")
        val following: Int? = null,

        @field:SerializedName("mentors")
        val mentors: Int? = null,

        @field:SerializedName("birth_date")
        val birthDate: Int? = null,

        @field:SerializedName("lres_id")
        val lresId: String? = null,

        @field:SerializedName("requests")
        val requests: Int? = null,

        @field:SerializedName("charge_price")
        val chargePrice: Int? = null
)