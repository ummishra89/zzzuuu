package com.mentorz.pubnub

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by craterzone on 11/8/2017.
 */
data class Data (

        @field:SerializedName("summary")
        var senderSummary: String? = null,

        @field:SerializedName("sender_lres")
        var senderLres: String? = null,

        @field:SerializedName("PushType")
        var pushType: Int? = null,

        @field:SerializedName("badge")
        var badge: Int? = null,

        @field:SerializedName("senderName")
        var senderName: String? = null,

        @field:SerializedName("senderId")
        var senderId: Long? = null,

        @field:SerializedName("alert")
        var alert: String? = null,

        @field:SerializedName("sound")
        var sound: String? = null,

        @field:SerializedName("content-available")
        var contentAvailable: Int? = null,

        @field:SerializedName("UserType")
        var userType: Int? = null,

        @field:SerializedName("uri")
        var uri: String? = null

): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
                    parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderSummary)
        parcel.writeString(senderLres)
        parcel.writeValue(pushType)
        parcel.writeValue(badge)
        parcel.writeString(senderName)
        parcel.writeValue(senderId)
        parcel.writeString(alert)
        parcel.writeString(sound)
        parcel.writeValue(contentAvailable)
        parcel.writeValue(userType)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}
