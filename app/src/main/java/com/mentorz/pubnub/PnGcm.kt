package com.mentorz.pubnub

import com.google.gson.annotations.SerializedName

/**
 * Created by craterzone on 11/8/2017.
 */
data class PnGcm (

    @field:SerializedName("data")
    var data: Data? = null
)