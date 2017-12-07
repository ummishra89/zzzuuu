package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.stories.PostResponse
import com.mentorz.stories.PostResponseListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class BoardRequester(pageNo: Int, private var presenter: Any?, private var listener: PostResponseListener?) : BaseRequester() {
    private var pageNo: Int? = pageNo

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getBoard(pageNo!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var mentorResponse: PostResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            mentorResponse = MentorzApplication.instance?.gson?.fromJson<PostResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), PostResponse::class.java)
            if (mentorResponse != null) {
                listener?.onPostSuccess(mentorResponse.postList!!)
            }
            return
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.noPostFound()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onPostFail()
        }
    }


}