package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.listener.MatchListener
import com.mentorz.match.MentorResponse
import com.mentorz.match.MyMentorRequest
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class MentorsRequester(offset: Int, mentorRequest: MyMentorRequest, private var presenter: Any?, private var listener: MatchListener?) : BaseRequester() {
    private var mentorRequest: MyMentorRequest? = mentorRequest
    private var limit: Int = 20
    private var offset: Int? = offset

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getMyMentorList(mentorRequest!!, offset!!, limit)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var mentorResponse: MentorResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            // mentorResponse = MentorzApplication.instance?.gson?.fromJson<MentorResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), MentorResponse::class.java)
            mentorResponse = MentorzApplication.instance?.gson?.fromJson<MentorResponse>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), MentorResponse::class.java)

            if (mentorResponse != null) {
                listener?.onMentorsSuccess(mentorResponse.userList)
            }
            return
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.onNoMentors()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onMentorsFail()
        }
    }


}