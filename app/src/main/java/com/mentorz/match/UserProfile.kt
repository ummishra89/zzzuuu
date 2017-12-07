package com.mentorz.match

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mentorz.R
import com.mentorz.model.Rating

data class UserProfile(

        @field:SerializedName("video_bio_lres")
        var videoBioLres: String? = "",

        @field:SerializedName("basic_info")
        var basicInfo: String? = "",

        @field:SerializedName("birth_date")
        var birthDate: String? = "",

        @field:SerializedName("lres_id")
        var lresId: String? = "",

        @field:SerializedName("requests")
        var requests: Int? = 0,

        @field:SerializedName("charge_price")
        val chargePrice: Int? = 0,

        @field:SerializedName("mentees")
        val mentees: Int? = 0,

        @field:SerializedName("followers")
        val followers: Long? = 0,

        @field:SerializedName("hres_id")
        var hresId: String? = "",

        @field:SerializedName("following")
        val following: Long? = 0,

        @field:SerializedName("mentors")
        val mentors: Int? = 0,

        @field:SerializedName("experience")
        val experience: Int? = 0,

        @field:SerializedName("name")
        var name: String? = "",

        @field:SerializedName("video_bio_hres")
        var videoBioHres: String? = "",
        @field:SerializedName("rating")
        var rating: Rating? = null,

        @field:SerializedName("chat_unread_count")
        var chatUnreadCount: Int? = null


) : Parcelable{
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<UserProfile> = object : Parcelable.Creator<UserProfile> {
            override fun createFromParcel(source: Parcel): UserProfile = UserProfile(source)
            override fun newArray(size: Int): Array<UserProfile?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readString(),
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readString(),
    source.readString(),
    source.readParcelable<Rating>(Rating::class.java.classLoader),
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(videoBioLres)
        dest.writeString(basicInfo)
        dest.writeString(birthDate)
        dest.writeString(lresId)
        dest.writeValue(requests)
        dest.writeValue(chargePrice)
        dest.writeValue(mentees)
        dest.writeValue(followers)
        dest.writeString(hresId)
        dest.writeValue(following)
        dest.writeValue(mentors)
        dest.writeValue(experience)
        dest.writeString(name)
        dest.writeString(videoBioHres)
        dest.writeParcelable(rating, 0)

    }
    fun getMenteeText(context: Context):String{
        if(mentees!! >= 2) {
            return context.getString(R.string.mentees)
        }
        else{
            return context.getString(R.string.mentee)
        }
    }
    fun getMentorText(context: Context):String{
        if(mentors!! >= 2) {
            return context.getString(R.string.mentors)
        }
        else{
            return context.getString(R.string.mentor)
        }
    }
    fun getFollowerText(context: Context):String{
        if(followers!! >= 2) {
            return context.getString(R.string.followers)
        }
        else{
            return context.getString(R.string.follower)
        }
    }
    fun getFollowingText(context: Context):String{
        if(following!! >= 2) {
            return context.getString(R.string.followings)
        }
        else{
            return context.getString(R.string.following)
        }
    }
}
