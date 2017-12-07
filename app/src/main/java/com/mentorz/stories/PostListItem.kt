package com.mentorz.stories

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mentorz.match.adapter.AdapterConstants
import com.mentorz.match.adapter.ViewType

data class PostListItem(

        @field:SerializedName("share_time")
        val shareTime: Long? = null,

        @field:SerializedName("share_count")
        var shareCount: Long? = 0,

        @field:SerializedName("comment_count")
        var commentCount: Long? = 0,

        @field:SerializedName("like_count")
        var likeCount: Long? = 0,

        @field:SerializedName("post_id")
        val postId: Long? = null,

        @field:SerializedName("user_id")
        val userId: Long? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("liked")
        var liked: Boolean? = null,

        @field:SerializedName("content")
        val content: Content? = null,

        @field:SerializedName("view_count")
        var viewCount: Long? = 0,
        var isFollowing: Boolean? = false,
        var rating: Float = 0.0F,
        var profileImage: String = "",
        var contentImage: String = "",
        var contentVideo: String = ""

) : ViewType, Parcelable {
    override fun getViewType(): Int {
        return AdapterConstants.ITEMS
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as PostListItem
        if (postId != other.postId) {
            return false
        }
        return true

    }

    override fun hashCode(): Int {
        return postId!!.toInt()
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<PostListItem> = object : Parcelable.Creator<PostListItem> {
            override fun createFromParcel(source: Parcel): PostListItem = PostListItem(source)
            override fun newArray(size: Int): Array<PostListItem?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readString(),
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readParcelable<Content>(Content::class.java.classLoader),
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readFloat(),
    source.readString(),
    source.readString(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(shareTime)
        dest.writeValue(shareCount)
        dest.writeValue(commentCount)
        dest.writeValue(likeCount)
        dest.writeValue(postId)
        dest.writeValue(userId)
        dest.writeString(name)
        dest.writeValue(liked)
        dest.writeParcelable(content, 0)
        dest.writeValue(viewCount)
        dest.writeValue(isFollowing)
        dest.writeFloat(rating)
        dest.writeString(profileImage)
        dest.writeString(contentImage)
        dest.writeString(contentVideo)
    }
}