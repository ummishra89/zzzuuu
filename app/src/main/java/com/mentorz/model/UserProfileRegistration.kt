package com.mentorz.model

import com.google.gson.annotations.SerializedName

/**
 * Created by aMAN GUPTA on 19/07/17.
 */
class UserProfileRegistration {

    @field:SerializedName("video_bio_lres")
    var videoBioLres: String? = ""

    @field:SerializedName("basic_info")
    var basicInfo: String? = ""

    @field:SerializedName("birth_date")
    var birthDate: String? = ""

    @field:SerializedName("name")
    var name: String? = ""

    @field:SerializedName("video_bio_hres")
    var videoBioHres: String? = ""
}