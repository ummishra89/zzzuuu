package com.mentorz.requester

import com.mentorz.interest.InterestPresenterImpl
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.interest.UpdateInterestListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 06/07/17.
 */
class UpdateInterestRequester(updateInterestRequest: InterestPresenterImpl.UpdateInterestRequest, private var presenter: Any?, private var listener: UpdateInterestListener?) : BaseRequester() {

    private var updateInterestRequest: InterestPresenterImpl.UpdateInterestRequest? = updateInterestRequest

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            mentorzApiResponse = HttpController.updateInterest(updateInterestRequest)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onUpdateInterestSuccess(mentorzApiResponse)

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onUpdateInterestFail()
        }
    }

}