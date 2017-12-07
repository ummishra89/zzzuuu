package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.match.MentorResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.userdetails.UserDetailsResponseListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetFollowingRequester(mentorId: Long, pageNo: Int, private var listener: UserDetailsResponseListener?) : BaseRequester() {
    private var presenter: Any? = null
    private var mentorId:Long? = mentorId
    private var pageNo: Int? = pageNo

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getFollowing(mentorId!!, pageNo!!)
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
                listener?.onFollowingSuccess(mentorResponse.userList!!)
            }
            return
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.noUserFound()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onFollowingFail()
        }
    }

    init {
        this.presenter = presenter
    }


}