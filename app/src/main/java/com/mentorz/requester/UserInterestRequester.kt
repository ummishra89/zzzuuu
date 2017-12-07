package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.interest.InterestResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.interest.UserInterestListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 06/07/17.
 */
class UserInterestRequester(private var presenter: Any?, private var listener: UserInterestListener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            mentorzApiResponse = HttpController.getInterest()
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {

        var interestResponse: InterestResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            interestResponse = MentorzApplication.instance?.gson?.fromJson<InterestResponse>(mentorzApiResponse.responseBody as String, InterestResponse::class.java)
            if (interestResponse != null) {
                listener?.onUserInterestSuccess(interestResponse)
            }

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onUserInterestFail()
        }
    }

}