package com.mentorz.requester

import com.mentorz.MentorzApplication
import com.mentorz.model.UrlResponse
import com.mentorz.retrofit.HttpController
import com.mentorz.retrofit.MentorzApiResponse
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType
import com.mentorz.utils.Prefs
import org.jetbrains.anko.doAsync
import org.jetbrains.annotations.Nullable
import java.net.HttpURLConnection

/**
 * Created by craterzone on 27/07/17.
 */
class SignedUrlRequester(private var fileType: String, @Nullable private var listener: SignedUrlListener?, @Nullable private var model: Any?, private var token: String?) : BaseRequester() {

    fun execute() {
        doAsync {
            //run in background
            val mentorzApiResponse: MentorzApiResponse? = HttpController.getSignedUrl(token)
            callBack(mentorzApiResponse)
        }
    }

    private fun callBack(mentorzApiResponse: MentorzApiResponse?) {
        var response: UrlResponse? = null
        if (mentorzApiResponse?.statusCode == HttpURLConnection.HTTP_OK) {
            response = MentorzApplication.instance?.gson?.fromJson<UrlResponse>(mentorzApiResponse.responseBody as String, UrlResponse::class.java)
            if (response != null) {
                when (fileType) {
                    FileType.PROFILE_PICTURE -> {
                        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_PICTURE_LRES, response.value)
                        listener?.signedUrlSuccess(response.value!!, model)
                    }
                    FileType.PROFILE_VIDEO_THUMBNAIL -> {
                        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_LRES, response.value)
                        listener?.signedUrlSuccess(response.value!!, model)
                    }
                    FileType.PROFILE_VIDEO -> {
                        MentorzApplication.instance?.prefs?.putString(Prefs.Key.PROFILE_VIDEO_HRES, response.value)
                        listener?.signedUrlSuccess(response.value!!, model)
                    }
                    FileType.FILE -> {
                        listener?.signedUrlSuccess(response.value!!, model)
                    }
                    FileType.VIDEO -> {
                        listener?.signedUrlSuccess(fileType,response.value!!, model)
                    }
                }
            }
            return
        } else {
            listener?.signedUrlFail()
        }
    }
}

