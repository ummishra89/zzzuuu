package com.mentorz.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import org.jetbrains.anko.db.*
import org.w3c.dom.Text

/**
 * Created by umesh on 20/07/17.
 */
class MentorzSqlHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "mentorz_db") {
//class MentorzSqlHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, ""+ Environment.getDataDirectory()+"/MentorzDb/"+"mentorz_db") {
    companion object {
        private var instance: MentorzSqlHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MentorzSqlHelper {
            if (instance == null) {
                instance = MentorzSqlHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Interest", true,
                "interest_id" to INTEGER + PRIMARY_KEY,
                "interest" to TEXT,
                "parent_id" to INTEGER,
                "has_children" to INTEGER,
                "is_my_Interest" to INTEGER
        )
        db.createTable("notification", true,
                "user_id" to INTEGER,
                "message" to TEXT,
                "post_id" to INTEGER,
                "push_type" to TEXT,
                "user_name" to TEXT,
                "time_stamp" to TEXT,
                "is_read" to INTEGER,
                "is_viewed" to INTEGER

        )
        db.createTable("pn_chat_buddy_info", true,
                "user_id" to INTEGER,
                "display_name" to TEXT,
                "chat_catagory" to INTEGER,
                "chat_id" to TEXT,
                "basic_info" to TEXT,
                "hres" to TEXT,
                "latest_msg_time_stamp" to TEXT,
                "lres" to INTEGER

        )
        db.createTable("pn_chat_message", true,
                "body" to TEXT,
                "chat_id" to INTEGER,
                "file_uri" to TEXT,
                "hres" to INTEGER,
                "is_ready_to_play" to INTEGER,
                "latitude" to INTEGER,
                "longitude" to INTEGER,
                "lres" to TEXT,
                "message_id" to TEXT,
                "sender_display_name" to TEXT,
                "sender_id" to INTEGER,
                "time_stamp" to TEXT,
                "type" to INTEGER,
                "is_sent" to INTEGER,
                "is_deliver" to INTEGER,
                "is_read" to INTEGER
        )
        db.createTable("unread_message", true,
                "count_of_unread_message" to INTEGER,
                "last_message_time_stamp" to INTEGER,
                "sender_id" to INTEGER,
                "is_seen" to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }


}