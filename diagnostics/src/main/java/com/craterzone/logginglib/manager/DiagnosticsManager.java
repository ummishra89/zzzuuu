package com.craterzone.logginglib.manager;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v7.appcompat.BuildConfig;
import android.util.Log;

import com.craterzone.logginglib.executer.BackgroundExecutor;
import com.craterzone.logginglib.executer.requester.SendFileRequester;
import com.craterzone.logginglib.listener.SendFileListener;
import com.craterzone.logginglib.model.DiagnosticResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by NicoDart on 13/1/16.
 */
public class DiagnosticsManager implements SendFileListener {

    private static DiagnosticsManager _instance;
    private String TAG = getClass().getSimpleName();
    private File zipFile = null;
    private File infoTextFile = null;
    private Context context;
    private DiagnosticsManager(Context context) {
       this.context=context;
    }

    public static DiagnosticsManager getInstance(Context context) {
        _instance = new DiagnosticsManager(context);
        return _instance;
    }

    public void sendDiagnostics(String url,String accessToken) {

        ArrayList<String> directories = new ArrayList<>();
        File logFolder = new File(LoggerManager.logFilePath());
        File[] directoryListing = logFolder.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                String filePath = child.getAbsolutePath();
                if (filePath.endsWith(".lck")) {
                   /* Logger.v(TAG, "deleting locked file: " + filePath);
                    child.delete();*/
                } else {
                    directories.add(filePath);
                }

            }
        }
        infoTextFile = new File(context.getFilesDir(), "DeviceInfo.txt");

        if (!infoTextFile.exists()) {
            try {
                infoTextFile.createNewFile();

            } catch (IOException e) {
                Log.e(TAG, "Error in creating infoTextFile", e);
            }
        }

        createInfoTextFile(infoTextFile.getAbsolutePath());
        directories.add(infoTextFile.getAbsolutePath());

        zipFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "_logs.zip");

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error in creating zipFile", e);
            }
        }

        makeZipFile(zipFile.getAbsolutePath(), directories);
        Log.d(TAG, "Zip file path :" + zipFile.getPath());


        BackgroundExecutor.getInstance().execute(new SendFileRequester(url,accessToken,zipFile.getAbsolutePath(),this));
    }




    private void makeZipFile(String zipFilePath, ArrayList<String> directories) {

        final int BUFFER = 2048;
        FileOutputStream fileOutputStream;
        ZipOutputStream zipOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipFilePath);
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

        } catch (FileNotFoundException e) {
            Log.d(TAG, "exception:" + e.getMessage());

        }
        try {
            for (String file : directories) {

                File sourceFile = new File(file);
                BufferedInputStream origin;

                if (sourceFile.isDirectory() && sourceFile.list().length > 0) {
                    zipSubFolder(zipOutputStream, sourceFile, sourceFile.getParent().length());
                } else {
                    Log.d(TAG, "file size:" + sourceFile.length() + ", file name:" + sourceFile.getName());
                    byte data[] = new byte[BUFFER];
                    FileInputStream fileInputStream = new FileInputStream(file);
                    origin = new BufferedInputStream(fileInputStream, BUFFER);
                    ZipEntry zipEntry = new ZipEntry(getLastPathComponent(file));
                    zipOutputStream.putNextEntry(zipEntry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        zipOutputStream.write(data, 0, count);
                    }
                }
                zipOutputStream.closeEntry();


            }
        } catch (Exception e) {
            Log.e(TAG, "Error in creating zipFile", e);

        } finally {
            try {
                zipOutputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception:" + e.getMessage());
            }
        }
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin;
        for (File file : fileList) {
            if (file.isDirectory() && file.list().length > 0) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath.substring(basePathLength);
                Log.i("ZIP SUBFOLDER", "Relative Path : " + relativePath);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     */
    private String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        return segments[segments.length - 1];
    }

    private void createInfoTextFile(String infoTextFilePath) {
        StringBuilder textFileContents = new StringBuilder();
        String newLine = "\r\n";

        Date gmtDate = new Date();
        textFileContents.append("GMT Date/Time: ").append(gmtDate).append("\r\n");
        TimeZone localTimeZone = TimeZone.getDefault();
        TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        long interval = localTimeZone.getOffset(gmtDate.getTime()) - gmtTimeZone.getOffset(gmtDate.getTime());
        Date localDate = new Date(gmtDate.getTime() - interval);
        textFileContents.append(String.format("Current Time Zone: %s, offset: %.2f, dst: %b, dstSaving: %.1f%s",
                localTimeZone.getID(), localTimeZone.getRawOffset() / ((float) (60 * 60 * 1000)), localTimeZone.useDaylightTime(), localTimeZone.getDSTSavings() / ((float) (60 * 60 * 1000)), newLine));
        textFileContents.append("Local Date/Time: ").append(localDate).append(newLine);
        textFileContents.append("OS Version: ").append(Build.VERSION.RELEASE).append(" (SDK: ").append(Build.VERSION.SDK_INT).append(")").append(newLine);
        textFileContents.append("Manufacturer: ").append(Build.MANUFACTURER).append(newLine);
        textFileContents.append("Model: ").append(Build.MODEL).append(newLine);
        textFileContents.append("Brand: ").append(Build.BRAND).append(newLine);
        textFileContents.append("App Version: ").append(BuildConfig.VERSION_NAME).append(newLine);

        /*
        writing notification settings.
        TODO
        */

        /*
        writing contents into infoTextFile
         */
        FileWriter fw = null;
        try {
            fw = new FileWriter(infoTextFilePath);
        } catch (IOException e) {
            Log.e(TAG, "Error in creating FileWriter object", e);
        }

        BufferedWriter bw = null;
        if (fw != null) {
            bw = new BufferedWriter(fw);
        }

        // write in file
        try {
            if (bw != null) {
                bw.write(textFileContents.toString());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error in writing contents into infoTextFile");
        }
        // close connection
        try {
            if (bw != null) {
                bw.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to close BufferReader");
        }
    }



    @Override
    public void onSendFileSuccess(DiagnosticResponse response) {

        File zipFile = new File(this.zipFile.getPath());
        File infoTextFile = new File(this.infoTextFile.getPath());
/*
        if (zipFile.exists()) {
            Log.v(TAG, "zip file deleted successfully after upload to server");
            zipFile.delete();
        }

        if (infoTextFile.exists()) {
            Log.v(TAG, "info text file deleted successfully after upload to server");
            infoTextFile.delete();
        }*/

    }

    @Override
    public void onSendFileError(DiagnosticResponse response) {


        File zipFile = new File(this.zipFile.getPath());
        File infoTextFile = new File(this.infoTextFile.getPath());

        if (zipFile.exists()) {
            Log.v(TAG, "zip file deleted successfully  but file doesn't upload to server");
            zipFile.delete();
        }

        if (infoTextFile.exists()) {
            Log.v(TAG, "info text file deleted successfully but file doesn't upload to server");
            infoTextFile.delete();
        }


    }



}
