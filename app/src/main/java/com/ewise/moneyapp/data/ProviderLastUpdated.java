package com.ewise.moneyapp.data;

import android.content.Context;

import com.ewise.android.pdv.api.util.DateTimeUtil;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiResults;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 1/5/17.
 */
public class ProviderLastUpdated extends Object {

    private String  instId;

    private long    lastUpdatedTimestamp;

    private static long SECONDS_IN_MINUTE=60;
    private static long SECONDS_IN_HOUR=3600;
    private static long SECONDS_IN_DAY=86400;
    private static long SECONDS_IN_WEEK=604800;
    private static long SECONDS_IN_MONTH=2592000;
    private static long SECONDS_IN_YEAR=31104000;

    public ProviderLastUpdated(String instId, long lastUpdatedTimestamp) {
        this.instId=instId;
        this.lastUpdatedTimestamp=lastUpdatedTimestamp;
    }

    public String getTimeElapsedText(Context context) {

        String getTimeElapsedText="";
        Date now = new Date();
        long timeElapsed = 0;
        long divisor = 0;
        String timeText="just now";

        long elapsedSeconds = TimeUnit.SECONDS.toSeconds(now.getTime() - lastUpdatedTimestamp);

        if (elapsedSeconds > SECONDS_IN_YEAR)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_YEAR);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_year_plural):context.getString(R.string.last_updated_time_elapsed_year);
        }
        else if (elapsedSeconds > SECONDS_IN_MONTH)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_MONTH);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_months_plural):context.getString(R.string.last_updated_time_elapsed_month);
        }
        else if (elapsedSeconds > SECONDS_IN_WEEK)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_WEEK);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_weeks_plural):context.getString(R.string.last_updated_time_elapsed_week);
        }
        else if (elapsedSeconds > SECONDS_IN_DAY)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_DAY);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_days_plural):context.getString(R.string.last_updated_time_elapsed_day);
        }
        else if (elapsedSeconds > SECONDS_IN_HOUR)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_HOUR);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_hours_plural):context.getString(R.string.last_updated_time_elapsed_hour);
        }
        else if (elapsedSeconds > SECONDS_IN_MINUTE)
        {
            timeElapsed=Math.abs(elapsedSeconds/SECONDS_IN_MINUTE);
            timeText=Math.abs(timeElapsed)>1?context.getString(R.string.last_updated_time_elapsed_minutes_plural):context.getString(R.string.last_updated_time_elapsed_minute);
        }
        else
        {
            timeElapsed=Math.abs(elapsedSeconds);
            if (elapsedSeconds>=10) {
                timeText = Math.abs(timeElapsed) > 1 ? context.getString(R.string.last_updated_time_elapsed_seconds_plural) : context.getString(R.string.last_updated_time_elapsed_second);

            }
        }

        if (elapsedSeconds>=10){ //if less than 10 seconds then just now is good enough
            getTimeElapsedText=String.format(context.getString(R.string.last_updated_time_elapsed_format), timeElapsed, timeText);
        }

        return getTimeElapsedText;

    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }


    public long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    @Override
    public String toString(){
        return PdvApiResults.toJsonString(this);
    }

}
