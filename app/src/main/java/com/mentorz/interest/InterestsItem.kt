package com.mentorz.interest

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.plumillonforge.android.chipview.Chip

data class InterestsItem(
        @field:SerializedName("has_children")
        var hasChildren: Boolean? = false,

        @field:SerializedName("interest")
        var interest: String? = null,

        @field:SerializedName("parent_id")
        var parentId: Int? = null,

        @field:SerializedName("is_my_Interest")
        var isMyInterest: Boolean? = null,

        @field:SerializedName("interest_id")
        var interestId: Int? = null,
        var isUpdated: Boolean? = false

) : Chip, Parcelable {
    override fun getText(): String? {
        return interest
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as InterestsItem
        if (interestId != other.interestId) {
            return false
        }
        return true

    }

    override fun hashCode(): Int {
        return interestId!!
    }

    constructor(source: Parcel) : this(
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(hasChildren)
        dest.writeString(interest)
        dest.writeValue(parentId)
        dest.writeValue(isMyInterest)
        dest.writeValue(interestId)
    }

    companion object CREATOR : Parcelable.Creator<InterestsItem> {
        override fun createFromParcel(parcel: Parcel): InterestsItem {
            return InterestsItem(parcel)
        }

        override fun newArray(size: Int): Array<InterestsItem?> {
            return arrayOfNulls(size)
        }
    }
}