package com.craterzone.logginglib.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.craterzone.logginglib.formatter.CustomLoggerFormatter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by craterzone on 4/9/16.
 */
public class LoggerManager {

    private Pattern outerPattern = Pattern.compile("\\[(.*?)\\]");
    private Pattern innerPattern = Pattern.compile("^\\s\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}(.\\d{3})");
    private static String TAG = LoggerManager.class.getSimpleName();
    private static final int FILE_SIZE = 5 * 1024 * 1000;
    private static final int TOTAL_FILE = 5;
    private static final String LOG_FOLDER = "mentorz";
    private FileHandler fileHandlerInXMLFormat = null;
    private Logger log = Logger.getLogger("");
    private static File logDir;
    Context mContext;
    private File zipFile;
    private static LoggerManager instance;

    public LoggerManager(Context context) {
        mContext = context;
    }

    public static LoggerManager getInstance(Context context) {
        if (instance == null) {
            instance = new LoggerManager(context);
        }
        return instance;
    }

    public void init() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        startLogging();
                    }
                }, 0, 1, TimeUnit.MINUTES);
    }

    public void startLogging() {
        try {
            //just for testing purpose

            logDir = new File(Environment.getExternalStorageDirectory(), LOG_FOLDER);
            //logDir = new File(mContext.getFilesDir(), LOG_FOLDER);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            if (!isWriteStorageAllowed(mContext)) {
                //Toast.makeText(mContext,"storage permission required to write logs",Toast.LENGTH_SHORT).show();
                return;
            }

            if (log.getHandlers().length > 1 && logDir.exists()) {
                readLogcat(fileHandlerInXMLFormat);
                return;
            }

            logDir = new File(Environment.getExternalStorageDirectory(), LOG_FOLDER);
            //logDir = new File(mContext.getFilesDir(), LOG_FOLDER);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            fileHandlerInXMLFormat = new FileHandler(logDir.getAbsolutePath() + "/logFile%g.txt", FILE_SIZE, TOTAL_FILE, true);
            fileHandlerInXMLFormat.setFormatter(new CustomLoggerFormatter());
            readLogcat(fileHandlerInXMLFormat);
            log.addHandler(fileHandlerInXMLFormat);


        } catch (IOException e) {

            Log.e(TAG, "FileHandler exception", e);
        }
    }

    public static String logFilePath() {
        return logDir.getPath();
    }

    private void readLogcat(FileHandler fileHandler) {
        try {
            //Dumps the entire logcat to std output.
            Process processD = Runtime.getRuntime().exec("logcat -v long -d");
            BufferedReader bufferedReaderD = new BufferedReader(new InputStreamReader(processD.getInputStream()));

            String line;
            String previousLine = null;
            // create objects here for efficiency sake
            Level level = Level.ALL;
            LogRecord logRecord = new LogRecord(level, "");

            while ((line = bufferedReaderD.readLine()) != null) {//Send to the file handler.
               /* if (line == null || line.length() == 0) {
                    // blank line - do nothing
                } else if (line.startsWith("---------") && previousLine == null) {
                    // do nothing
                } else if (previousLine != null) {
                    //UpdateLogRecord(logRecord, previousLine, line);*/
                logRecord.setMessage(line);
                fileHandler.publish(logRecord);
              /*      previousLine = null;
                } else {
                    previousLine = line;
                }*/
            }

            //Clear the logcat storage. Don't feel like rewriting old records.
            Runtime.getRuntime().exec("logcat -c");

        } catch (IOException e) {
            Log.e(TAG, "Could not get Logcat logs.", e);
        }
    }

    private void UpdateLogRecord(LogRecord logRecord, String firstLine, String secondLine) {

        Matcher matcher = outerPattern.matcher(firstLine);
        if (matcher.find()) {

            firstLine = firstLine.substring(firstLine.indexOf('[') + 1, firstLine.indexOf(']'));
            matcher = innerPattern.matcher(firstLine);
            if (matcher.find()) {
                logRecord.setLevel(getLevel(firstLine, matcher.end()));
                logRecord.setMillis(getMilliSeconds(firstLine, matcher.start(), matcher.end()));
                logRecord.setMessage(secondLine);
                logRecord.setThreadID(getThreadID(firstLine, matcher.end()));
                logRecord.setLoggerName(getLoggerName(firstLine, matcher.end()));

            }
        }
    }

    private int getThreadID(String string, int start) {
        string = string.substring(start).split("/")[0];
        String s[] = string.split(":");
        return Integer.parseInt(s[1].substring(0, s[1].length() - 2).trim());
    }

    private long getMilliSeconds(String string, int start, int end) {
        return getTimeStampFromDate(string.substring(start, end).trim());

    }

    private Level getLevel(String string, int start) {
        string = string.substring(start).split("/")[0];
        char logLevel = string.charAt(string.length() - 1);

        switch (logLevel) {
            case 'D':
                return Level.FINE;
            case 'E':
                return Level.SEVERE;
            case 'I':
                return Level.INFO;
            case 'V':
                return Level.FINER;
            case 'W':
                return Level.WARNING;
            case 'F':
                return Level.SEVERE;

        }
        return Level.ALL;

    }

    private String getLoggerName(String string, int start) {
        return string.substring(start).split("/")[1].trim();
    }

    public long getTimeStampFromDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {

            Date d = sdf.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + dateString);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public String getDiagnosticsFilePath() {

        if (!isWriteStorageAllowed(mContext)) {
            Toast.makeText(mContext,"storage permission required to get logs file",Toast.LENGTH_SHORT).show();
            return "";
        }

        ArrayList<String> directories = new ArrayList<>();

        File logFolder = new File(logDir.getPath());
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


        zipFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + "_logs.zip");

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error in creating zipFile", e);
            }
        }


        makeZipFile(zipFile.getAbsolutePath(), directories);

        return zipFile.getAbsolutePath();
    }

    public String getFilePathToSendDaignostic() {

        return zipFile.getAbsolutePath();
    }

    private String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        return segments[segments.length - 1];
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

    private boolean isWriteStorageAllowed(Context context) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

}
