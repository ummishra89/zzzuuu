package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.interest.InterestResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.interest.DefaultChildInterestListener
import com.mentorz.retrofit.listeners.DefaultInterestListener
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 06/07/17.
 */
class InterestRequester : BaseRequester {

    private var presenter: Any? = null
    private var listener: DefaultInterestListener? = null
    private var parentId: Int? = null

    private var childlistener: DefaultChildInterestListener? = null

    constructor(presenter: Any?, listener: DefaultInterestListener?) : super() {
        this.presenter = presenter
        this.listener = listener
    }

    constructor(parentId: Int, presenter: Any?, childlistener: DefaultChildInterestListener?) : super() {
        this.presenter = presenter
        this.childlistener = childlistener
        this.parentId = parentId
    }

    fun execute() {
        doAsync {
            //run in background
            var mentorzApiResponse: MentorzApiResponse? = null
            if (parentId != null) {
                mentorzApiResponse = HttpController.getInterestByParent(parentId!!)
                if (!isSessionExpired(childlistener!!, mentorzApiResponse!!)) {
                    callBack(mentorzApiResponse)
                }

            } else {
                mentorzApiResponse = HttpController.getRootInterest()
                if (!isSessionExpired(listener!!, mentorzApiResponse!!)) {
                    callBack(mentorzApiResponse)
                }
            }


        }
    }

    fun callBack(mentorzApiResponse: MentorzApiResponse?) {

        var interestResponse: InterestResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            interestResponse = MentorzApplication.instance?.gson?.fromJson<InterestResponse>(mentorzApiResponse.responseBody as String, InterestResponse::class.java)
            if (interestResponse != null) {
                if (parentId != null) {
                    childlistener?.onDefaultChildInterestSuccess(interestResponse)
                } else {
                    listener?.onDefaultInterestSuccess(interestResponse)

                }
            }

        }
        else if(mentorzApiResponse?.statusCode==0) {
            listener?.onNetworkFail()
        }
        else {
            if (parentId != null) {
                childlistener?.onDefaultChildInterestFail()
            } else {
                listener?.onDefaultInterestFail()

            }
        }
    }

}