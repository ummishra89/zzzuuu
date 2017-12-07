package com.mentorz.manager

import android.content.Intent
import android.util.Log
import com.mentorz.MentorzApplication
import com.mentorz.constants.PushType
import com.mentorz.controller.Controller
import com.mentorz.database.DbManager
import com.mentorz.model.NotificationData
import com.mentorz.pubnub.StreamChatPacket
import com.mentorz.utils.Constant
import com.mentorz.utils.NotificationUtils
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import org.json.JSONObject


/**
 * Created by umesh on 01/09/17.
 */
class PubNubManager {

    fun initPubNub() {
        if(MentorzApplication.instance?.prefs?.getUserId()==0L){
           return
        }
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = SUB_KEY
        pnConfiguration.publishKey = PUB_KEY
        if(pubNub==null){
        pubNub = PubNub(pnConfiguration)
            pubNub?.addListener(subscribeCallBack)
            pubNub?.subscribe()!!.channels(mutableListOf(myChannelName())).execute()
        }else {
            pubNub?.addListener(subscribeCallBack)
            pubNub?.subscribe()!!.channels(mutableListOf(myChannelName())).execute()
        }
    }

    companion object {

        private val TAG = "PubNubManager"
        // STAGING
        private val PUB_KEY = "pub-c-860ece69-900a-42b1-92e4-d46445c0f7df"
        private val SUB_KEY = "sub-c-e7385b06-fce4-11e6-99d2-0619f8945a4f"


        //PROD
        //  private val PUB_KEY  = "pub-c-07930415-b78d-4649-9a0a-ec4cab137854"
        //  private val SUB_KEY  = "sub-c-8032ad00-354a-11e7-a58b-02ee2ddab7fe"
        var instance: PubNubManager = PubNubManager()
        private var pubNub: PubNub? = null
    }

    fun myChannelName(): String {
        return MentorzApplication.instance?.prefs?.getUserId().toString()
    }

    var subscribeCallBack = object : SubscribeCallback() {
        override fun status(pubnub: PubNub?, status: PNStatus?) {
            when (status!!.operation) {
                PNOperationType.PNSubscribeOperation -> {

                }
                PNOperationType.PNUnsubscribeOperation -> {
                    when (status.category) {
                        PNStatusCategory.PNAccessDeniedCategory -> {

                        }
                        PNStatusCategory.PNConnectedCategory -> {

                        }
                        PNStatusCategory.PNDisconnectedCategory -> {

                        }
                        PNStatusCategory.PNReconnectedCategory -> {

                        }
                        PNStatusCategory.PNUnexpectedDisconnectCategory -> {
                            pubnub?.reconnect()
                        }
                        PNStatusCategory.PNTimeoutCategory -> {
                            pubnub?.reconnect()
                        }
                        else -> {
                        }
                    }

                }
                PNOperationType.PNHeartbeatOperation->{
                    if(status.isError){
                        // There is error  with heart beat operation
                    }
                    else{
                        //heart beat operation  was successfull
                    }
                }

                else -> {
                }
            }
        }

        override fun presence(pubnub: PubNub?, presence: PNPresenceEventResult?) {

        }

        override fun message(pubnub: PubNub?, message: PNMessageResult?) {
            Log.d("aaa",message.toString())
            val intent: Intent = Intent(Constant.ACTION_PUB_NUB_MESSAGE_RECEIVED)
            val packet =MentorzApplication.instance?.gson?.fromJson<StreamChatPacket>(message!!.message.asJsonObject,StreamChatPacket::class.java)
            intent.putExtra("packet",packet)
            DbManager.getInstance(MentorzApplication.applicationContext()).insertChatMassage(packet)
            if(packet?.chatId==Controller.chatId){
            MentorzApplication.applicationContext().sendBroadcast(intent)
            Log.d(TAG, "message received:"+message?.message.toString())}
            else {
                var notificationData = NotificationData()
                notificationData.pushType = PushType.CHAT_MESSAGE_RECEIVED
                notificationData.userId = packet?.senderId
                notificationData.postId = packet?.chatId
                notificationData.userName = packet?.senderDisplayName

                NotificationUtils.sendNotification(MentorzApplication.applicationContext(),"Mentorz",""+packet?.senderDisplayName+" : "+packet?.body, notificationData)
            }
        }

    }


    fun publishMessage(streamChatPacket: StreamChatPacket,channelName: String,payLoad:JSONObject) {
        if (pubNub == null) {
            initPubNub()
        }
        pubNub?.publish()!!
                .message(streamChatPacket)
                .channel(channelName)
                .async(object : PNCallback<PNPublishResult>() {
                    override fun onResponse(result: PNPublishResult?, status: PNStatus?) {
                        if (status!!.isError) {
                           Log.d(TAG, ""+status.errorData.information)
                        } else {
                            Log.d(TAG, "message published")
                        }
                    }

                })
    }

    fun registerForPushNotifications() {
        pubNub?.addPushNotificationsOnChannels()

    }

    fun removeAllPushNotification() {
        pubNub?.removeAllPushNotificationsFromDeviceWithPushToken()

    }
}