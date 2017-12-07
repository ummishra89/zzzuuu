package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.plumillonforge.android.chipview.Chip

data class ValuesItem(

        @field:SerializedName("value_id")
        var valueId: Int? = null,

        @field:SerializedName("value")
        var value: String? = null,

        var isMyValue: Boolean? = false
) : Chip {
    override fun getText(): String? {
        return value
    }
}