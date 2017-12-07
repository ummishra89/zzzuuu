package com.mentorz.retrofit.listeners

import com.mentorz.listener.NetworkErrorListener
import com.mentorz.listener.SessionExpiredListener

/**
 * Created by craterzone on 27/07/17.
 */
interface RatingListener : SessionExpiredListener,NetworkErrorListener {
    fun onRatingSuccess(rating: Float, mentorId: Long)
    fun onRatingFail()
    fun onRateUserSuccess(){

    }
}