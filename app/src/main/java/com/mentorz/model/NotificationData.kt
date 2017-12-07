package com.mentorz.model

import com.google.gson.annotations.SerializedName

data class NotificationData(

        @field:SerializedName("userName")
        var userName: String? = null,

        @field:SerializedName("userId")
        var userId: Long? = null,

        @field:SerializedName("pushType")
        var pushType: String? = null,

        @field:SerializedName("postId")
        var postId: Long? = null,

        @field:SerializedName("commentId")
        var commentId: String? = null

)