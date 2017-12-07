package com.craterzone.logginglib.formatter;

import com.craterzone.logginglib.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by NicoDart on 2/21/2016.
 */
public class TagCaptionLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        return new StringBuilder()
                .append(String.format("%s /%s : %s \n",
                        getDate(record.getMillis()),
                        record.getLoggerName(),
                        getCaption(record.getMessage())))
                .toString();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("utc"));
        cal.setTimeInMillis(time);
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTimeInMillis());
    }

    private CharSequence getCaption(String message) {
        int end = message.indexOf(BuildConfig.LOG_CAPTION_SEPERATOR);
        if(end < 0) return message;
        return message.subSequence(0, end);
    }
}
