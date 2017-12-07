package com.mentorz.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Rating(
        @field:SerializedName("rating")
        var rating: Float? = 0.0f,
        @field:SerializedName("review")
        var review: String? = null,
        @field:SerializedName("time")
        val time: Long? = null
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Rating> = object : Parcelable.Creator<Rating> {
            override fun createFromParcel(source: Parcel): Rating = Rating(source)
            override fun newArray(size: Int): Array<Rating?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readValue(Float::class.java.classLoader) as Float?,
    source.readString(),
    source.readValue(Long::class.java.classLoader) as Long?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(rating)
        dest.writeString(review)
        dest.writeValue(time)
    }
}
