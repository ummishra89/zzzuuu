package com.mentorz.model

import com.google.gson.annotations.SerializedName
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

data class CommentListItem(

        @field:SerializedName("comment_time")
        val commentTime: Long? = null,

        @field:SerializedName("user_id")
        val userId: Long? = null,

        @field:SerializedName("hres_id")
        var hresId: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("lres_id")
        var lresId: String? = null,

        @field:SerializedName("comment")
        val comment: String? = null,

        @field:SerializedName("comment_id")
        val commentId: Long? = null
) : ViewType {
    override fun getViewType(): Int {
        return AdapterConstants.ITEMS
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as CommentListItem
        if (commentId != other.commentId) {
            return false
        }
        return true

    }

    override fun hashCode(): Int {
        return commentId!!.toInt()
    }
}