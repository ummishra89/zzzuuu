
package com.mentorz.pubnub

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
data class Aps (

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
		var userType: Int? = null
): Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readValue(Int::class.java.classLoader) as? Int,
			parcel.readValue(Int::class.java.classLoader) as? Int,
			parcel.readString(),
			parcel.readValue(Long::class.java.classLoader) as? Long,
			parcel.readString(),
			parcel.readString(),
			parcel.readValue(Int::class.java.classLoader) as? Int,
			parcel.readValue(Int::class.java.classLoader) as? Int) {
	}

	override fun writeToParcel(p0: Parcel?, p1: Int) {
		p0?.writeString(senderLres)
		p0?.writeValue(pushType)
		p0?.writeValue(badge)
		p0?.writeString(senderName)
		p0?.writeValue(senderId)
		p0?.writeString(alert)
		p0?.writeString(sound)
		p0?.writeValue(contentAvailable)
		p0?.writeValue(userType)


	}

	override fun describeContents()=0

	companion object CREATOR : Parcelable.Creator<Aps> {
		override fun createFromParcel(parcel: Parcel): Aps {
			return Aps(parcel)
		}

		override fun newArray(size: Int): Array<Aps?> {
			return arrayOfNulls(size)
		}
	}
}