
package com.mentorz.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mentorz.constants.PushType
import com.mentorz.interest.InterestsItem
import com.mentorz.model.Notification
import com.mentorz.pubnub.PnAPNS
import com.mentorz.pubnub.StreamChatPacket
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select


/**
 * Created by umesh on 20/07/17.
 */


class DbManager(ctx: Context) {
    var context: Context? = null

    init {
        context = ctx
    }

    companion object {
        private var instance: DbManager? = null
        private var notificationDataChangeListener: NotificationDataChangeListener? = null
        private var notificationDataReadListener: NotificationDataReadListener? = null


        fun getInstance(ctx: Context): DbManager {
            if (instance == null) {
                instance = DbManager(ctx.applicationContext)
            }
            return instance!!
        }

        fun registerNotificationDataChangeListener(notificationDataChangeListener: NotificationDataChangeListener?){
            this.notificationDataChangeListener = notificationDataChangeListener
        }
    }

    fun insertNotification(notification: Notification?) {
        val c : Cursor
        context?.database?.use {
            val query = "DELETE FROM notification WHERE user_id = "+notification?.userId+" AND push_type = '"+notification?.pushType+"' AND post_id = "+notification?.postId
            execSQL(query)
           // notificationDataChangeListener?.onNotificationDataChange()

        }
        context?.database?.use{
            insert("notification", "user_id" to notification?.userId,
                    "message" to notification?.message,
                    "post_id" to notification?.postId,
                    "push_type" to notification?.pushType,
                    "time_stamp" to notification?.timeStamp,
                    "user_name" to notification?.userName,
                    "is_read" to 0, "is_viewed" to 0)
        }
        notificationDataChangeListener?.onNotificationDataChange()

    }

    fun insertChatMassage(streamChatPacket: StreamChatPacket?) {
        context?.database?.use{
            insert("pn_chat_message",
                    "body" to streamChatPacket?.body,
                    "chat_id" to streamChatPacket?.chatId,
                    "file_uri" to streamChatPacket?.chatId,
                    "hres" to streamChatPacket?.chatId,
                    "is_ready_to_play" to 0,
                    "latitude" to streamChatPacket?.chatId,
                    "longitude" to streamChatPacket?.chatId,
                    "lres" to streamChatPacket?.pn_apns?.aps?.senderLres,
                    "message_id" to streamChatPacket?.messageId,
                    "sender_display_name" to streamChatPacket?.senderDisplayName,
                    "sender_id" to streamChatPacket?.senderId,
                    "time_stamp" to streamChatPacket?.timestamp,
                    "type" to streamChatPacket?.type,
                    "is_sent" to 0,
                    "is_deliver" to 0,
                    "is_read" to streamChatPacket?.pn_apns?.aps?.badge
            )
        }
    }

    fun deleteUnpublishedSentMessageFromChatTable(streamChatPacket: StreamChatPacket){
        context?.database?.use {
            val query = "DELETE FROM pn_chat_message WHERE chat_id = "+streamChatPacket?.chatId+" AND message_id = '"+streamChatPacket?.messageId+"' AND post_id = "+streamChatPacket?.senderId
            execSQL(query)
        }

    }

    fun getUnReadCountForUser(senderId:Long?) :Int{
        var count :Int =0
        context?.database?.use {
            select("pn_chat_message", "body", "chat_id", "file_uri", "hres", "is_ready_to_play","latitude","longitude","lres","message_id","sender_display_name","sender_id","time_stamp","type","is_sent","is_deliver","is_read").whereSimple("(sender_id = ?)", ""+senderId).exec {
                while (moveToNext()) {
                    if(getInt(15) == 0)
                    { count ++}
                }
            }

        }
        return count

    }
    fun getUnReadCountForUserTest(senderId:Long?) :Int{
        var count = 0
        context?.database?.use {
            val countQuery = "SELECT  * FROM pn_chat_message WHERE sender_id = $senderId AND is_read = 0"
            val cursor = rawQuery(countQuery, null)
            count = cursor.count
            cursor.close()
        }
        return count

    }

    fun getLastTimestempOfMessageFromChat() :String{
        var  time  : String = ""
        context?.database?.use {
            val countQuery = "SELECT * FROM pn_chat_message ORDER BY time_stamp DESC LIMIT 1"
            val cursor = rawQuery(countQuery, null)
            if (cursor.moveToFirst())
                time = cursor.getString(cursor.getColumnIndex("time_stamp"))
            cursor.close()
        }
        return time

    }
    fun setReadAllChatMessage(senderId:Long){
        context?.database?.use {
            val query = "UPDATE pn_chat_message SET is_read = 1 WHERE sender_id = $senderId"
            execSQL(query)
            notificationDataChangeListener?.onNotificationDataChange()
        }
    }


    fun insertUserForChat(notification: Notification?) {

        context?.database?.use{
            insert("notification", "user_id" to notification?.userId,
                    "message" to notification?.message,
                    "post_id" to notification?.postId,
                    "push_type" to notification?.pushType,
                    "time_stamp" to notification?.timeStamp,
                    "user_name" to notification?.userName,
                    "is_read" to 0, "is_viewed" to 0)
        }
        notificationDataChangeListener?.onNotificationDataChange()

    }


    fun setReadAllRequestNotification(){
        context?.database?.use {
            val query = "UPDATE notification SET is_read = 1 WHERE push_type = '"+PushType.SEND_REQUEST+"'"
            execSQL(query)
            notificationDataChangeListener?.onNotificationDataChange()
        }
    }


    fun getNotViewedNotificationCount(): Int{
        var count = 0
        context?.database?.use {
            val countQuery = "SELECT  * FROM notification WHERE is_viewed = 0"
            val cursor = rawQuery(countQuery, null)
            count = cursor.count
            cursor.close()
        }
        return count

    }
    fun getChatMessages(chatId:Long): MutableList<StreamChatPacket>{
        val list: MutableList<StreamChatPacket> = mutableListOf()
        context?.database?.use {
            select("pn_chat_message", "body", "chat_id", "file_uri", "hres", "is_ready_to_play","latitude","longitude","lres","message_id","sender_display_name","sender_id","time_stamp","type","is_sent","is_deliver","is_read").whereSimple("(chat_id = ?)", ""+chatId).exec {
                while (moveToNext()) {
                    val item = StreamChatPacket()
                    item.body = getString(0)
                     val pnaps = PnAPNS()
                    item.chatId = getLong(1)
                    pnaps.aps?.senderLres = getString(7)
                    item.pn_apns = pnaps
                    item.senderDisplayName =getString(9)
                    item.messageId =getString(8)
                    item.type =getInt(12)
                    item.isDelivered = getInt(14) != 0
                    item.isRead = getInt(15) != 0
                    item.timestamp =getString(11)
                    item.senderId = getLong(10)
                    list.add(item)
                }
            }

        }
        return list
    }
    fun deleteNotification(userId: Long?){
        context?.database?.use {
            val query = "DELETE FROM notification WHERE user_id = "+userId
            execSQL(query)
            notificationDataChangeListener?.onNotificationDataChange()

        }
    }


    fun setAllViewedNotification(){
        context?.database?.use {
            val query = "UPDATE notification SET is_viewed = 1 WHERE is_viewed = 0"
            execSQL(query)
         //   notificationDataChangeListener?.onNotificationDataChange()
        }
    }

    fun setReadNotification(userId: Long, pushType: String?) {
        context?.database?.use {
            val query = "UPDATE notification SET is_read = 1 WHERE user_id = $userId AND push_type = '$pushType'"
            execSQL(query)
            notificationDataChangeListener?.onNotificationDataChange()

        }
    }

    fun setReadNotification(userId: Long, postId: Long, pushType: String?) {
        context?.database?.use {
            val query = "UPDATE notification SET is_read = 1 WHERE user_id = $userId AND push_type = '$pushType' AND post_id = $postId"
            execSQL(query)
            val query1 = "UPDATE notification SET is_viewed = 1 WHERE user_id = $userId AND push_type = '$pushType' AND post_id = $postId"
            execSQL(query1)
            notificationDataChangeListener?.onNotificationDataChange()

        }
    }

    fun getNotifications(): MutableList<Notification> {
        val list: MutableList<Notification> = mutableListOf()
        context?.database?.use {
            select("notification", "message","time_stamp", "user_id", "post_id", "push_type", "user_name", "is_read").exec {
                while (moveToNext()) {
                    val item = Notification(getString(0),getLong(1),getLong(2),getLong(3),getString(4),EncodingDecoding.decodeString(getString(5)))
                    item.isRead = getInt(6) != 0
                    list.add(item)
                }
            }

        }
        return list
    }


        fun insertInterest(interestList: List<InterestsItem>) {
        context?.database?.use {

            for (item in interestList) {
                Log.d("DbManager", "interest_id:" + item.interestId)

                val interest = item.interestId
                insert("Interest", "interest_id" to item.interestId, "interest" to item.interest, "parent_id" to item.parentId,
                        "has_children" to (if (item.hasChildren!!) {
                            1
                        } else {
                            0
                        }),
                        "is_my_Interest" to (if (item.isMyInterest!!) {
                            1
                        } else {
                            0
                        }))
            }


        }
    }

    fun updateInterest(interestList: List<InterestsItem>) {
        context?.database?.use {

            interestList.forEach {
                val values: ContentValues = ContentValues()
                values.put("is_my_Interest", it.isMyInterest)
                update("Interest", values, "interest_id=" + it.interestId, null)
            }
        }
    }

    fun getInterest(): MutableList<InterestsItem> {
        val list: MutableList<InterestsItem> = mutableListOf()
        context?.database?.use {
            select("Interest", "interest_id", "interest", "parent_id", "has_children", "is_my_Interest").whereSimple("(parent_id = ?)", "0").exec {
                while (moveToNext()) {
                    val item = InterestsItem()
                    item.interestId = getInt(0)
                    item.interest = getString(1)
                    item.parentId = getInt(2)
                    item.hasChildren = getInt(3) != 0
                    item.isMyInterest = getInt(4) != 0
                    list.add(item)


                }
            }

        }
        return list
    }

    fun getInterestByParentId(parentId: Int): MutableList<InterestsItem> {
        val list: MutableList<InterestsItem> = mutableListOf()
        context?.database?.use {
            select("Interest", "interest_id", "interest", "parent_id", "has_children", "is_my_Interest").whereSimple("(parent_id = ?)", "$parentId").exec {
                while (moveToNext()) {
                    val item = InterestsItem()
                    item.interestId = getInt(0)
                    item.interest = getString(1)
                    item.parentId = getInt(2)
                    item.hasChildren = getInt(3) != 0
                    item.isMyInterest = getInt(4) != 0
                    list.add(item)
                }
            }

        }
        return list
    }

    fun clear() {
        context?.database?.use {
            execSQL("DELETE FROM Interest")
            execSQL("DELETE FROM notification")
        }
    }

    // Access property for Context
    val Context.database: MentorzSqlHelper
        get() = MentorzSqlHelper.getInstance(getApplicationContext())

    interface NotificationDataChangeListener {
        fun onNotificationDataChange()
    }
    interface NotificationDataReadListener {
        fun onNotificationDataRead()
    }
}
