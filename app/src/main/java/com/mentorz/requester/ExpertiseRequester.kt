package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.expertise.DefaultExpertiseListener
import com.mentorz.expertise.Response
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 06/07/17.
 */
class ExpertiseRequester : BaseRequester {

    private var presenter: Any? = null
    private var listener: DefaultExpertiseListener? = null
    private var parentId: Int? = null

    constructor(presenter: Any?, listener: DefaultExpertiseListener?) : super() {
        this.presenter = presenter
        this.listener = listener
    }

    constructor(parentId: Int, presenter: Any?, listener: DefaultExpertiseListener?) : super() {
        this.presenter = presenter
        this.listener = listener
        this.parentId = parentId
    }

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            if (parentId != null) {
                mentorzApiResponse = HttpController.getExpertiseByParentId(parentId!!)
            } else {
                mentorzApiResponse = HttpController.getRootExpertise()

            }
            if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                callBack(mentorzApiResponse)
            }
        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {

        var response: Response? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<Response>(mentorzApiResponse.responseBody as String, Response::class.java)
            if (response != null) {
                if (parentId != 0) {
                    listener?.onDefaultChildExpertiseSuccess(response)

                } else {
                    listener?.onDefaultExpertiseSuccess(response)

                }
            }
        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            if (parentId != 0) {
                listener?.onDefaultChildExpertiseFail()

            } else {
                listener?.onDefaultExpertiseFail()

            }
        }
    }

}