package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class ValuesResponse(

        @field:SerializedName("values")
        var values: MutableList<ValuesItem>? = null
)