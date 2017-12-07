package com.mentorz.utils

import android.os.Handler
import android.os.Looper
import com.mentorz.R.string.post
import android.os.Looper.getMainLooper
import android.util.Log
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.uploadfile.FileType
import com.mentorz.uploadfile.FileUploadListener
import okhttp3.MediaType
import okio.BufferedSink
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection


/**
 * Created by craterzone on 10/26/2017.
 */
class ProgressRequestBody(val listener: FileUploadListener,  val mFile: File,val fileType: String,val token:String) : RequestBody(), SignedUrlListener {
    override fun onSessionExpired() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        listener.fileUploadSuccess(url)
    }
    private val mPath: String? = null

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    override fun contentType(): MediaType? {
        // i want to upload only images
        return MediaType.parse("image/*")
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val input = FileInputStream(mFile)
        var uploaded: Long = 0

        try {
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (( input.read(buffer)) != -1) {
                read =( input.read(buffer))


                listener.publishProgress((100 * read / fileLength).toInt(),fileType)
                Log.d("ProgressRequestBody",""+(100 * read / fileLength).toInt())

           // uploaded =+read as Long
            sink.write(buffer,0,read)

            }

           // val statusCode = conn.getResponseCode()
            if(fileType!= FileType.VIDEO) {
                mFile.delete()
            }
            //if (statusCode == HttpURLConnection.HTTP_OK) {
                listener.fileUploadSuccess(token, fileType)
                SignedUrlRequester(FileType.FILE,this,null,token).execute()
           // } else {

            //}
            input.close()
        }catch (exception :Exception){
            listener.fileUploadFail()
            Log.e("ProgressRequestBody",""+exception.toString())
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Int, private val mTotal: Long) : Runnable {

        override fun run() {
           // listener.onProgressUpdate((100 * mUploaded / mTotal).toInt())
            listener.publishProgress((100 * mUploaded / mTotal).toInt(),fileType)
        }
    }

    companion object {

        private val DEFAULT_BUFFER_SIZE = 1024*4
    }
}