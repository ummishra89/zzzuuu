package com.mentorz.requester

import com.google.gson.annotations.SerializedName
import com.mentorz.listener.AddPostListener
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.stories.Content
import com.mentorz.utils.EncodingDecoding
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by aMAN GUPTA on 20/7/17.
 */
class AddPostRequester(content: Content, private var listener: AddPostListener?) : BaseRequester() {
    private var content: Content? = content

    fun execute() {
        doAsync {
            //run in background
            content?.description = EncodingDecoding.encodeString(content?.description!!)
            val mentorzApiResponse: MentorzApiResponse? = HttpController.addPost(ContentRequest(content))
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            listener?.onAddPostSuccess()
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            listener?.onAddPostFail()
        }
    }
    data class ContentRequest(

        @field:SerializedName("content")
        val content: Content? = null
    )

}