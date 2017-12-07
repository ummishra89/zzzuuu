package com.mentorz.retrofit

import android.util.Log
import android.widget.Toast
import com.mentorz.MentorzApplication
import okhttp3.ResponseBody
import retrofit2.Call

/**
 * Created by craterzone on 06/07/17.
 */
object ConnectionUtil {
    val TAG: String = "ConnectionUtil"
    fun execute(call: Call<ResponseBody>?): MentorzApiResponse? {
        try {
            val response = call?.execute()
            Log.d(TAG, "MentorzApiResponse Status Code:" + response?.code())
            Log.d(TAG, "MentorzApiResponse call request:" + call?.request())
            Log.d(TAG, "MentorzApiResponse request body:" + call?.request()?.body())
try {
    Log.d(TAG, "MentorzApiResponse request oath_token :" + call?.request()?.header("oauth-token"))
    Log.d(TAG, "MentorzApiResponse request user-agent :" + call?.request()?.header("user-agent"))
}catch (e:Exception){}

            val body: String? = response?.body()?.string()
            Log.d(TAG, "before decoding MentorzApiResponse Body:" + body)


            /*if (!TextUtils.isEmpty(body)) {
                body = EncodingDecoding.decodeString(body!!)
            }*/
            Log.d(TAG, "MentorzApiResponse Body:" + body)
            Log.d(TAG, "MentorzApiResponse Headers:" + response?.headers())
            if (response?.code() == 0) {
                Toast.makeText(MentorzApplication.applicationContext(), "Unable to connect to server. Please check your internet connection or try again later.", Toast.LENGTH_SHORT)
            }
            return MentorzApiResponse(body, response!!.code(), response.headers())
        } catch (ex: Exception) {
            Log.d(TAG, "Error in execute api" + ex.message)
        }
        return MentorzApiResponse(null, 0, null)
    }

}