package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.UrlResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.UploadSessionUrlListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 27/07/17.
 */
class UploadSessionUrlRequester(private var listener: UploadSessionUrlListener?, private var contentType: String?, private var token: String?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getUploadSessionUrl(token, contentType)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var response: UrlResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<UrlResponse>(mentorzApiResponse.responseBody as String, UrlResponse::class.java)
            if (response != null) {
                listener?.uploadSessionUrlSuccess(response.value!!)
            }
            return
        }
        else {
            listener?.uploadSessionUrlFail()
        }
    }
}

