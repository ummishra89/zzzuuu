package com.mentorz.stories

import com.google.gson.annotations.SerializedName

data class PostResponse(

        @field:SerializedName("post_list")
        val postList: List<PostListItem?>? = null
)