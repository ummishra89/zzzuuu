package com.craterzone.logginglib.executer.requester;

import android.util.Log;

import com.craterzone.logginglib.listener.SendFileListener;
import com.craterzone.logginglib.model.DiagnosticResponse;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by NicoDart on 24/1/16.
 */
public class SendFileRequester implements Runnable {

    private String zipFilePath;
    private String TAG = getClass().getSimpleName();
    private String url;
    private String accessToken;
    private SendFileListener sendFileListener;
    public SendFileRequester(String url, String accessToken, String zipFilePath, SendFileListener sendFileListener) {
        this.zipFilePath = zipFilePath;
        this.url=url;
        this.accessToken=accessToken;
        this.sendFileListener=sendFileListener;
    }

    @Override
    public void run() {
        requestService();
    }

    private void requestService() {
        sendFileListener.onSendFileSuccess(null);
       /* int responseCode;
        Log.d(TAG, "zip file path:" + zipFilePath);
        DiagnosticResponse response = postMultiPartFileUpload(url,accessToken,zipFilePath);
        responseCode = response.getStatusCode();
        Log.d(TAG, "response code:" + response.getStatusCode() + ",response message:" + response.getResponse());


            if (responseCode == 200) {
               sendFileListener.onSendFileSuccess(response);
            }
             else {
                sendFileListener.onSendFileError(response);
            }*/

    }
    public static DiagnosticResponse postMultiPartFileUpload(String urlString, String authorization, String filePath) {

        String TAG = "RestServiceUtils";
        HttpURLConnection conn;
        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(filePath);
        try {
            URL url = new URL(urlString);

            conn = (HttpsURLConnection) url.openConnection();
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", authorization);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("file", filePath);
            try {
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + filePath + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.getMessage(), e);
                return new DiagnosticResponse(0, "");
            }
            int statusCode = conn.getResponseCode();
            Log.d(TAG, "Status is" + statusCode);
            switch (statusCode) {
                case 200:
                    return new DiagnosticResponse(statusCode, "file upload successfully");
                default:
                    return new DiagnosticResponse(statusCode, "");
            }
        } catch (MalformedURLException ex) {
            Log.e(TAG, "error: " + ex.getMessage(), ex);
            return new DiagnosticResponse(0, "");
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage(), e);
            return new DiagnosticResponse(0, "");
        }
    }
}
