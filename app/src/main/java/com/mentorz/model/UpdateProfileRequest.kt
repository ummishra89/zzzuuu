package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(

        @field:SerializedName("video_bio_lres")
        var videoBioLres: String? = "",

        @field:SerializedName("basic_info")
        var basicInfo: String? = "",

        @field:SerializedName("hres_id")
        var hresId: String? = "",

        @field:SerializedName("lres_id")
        var lresId: String? = "",

        @field:SerializedName("name")
        var name: String? = "",

        @field:SerializedName("video_bio_hres")
        var videoBioHres: String? = ""
)