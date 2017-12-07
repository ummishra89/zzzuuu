package com.mentorz.match

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mentorz.model.InterestIds

data class MyMentorRequest(

        @field:SerializedName("max_price")
        var maxPrice: Int? = 200,

        @field:SerializedName("min_price")
        var minPrice: Int? = 0,

        @field:SerializedName("min_rating")
        var minRating: Int? = 0,

        @field:SerializedName("max_exp")
        var maxExp: Int? = 50,

        @field:SerializedName("interests")
        var interests: List<InterestIds?>? = null,

        @field:SerializedName("max_rating")
        var maxRating: Int? = 5,

        @field:SerializedName("min_exp")
        var minExp: Int? = 1
) : Parcelable {
        companion object {
                @JvmField val CREATOR: Parcelable.Creator<MyMentorRequest> = object : Parcelable.Creator<MyMentorRequest> {
                        override fun createFromParcel(source: Parcel): MyMentorRequest = MyMentorRequest(source)
                        override fun newArray(size: Int): Array<MyMentorRequest?> = arrayOfNulls(size)
                }
        }

        constructor(source: Parcel) : this(
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?,
        ArrayList<InterestIds?>().apply { source.readList(this, InterestIds::class.java.classLoader) },
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeValue(maxPrice)
                dest.writeValue(minPrice)
                dest.writeValue(minRating)
                dest.writeValue(maxExp)
                dest.writeList(interests)
                dest.writeValue(maxRating)
                dest.writeValue(minExp)
        }
}