package com.craterzone.logginglib.formatter;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by NicoDart on 2/21/2016.
 */
public class CustomLoggerFormatter extends Formatter {
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    @Override


    public String format(LogRecord record) {
        String string;
        string = String.format(" %s\n", record.getMessage());
        return string;

       /* StringBuilder sb = new StringBuilder();
                sb.append(String.format("<LogEvent logger=\"%s\" epochtime=\"%s\" level=\"%s\" thread=\"%d\" version=\"%s\"><message>%s</message><exception></exception></LogEvent>",
                                record.getLoggerName(), record.getMillis(), record.getLevel(), record.getThreadID(), BuildConfig.VERSION_NAME, record.getMessage(), record.getThrown()));
                return sb.toString();*/
    }
}
