package com.mentorz.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by umesh on 25/07/17.
 */

data class InterestIds(

        @field:SerializedName ("parent_id")
        var parentId : Int ?=0,

        @field:SerializedName("interest_id")
        var interestId: Int? = null) : Parcelable {
        companion object {
                @JvmField val CREATOR: Parcelable.Creator<InterestIds> = object : Parcelable.Creator<InterestIds> {
                        override fun createFromParcel(source: Parcel): InterestIds = InterestIds(source)
                        override fun newArray(size: Int): Array<InterestIds?> = arrayOfNulls(size)
                }
        }

        constructor(source: Parcel) : this(
        source.readValue(Int::class.java.classLoader) as Int?
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeValue(interestId)
                dest.writeValue(parentId)
        }
}