package com.mentorz.controller

import com.mentorz.match.UserListItem
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub

/**
 * Created by craterzone on 11/3/2017.
 */
object Controller{

    // STAGING
    private val PUB_KEY = "pub-c-860ece69-900a-42b1-92e4-d46445c0f7df"
    private val SUB_KEY = "sub-c-e7385b06-fce4-11e6-99d2-0619f8945a4f"


    //PROD
    //  private val PUB_KEY  = "pub-c-07930415-b78d-4649-9a0a-ec4cab137854"
    //  private val SUB_KEY  = "sub-c-8032ad00-354a-11e7-a58b-02ee2ddab7fe"

    var chatId:Long? = null
    var pubNub : PubNub? = null
    var  userList: MutableList<UserListItem>? = null
    var unReadCount :Int? = null

}