package com.mentorz.extensions

/**
 * Created by umesh on 08/08/17.
 */

import android.app.Notification
import android.content.Context
import android.support.v4.app.NotificationCompat.Builder

inline fun Notification.build(context: Context, func: Builder.() -> Unit): Notification {
    val builder = Builder(context)
    builder.func()
    return builder.build()
}