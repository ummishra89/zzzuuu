package com.mentorz.retrofit

import okhttp3.Headers


/**
 * Created by craterzone on 06/07/17.
 */
data class MentorzApiResponse(var responseBody: Any?, val statusCode: Int, var header: Headers?) {
}