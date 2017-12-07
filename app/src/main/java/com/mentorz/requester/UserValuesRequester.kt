package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.ValuesResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UserValuesLintener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class UserValuesRequester(private var presenter: Any?, private var listener: UserValuesLintener?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getUserValues()
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var valuesResponse: ValuesResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            valuesResponse = MentorzApplication.instance?.gson?.fromJson<ValuesResponse>(mentorzApiResponse.responseBody as String, ValuesResponse::class.java)
            if (valuesResponse != null) {
                listener?.userValues(valuesResponse)
            }
            return
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.noUserValues()
        } else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.userValuesFail()
        }
    }
}