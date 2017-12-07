package com.mentorz.pubnub

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
data class PnAPNS(

	@field:SerializedName("aps")
	var aps: Aps? = null
)