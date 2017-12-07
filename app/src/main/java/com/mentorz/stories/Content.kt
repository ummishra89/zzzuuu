package com.mentorz.stories

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class Content(

        @field:SerializedName("content_id")
        val contentId: Int? = null,

        @field:SerializedName("media_type")
        var mediaType: String? = null,

        @field:SerializedName("hres_id")
        var hresId: String? = null,

        @field:SerializedName("lres_id")
        var lresId: String? = null,

        @field:SerializedName("description")
        var description: String? = ""
) : Parcelable {
        companion object {
                @JvmField val CREATOR: Parcelable.Creator<Content> = object : Parcelable.Creator<Content> {
                        override fun createFromParcel(source: Parcel): Content = Content(source)
                        override fun newArray(size: Int): Array<Content?> = arrayOfNulls(size)
                }
        }

        constructor(source: Parcel) : this(
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
                dest.writeValue(contentId)
                dest.writeString(mediaType)
                dest.writeString(hresId)
                dest.writeString(lresId)
                dest.writeString(description)
        }
}