package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.MentorBioResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection

/**
 * Created by craterzone on 28/07/17.
 */
class MentorBioRequester(private var mentorId: Long?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getMentorBio(mentorId)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var response: MentorBioResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<MentorBioResponse>(mentorzApiResponse.responseBody as String, MentorBioResponse::class.java)
            if (response != null) {
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.OCCUPATION, response.designation)
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.ORGANIZATION, response.organization)
                MentorzApplication.instance?.prefs?.putString(Prefs.Key.LOCATION, response.location)
                MentorzApplication.instance?.prefs?.putInt(Prefs.Key.EXPERIENCE, response.expYears)
            }
        }
    }


}