package com.mentorz.requester

import com.google.gson.reflect.TypeToken
import com.mentorz.MentorzApplication
import com.mentorz.match.UserListItem
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.review.ReviewResponseListener
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class GetReviewRequester(userId: Long, pageNo: Int, private var presenter: Any?, private var listener: ReviewResponseListener?) : BaseRequester() {
    private var pageNo: Int? = pageNo
    private var userId: Long? = userId

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getReview(userId!!, pageNo!!)
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var reviewList: MutableList<UserListItem>? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            reviewList = MentorzApplication.instance?.gson?.fromJson<MutableList<UserListItem>>(EncodingDecoding.decodeString(mentorzApiResponse.responseBody as String), object : TypeToken<ArrayList<UserListItem>>() {}.type)
            if (reviewList != null) {
                listener?.onReviewResponseSuccess(reviewList)
            }
        } else if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_NO_CONTENT) {
            listener?.reviewNoFound()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onReviewResponseFail()
        }
    }


}