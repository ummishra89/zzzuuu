package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class CommentResponse(

        @field:SerializedName("comment_list")
        val commentList: List<CommentListItem?>? = null
)