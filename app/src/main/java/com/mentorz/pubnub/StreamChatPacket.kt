package com.mentorz.pubnub

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import com.mentorz.model.MessageType

/**
 * Created by umesh on 01/09/17.
 */
data class StreamChatPacket(var body: String?=null, var chatId: Long?=null, var messageId: String?=null, var senderId: Long?=null, var senderDisplayName: String?=null, var timestamp: String?=null, var type: Int?=null, var isRead: Boolean?=null, var isSent: Boolean?=null, var isDelivered: Boolean?=null, var pn_apns: PnAPNS? = null, var pn_gcm: PnGcm? = null) : Parcelable,StreamMessage(senderId){
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<StreamChatPacket> = object : Parcelable.Creator<StreamChatPacket> {
            override fun createFromParcel(source: Parcel): StreamChatPacket = StreamChatPacket(source)
            override fun newArray(size: Int): Array<StreamChatPacket?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readString(),
    source.readValue(Long::class.java.classLoader) as Long?,
    source.readString(),
    source.readString(),
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readValue(Boolean::class.java.classLoader) as Boolean?,
    source.readValue(Boolean::class.java.classLoader) as Boolean?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(body)
        dest.writeValue(chatId)
        dest.writeString(messageId)
        dest.writeValue(senderId)
        dest.writeString(senderDisplayName)
        dest.writeString(timestamp)
        dest.writeValue(type)
        dest.writeValue(isRead)
        dest.writeValue(isSent)
        dest.writeValue(isDelivered)

    }
}

