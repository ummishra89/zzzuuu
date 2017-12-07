package com.mentorz.pubnub

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.EditText
import com.google.gson.JsonElement
import com.mentorz.MentorzApplication
import com.mentorz.constants.PushType
import com.mentorz.controller.Controller
import com.mentorz.database.DbManager
import com.mentorz.manager.PubNubManager
import com.mentorz.model.NotificationData
import com.mentorz.utils.Constant
import com.mentorz.utils.NotificationUtils
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNOperationType
import com.pubnub.api.enums.PNPushType
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.history.PNHistoryItemResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import org.json.JSONObject
import com.pubnub.api.models.consumer.history.PNHistoryResult
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult
import okhttp3.HttpUrl
import org.jetbrains.anko.doAsync
import retrofit2.http.HTTP
import java.net.HttpURLConnection


/**
 * Created by craterzone on 11/3/2017.
 */
class PubNubManagerService : Service() {

    // STAGING
    private val PUB_KEY = "pub-c-860ece69-900a-42b1-92e4-d46445c0f7df"
    private val SUB_KEY = "sub-c-e7385b06-fce4-11e6-99d2-0619f8945a4f"


    //PROD
    //  private val PUB_KEY  = "pub-c-07930415-b78d-4649-9a0a-ec4cab137854"
    //  private val SUB_KEY  = "sub-c-8032ad00-354a-11e7-a58b-02ee2ddab7fe"


    companion object {
        public var instance: PubNubManagerService = PubNubManagerService()
        @JvmStatic
        var isDatSent: Boolean = false
    }

    private val TAG = PubNubManagerService::class.java.simpleName
    override fun onBind(p0: Intent?): IBinder? {

        return null

    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        initPubNub()
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        if (Controller.pubNub != null) {
            Controller.pubNub?.unsubscribe()
        }
        startService(Intent(applicationContext, PubNubManagerService::class.java))

    }

    private val lock = Any()
    fun initPubNub() {
        if (MentorzApplication.instance?.prefs?.getUserId() == 0L) {
            return
        }
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = SUB_KEY
        pnConfiguration.publishKey = PUB_KEY
        if (Controller.pubNub == null) {
            Controller.pubNub = PubNub(pnConfiguration)
            Controller.pubNub?.addListener(subscribeCallBack)
            Controller.pubNub?.subscribe()!!.channels(mutableListOf(myChannelName())).execute()


        } else {
              Controller.pubNub?.unsubscribeAll()
                Controller.pubNub?.unsubscribe()
                Controller.pubNub=null
                initPubNub()


        }

    }

    fun myChannelName(): String {
        return MentorzApplication.instance?.prefs?.getUserId().toString()
    }

    var subscribeCallBack = object : SubscribeCallback() {
        override fun status(pubnub: PubNub?, status: PNStatus?) {
            when (status!!.operation) {
                PNOperationType.PNSubscribeOperation -> {
                    Log.d("Hello", "PNSubscribeOperation")
                }
                PNOperationType.PNUnsubscribeOperation -> {
                    when (status.category) {
                        PNStatusCategory.PNAccessDeniedCategory -> {
                            Log.d("Hello", "PNAccessDeniedCategory")
                        }
                        PNStatusCategory.PNConnectedCategory -> {
                            Log.d(TAG, "PNConnectedCategory")
                        }
                        PNStatusCategory.PNDisconnectedCategory -> {
                            Log.d(TAG, "PNDisconnectedCategory")
                        }
                        PNStatusCategory.PNReconnectedCategory -> {
                            Log.d(TAG, "PNReconnectedCategory")
                        }
                        PNStatusCategory.PNUnexpectedDisconnectCategory -> {
                            Log.d(TAG, "PNUnexpectedDisconnectCategory")
                            pubnub?.reconnect()
                        }
                        PNStatusCategory.PNTimeoutCategory -> {
                            Log.d(TAG, "PNTimeoutCategory")
                            pubnub?.reconnect()
                        }
                        else -> {
                            Log.d(TAG, "SomeThing")
                        }
                    }

                }
                PNOperationType.PNHeartbeatOperation -> {
                    if (status.isError) {
                        Log.d(TAG, " PNHeartbeatOperation working  error")
                    } else {
                        Log.d(TAG, "PNHeartbeatOperation working succes ")
                    }
                }

                else -> {
                }
            }
        }

        override fun presence(pubnub: PubNub?, presence: PNPresenceEventResult?) {

        }

        override fun message(pubnub: PubNub?, message: PNMessageResult?) {
            Log.d("aaa", message.toString())
            val intent = Intent(Constant.ACTION_PUB_NUB_MESSAGE_RECEIVED)
            val packet = MentorzApplication.instance?.gson?.fromJson<StreamChatPacket>(message!!.message.asJsonObject, StreamChatPacket::class.java)
            intent.putExtra("packet", packet)
            intent.putExtra("aps", packet?.pn_apns?.aps)

            if (packet?.chatId == Controller.chatId) {
                MentorzApplication.applicationContext().sendBroadcast(intent)
                packet?.pn_apns?.aps?.badge = 1
                DbManager.getInstance(MentorzApplication.applicationContext()).insertChatMassage(packet)
                Log.d(TAG, "message received:" + message?.message.toString())
            } else {

                var notificationData = NotificationData()
                notificationData.pushType = PushType.CHAT_MESSAGE_RECEIVED
                notificationData.userId = packet?.senderId
                notificationData.postId = packet?.chatId
                notificationData.userName = packet?.senderDisplayName
                NotificationUtils.sendNotification(MentorzApplication.applicationContext(), "Mentorz", "" + packet?.senderDisplayName + " : " + packet?.body, notificationData)
                packet?.pn_apns?.aps?.badge = 0
                DbManager.getInstance(MentorzApplication.applicationContext()).insertChatMassage(packet)
                val intent = Intent(Constant.ACTION_PUB_NUB_MESSAGE_UNREAD_COUNT)
                intent.putExtra("packet", packet)
                intent.putExtra("aps", packet?.pn_apns?.aps)
                MentorzApplication.applicationContext().sendBroadcast(intent)
            }
        }

    }


    //var isDatSent :Boolean=false
    fun publishMessage(editText: EditText, context: Context, streamChatPacket: StreamChatPacket, channelName: String, payLoad: JSONObject) {
        if (Controller.pubNub == null) {
            initPubNub()
        }
        Controller.pubNub?.publish()!!
                .message(streamChatPacket)
                .channel(channelName)
                .async(object : PNCallback<PNPublishResult>() {
                    override fun onResponse(result: PNPublishResult?, status: PNStatus?) {
                        if (status!!.isError) {
                            isDatSent = false
                            Log.d(TAG, "" + status.errorData.information)
                        } else {
                            isDatSent = true
                            Log.d(TAG, "message published")
                        }
                        val intent = Intent()
                        intent.action = Constant.ACTION_SENDING_CHAT_MESSAGE_STATUS
                        intent.putExtra("is_message_sent", isDatSent);
                        intent.putExtra("packet", streamChatPacket)
                        intent.putExtra("aps", streamChatPacket.pn_apns?.aps)
                        intent.putExtra("data", streamChatPacket.pn_gcm?.data)
                        context?.sendBroadcast(intent)
                    }

                })
    }


    public fun punNubMessageHistory(firstTimeStamp: Long, lastTimeStamp: Long) {
        try {
            var historyResult = object : PNCallback<PNHistoryResult>(
            ) {
                override fun onResponse(result: PNHistoryResult?, status: PNStatus?) {
                    consmeHistoryMessages(result, status)
                }
            }
            if (Controller.pubNub != null) {
                Controller.pubNub!!.history()
                        .channel(myChannelName()) // where to fetch history from
                           .start(firstTimeStamp)
                          .end(lastTimeStamp)
                        .reverse(true)
                        .count(20) // how many items to fetch
                        .async(historyResult)// last timestamp
            }
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "" + e.toString())
        } catch (e: Exception) {
            Log.e(TAG, "" + e.toString())
        }

    }

    fun registerForPushNotifications() {
        Controller.pubNub?.addPushNotificationsOnChannels()
    }

    fun removeAllPushNotification() {
        Controller.pubNub?.removeAllPushNotificationsFromDeviceWithPushToken()
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    fun consmeHistoryMessages(result: PNHistoryResult?, status: PNStatus?) {
        if (status?.statusCode == HttpURLConnection.HTTP_OK && result!=null) {
            var list : List<PNHistoryItemResult>?  =result?.messages
            for (item in list!!){
                messageManager (item.entry)
            }
        }

    }

    fun messageManager(message: JsonElement) {

        Log.d("aaa", message.toString())
        val intent = Intent(Constant.ACTION_PUB_NUB_MESSAGE_RECEIVED)
        val packet = MentorzApplication.instance?.gson?.fromJson<StreamChatPacket>(message.asJsonObject, StreamChatPacket::class.java)
        intent.putExtra("packet", packet)
        intent.putExtra("aps", packet?.pn_apns?.aps)

        if (packet?.chatId == Controller.chatId) {
            MentorzApplication.applicationContext().sendBroadcast(intent)
            packet?.pn_apns?.aps?.badge = 1
            DbManager.getInstance(MentorzApplication.applicationContext()).insertChatMassage(packet)
            Log.d(TAG, "message received:" + message.toString())
        } else {

            var notificationData = NotificationData()
            notificationData.pushType = PushType.CHAT_MESSAGE_RECEIVED
            notificationData.userId = packet?.senderId
            notificationData.postId = packet?.chatId
            notificationData.userName = packet?.senderDisplayName
            NotificationUtils.sendNotification(MentorzApplication.applicationContext(), "Mentorz", "" + packet?.senderDisplayName + " : " + packet?.body, notificationData)
            packet?.pn_apns?.aps?.badge = 0
            DbManager.getInstance(MentorzApplication.applicationContext()).insertChatMassage(packet)
            val intent = Intent(Constant.ACTION_PUB_NUB_MESSAGE_UNREAD_COUNT)
            intent.putExtra("packet", packet)
            intent.putExtra("aps", packet?.pn_apns?.aps)
            MentorzApplication.applicationContext().sendBroadcast(intent)
        }
    }
}