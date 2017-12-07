package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by craterzone on 31/08/17.
 */
interface UpdatePushTokenListener : SessionExpiredListener, NetworkErrorListener {
}