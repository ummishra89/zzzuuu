package com.mentorz.expertise

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.plumillonforge.android.chipview.Chip

data class ExpertiseItem(

        @field:SerializedName("has_children")
        val hasChildren: Boolean? = null,

        @field:SerializedName("parent_id")
        val parentId: Int? = null,

        @field:SerializedName("is_my_expertise")
        var isMyExpertise: Boolean? = null,

        @field:SerializedName("expertise")
        val expertise: String? = null,

        @field:SerializedName("expertise_id")
        val expertiseId: Int? = null,
        var isUpdated: Boolean? = false
) : Chip, Parcelable {
    override fun getText(): String? {
        return expertise
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as ExpertiseItem
        if (expertiseId != other.expertiseId) {
            return false
        }
        return true

    }

    override fun hashCode(): Int {
        return expertiseId!!
    }

    constructor(source: Parcel) : this(
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(hasChildren)
        dest.writeValue(parentId)
        dest.writeValue(isMyExpertise)
        dest.writeString(expertise)
        dest.writeValue(expertiseId)
    }

    companion object CREATOR : Parcelable.Creator<ExpertiseItem> {
        override fun createFromParcel(parcel: Parcel): ExpertiseItem {
            return ExpertiseItem(parcel)
        }

        override fun newArray(size: Int): Array<ExpertiseItem?> {
            return arrayOfNulls(size)
        }
    }
}