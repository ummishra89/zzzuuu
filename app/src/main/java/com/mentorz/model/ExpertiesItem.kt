package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class ExpertiesItem(

        @field:SerializedName("has_children")
        val hasChildren: Boolean? = null,

        @field:SerializedName("parent_id")
        val parentId: Int? = null,

        @field:SerializedName("is_my_expertise")
        val isMyExpertise: Boolean? = null,

        @field:SerializedName("expertise")
        val expertise: String? = null,

        @field:SerializedName("expertise_id")
        val expertiseId: Int? = null
)