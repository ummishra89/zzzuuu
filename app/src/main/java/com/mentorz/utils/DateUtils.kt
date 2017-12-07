package com.mentorz.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by craterzone on 10/07/17.
 */
class DateUtils {
    companion object {
        private val SECONDS_IN_DAY: Long = 86400
        private val SECONDS_IN_HOUR: Int = 3600
        private val SECONDS_IN_MONTH: Long = 2592000
        private val SECONDS_IN_WEEK: Long = 604800
        private val SECONDS_IN_YEAR: Long = 30672000
        private val SECONDS_IN_MINUTES: Int = 60

        fun getDurationFromDate(timestampInMilliSec: Long): String {
            val currentTimestamp = Date().time
            val prevTimestamp = timestampInMilliSec
            val diff = (currentTimestamp - prevTimestamp) / 1000
            val years = diff / SECONDS_IN_YEAR
            val remainingTimeAfterYears = diff % SECONDS_IN_YEAR
            if (years > 0) {
                return "${years}y ago"
            }
            val months = remainingTimeAfterYears / SECONDS_IN_MONTH
            val remainingTimeAfterMonths = remainingTimeAfterYears % SECONDS_IN_MONTH

            if (months > 0) {
                return "${months}m ago"
            }

            val weeks = remainingTimeAfterMonths / SECONDS_IN_WEEK
            val remainingTimeAfterWeek = remainingTimeAfterMonths % SECONDS_IN_WEEK

            if (weeks > 0) {
                return "${weeks}w ago"
            }
            val days = remainingTimeAfterWeek / SECONDS_IN_DAY
            val remainingTimeAfterDays = remainingTimeAfterWeek % SECONDS_IN_DAY

            if (days > 0) {
                return "${days}d ago"
            }
            val hours = remainingTimeAfterDays / SECONDS_IN_HOUR
            val remainingTimeAfterHours = remainingTimeAfterDays % SECONDS_IN_HOUR

            if (hours > 0) {
                return "${hours}hr ago"
            }
            val minutes = remainingTimeAfterHours / SECONDS_IN_MINUTES

            if (minutes > 0) {
                return "$minutes min ago"
            }
            return "Just now..."
        }
         fun getTimeStamp(): String {

            val timestamp = Date().time
            val cal = Calendar.getInstance()
             cal.timeInMillis = timestamp
            val timeString = SimpleDateFormat("HH_mm_ss_SSS").format(cal.time)
            return timeString
        }
        fun getCurrentTimeStamp(): Long {
            return Date().time
        }

        fun changeTimeStampFormatForIOS(timeStemp : String?):String?{
            var time :String ? = timeStemp
            if(timeStemp?.length==13){
                time =timeStemp?.substring(0,10)+"."+timeStemp?.substring(10,timeStemp.length)+"000"
            }
            return time
        }

        fun changeTimeStampInto17DigitLongValue(time: String?):Long {
            var timeString = ""
            if(time?.length==15){
                timeString=time.substring(0,10)+time.substring(11,time.length)+"0"
            }
            else if(time?.length==13){
                timeString=time+"0000"
            }
            return java.lang.Long.parseLong(timeString)
        }
        fun getCurrentTimestampInLong(): Long {
            return Calendar.getInstance(TimeZone.getTimeZone("utc")).getTimeInMillis()
        }
    }
}