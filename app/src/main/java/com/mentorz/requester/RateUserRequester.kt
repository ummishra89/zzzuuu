package com.mentorz.requester

import com.mentorz.model.Rating
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.RatingListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection


class RateUserRequester(mentorId: Long, rating: Rating, private var listener: RatingListener?) : BaseRequester() {
    private var presenter: Any? = null
    private var mentorId: Long? = mentorId
    private var rating: Rating? = rating

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.rateUser(mentorId!!,rating!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onRateUserSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onRatingFail()
        }
    }

    init {
        this.presenter = presenter
    }

}