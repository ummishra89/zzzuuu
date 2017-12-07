package com.mentorz.uploadfile

import android.util.Log

import com.mentorz.controller.FileUploadController
import com.mentorz.listener.CancelFileUploadListner
import com.mentorz.requester.SignedUrlRequester
import com.mentorz.requester.UploadSessionUrlRequester
import com.mentorz.retrofit.listeners.SignedUrlListener
import com.mentorz.retrofit.listeners.UploadSessionUrlListener
import com.mentorz.token.TokenGenerator
import com.mentorz.utils.ProgressRequestBody
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.*
import okhttp3.MultipartBody
import org.jetbrains.annotations.Nullable
import java.util.concurrent.Future


/**
 * Created by craterzone on 27/07/17.
 */
class FileUploadRequester(val listener: FileUploadListener, val filePath: String, val contentType: String, val fileType: String) : UploadSessionUrlListener, SignedUrlListener{

    constructor(listener: FileUploadListener,  filePath: String,  contentType: String, fileType: String, cancelFileUploadListner :CancelFileUploadListner) : this(listener,filePath,contentType,fileType){}




    override fun onSessionExpired() {

    }

    override fun signedUrlSuccess(url: String, model: Any?) {
        listener.fileUploadSuccess(url)
    }

    var urlString: String = ""
    var token: String = ""
    override fun uploadSessionUrlSuccess(url: String) {
        urlString = url
       /* doAsync {
            uploadTest(urlString)
        }*/
      //  FileUploadController.a = uploadFile()

        FileUploadController.unit = fileUploadUsingThread()



    }



    override fun uploadSessionUrlFail() {
        listener.fileUploadFail()
    }

    fun execute() {
        token = TokenGenerator.generateToken()
        UploadSessionUrlRequester(this, contentType, token).execute()
    }

    public fun  uploadFile(): Future<Unit> {

     val imageUploadThreas =  doAsync {
            var conn: HttpURLConnection? = null
            var dos: DataOutputStream? = null
            var bytesRead: Int
            var bytesAvailable: Int
            var bufferSize: Int
            val buffer: ByteArray
            val maxBufferSize = 1024 * 4;
            val sourceFile = File(filePath)
            val fileSize=sourceFile.length()

            try {
                val url = URL(urlString)
                conn = url.openConnection() as HttpsURLConnection

                 val fileInputStream = FileInputStream(sourceFile)
                conn.setRequestMethod("PUT")

                conn.setRequestProperty("Accept", "application/json")
                conn.setRequestProperty("Content-Type", contentType)
               // conn.readTimeout=1000
               // conn.connectTimeout=1000
                dos = DataOutputStream(conn.getOutputStream())

                bytesAvailable = fileInputStream.available()
                bufferSize = Math.min(bytesAvailable, maxBufferSize)
                buffer = kotlin.ByteArray(bufferSize)
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                var sentBytes:Long=0
                var progress:Int=0
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize)
                    dos.flush()
                    sentBytes = sentBytes.plus(bytesRead)
                    progress  = ((sentBytes * 100) / fileSize ).toInt()
                    Log.d("FileUploadRequester","progress:"+progress)
                   if (progress < 10){
                        listener.publishProgress(progress,fileType)
                    }
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }
                fileInputStream.close()
              //  dos.flush()
                dos.close()

                val statusCode = conn.getResponseCode()
                if(fileType!=FileType.VIDEO) {
                    sourceFile.delete()
                }
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    listener.fileUploadSuccess(token, fileType)
                    SignedUrlRequester(FileType.FILE,this@FileUploadRequester,null,token).execute()
                } else {
                    listener.fileUploadFail()
                }

            }  catch (e: Exception) {
                listener.fileUploadFail()
           Log.e( "FileUploadRequester" ,""+ e.message);
            }
        }

return imageUploadThreas
    }


    fun fileUploadUsingThread() : Thread{


        var thread = object : Thread() {
            override fun run() {
        var conn: HttpURLConnection? = null
        var dos: DataOutputStream? = null
        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize =1024* 1024 * 4;


        try {
            val sourceFile = File(filePath)

            val fileSize=sourceFile.length()
            val url = URL(urlString)
            conn = url.openConnection() as HttpsURLConnection

            val fileInputStream = FileInputStream(sourceFile)
            conn.setRequestMethod("PUT")

            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Content-Type", contentType)
            // conn.readTimeout=1000
            // conn.connectTimeout=1000
            conn.setChunkedStreamingMode(1024)
            dos = DataOutputStream(conn.getOutputStream())

            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            buffer = kotlin.ByteArray(bufferSize)
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
            var sentBytes:Long=0
            var progress:Int=0
            while (bytesRead > 0) {
                try {
                    dos.write(buffer, 0, bufferSize)
                    sentBytes = sentBytes.plus(bytesRead)
                    progress = ((sentBytes * 100) / fileSize).toInt()
                    Log.d("FileUploadRequester", "progress:" + progress)
                    if (progress < 10) {
                   //     listener.publishProgress(progress, fileType)
                    }

                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }catch (e:OutOfMemoryError){
                    Log.e("FileUploadRequester",""+e.toString())
                }
            }
            fileInputStream.close()
            dos.flush()
            dos.close()

            val statusCode = conn.getResponseCode()
            if(fileType!=FileType.VIDEO) {
                sourceFile.delete()
            }
            if (statusCode == HttpURLConnection.HTTP_OK) {
                listener.fileUploadSuccess(token, fileType)
                SignedUrlRequester(FileType.FILE,this@FileUploadRequester,null,token).execute()
            } else {
                listener.fileUploadFail()
            }
        }  catch (e: Exception) {
            listener.fileUploadFail()
            Log.e( "FileUploadRequester" ,""+ e.message);
        }
            }
        }
        thread.start()

      return thread

    }




}