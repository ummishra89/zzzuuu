package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.RatingResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.RatingListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 27/07/17.
 */
class RatingRequester(private var userId: Long?, private var listener: RatingListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getUserRating(userId)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var ratingResponse: RatingResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            ratingResponse = MentorzApplication.instance?.gson?.fromJson<RatingResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), RatingResponse::class.java)
            if (ratingResponse != null) {
                listener?.onRatingSuccess(ratingResponse.rating!!, userId!!)
            }

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onRatingFail()
        }
    }


}