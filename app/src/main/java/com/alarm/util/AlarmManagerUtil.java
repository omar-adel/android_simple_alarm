package com.alarm.util;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.alarm.Widgets.WigetUtil;
import com.alarm.model.Prefs;
import com.alarm.model.sqlite.Alarm;
import com.alarm.model.sqlite.AlarmDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.receivers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

public class AlarmManagerUtil {

    public static final String ALARM_ACTION_OBJECT = "ALARM_ACTION_OBJECT";


    public static final String Alarm_WIDGET_DATE_FORMAT = "EEE dd MMM hh:mm a";

    public static final String ALARM_SCHEDULED_ALARM_TIME = "ALARM_SCHEDULED_ALARM_TIME";//FOR USE IN WIDGET
    public static final String ALARM_TO_FIRE = "ALARM_TO_FIRE";
    public static long ALARM_INTERVAL_DAY = 24 * 60 * 60 * 1000;
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm", Locale.getDefault());
    private static final SimpleDateFormat AM_PM_FORMAT =
            new SimpleDateFormat("a", Locale.getDefault());

    private static final int REQUEST_ALARM = 1;
    private static final String[] PERMISSIONS_ALARM = {
            Manifest.permission.VIBRATE
    };

    public static void scheduleAlarm(Context context, PendingIntent pendingIntent, long date) {



        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (null != alarmMgr) {
            if (Build.VERSION.SDK_INT >= 23) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmMgr.setExact(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            } else {
                alarmMgr.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
        }
    }



    //scheduleNextAlarm called when insert , update , delete alarm and also in alarmreciver and also in update from background using workmanager and
    //in handleBootComplete and in handleTimeChange
    public static void scheduleNextAlarm(final Context context) {
        getAlarmsFromDb(context, new OnGetAlarmsFromDb() {
            @Override
            public void onGetAlarmsFromDb(List<Alarm> alarms) {
                onAlarmsChangedNextAlarm(context, (ArrayList<Alarm>) alarms);
            }
        });

    }
    private static void onAlarmsChangedNextAlarm(final Context context , ArrayList<Alarm> alarms) {
        refreshAlarmTimes(context,alarms);

        getAlarmsFromDb(context, new OnGetAlarmsFromDb() {
            @Override
            public void onGetAlarmsFromDb(List<Alarm> alarms) {
                if (alarms.size() > 0) {
                    Alarm nearestAlarm = getNearestAlarmLoc((ArrayList<Alarm>) alarms);
                    if (nearestAlarm != null) {
                        PendingIntent pIntent = getPendingIntent(context, nearestAlarm);
                        Prefs.putLong(ALARM_SCHEDULED_ALARM_TIME,nearestAlarm.getTime());
                        scheduleAlarm(context, pIntent, nearestAlarm.getTime());
                    }
                  else   {
                        Prefs.putLong(ALARM_SCHEDULED_ALARM_TIME,0);
                    }

                    WigetUtil.UpdateWidget(context);

                }
            }
        });

    }


    public static void cancelThisAlarm(Context context, Alarm alarm) {
        PendingIntent pIntent = getPendingIntent(context, alarm);
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pIntent);
    }

    public static void handleBootComplete(Context context) {
        scheduleNextAlarm(context);

    }

    public static void handleTimeChange(Context context) {

        scheduleNextAlarm(context);
    }

    private static PendingIntent getPendingIntent(Context context, Alarm alarm) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        byte[] bytes = ParcelableUtil.marshall(alarm);
        intent.putExtra(ALARM_TO_FIRE, bytes);
        final PendingIntent pIntent = PendingIntent.getBroadcast(
                context,
                getNotificationId(alarm),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        return pIntent;
    }

    private static   Alarm   getNearestAlarmLoc(ArrayList<Alarm>alarms) {
        Alarm alarmNear = getNearestAlarm( alarms , Calendar.getInstance().getTimeInMillis());
        return alarmNear ;
    }

    private static Alarm getNearestAlarm(ArrayList<Alarm> arrayList, long comparingTime) {
        Alarm nearestAlarm = null;
        long minDiff = -1;
        long currDiff=-1;
        long targetTS = comparingTime;
        for (int i = 0; i < arrayList.size(); i++) {
            Alarm alarm = arrayList.get(i);
            if(isAlarmActiveInAnyDay(alarm))
            {
                //280971594
                alarm =  getNearestActiveAlarmDay(alarm);
                if(alarm!=null)
                {
                    currDiff = Math.abs(alarm.getTime() - targetTS);
                    if (minDiff == -1 || currDiff < minDiff) {
                        if(Long.valueOf(alarm.getTime()).compareTo(Long.valueOf(Calendar.getInstance().getTimeInMillis()))>0)
                        {

                            minDiff = currDiff;
                            nearestAlarm = alarm ;
                        }
                    }
                }

            }

        }
         return nearestAlarm;
    }

    private static  Alarm getNearestActiveAlarmDay(Alarm alarm) {
         Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);

        for ( int i = 0; i < 7; i++ ) {
            calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DATE,i);
              if ( isAlarmActiveInTime(alarm,calendar.getTimeInMillis()))
            {
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                calendar.set(Calendar.MINUTE, alarm.getMinute());
                 alarm.setTime(calendar.getTimeInMillis());
                return alarm ;
            }
        }
        return null ;
     }

    private static void refreshAlarmTimes(Context context , ArrayList<Alarm> alarms) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);
            currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.SECOND, alarm.getSecond());
            currentCalendar.set(Calendar.MILLISECOND, 0);
            if (alarm.getTime() <= currentCalendar.getTimeInMillis()) {
                //in case of user has changes device date to date after
                currentCalendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                currentCalendar.set(Calendar.MINUTE, alarm.getMinute());

                int iDiff = 1;
                while ((alarm.getTime() + (iDiff * ALARM_INTERVAL_DAY)) <= currentCalendar.getTimeInMillis()) {
                    iDiff++;
                }

                currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.SECOND, alarm.getSecond());
                currentCalendar.set(Calendar.MILLISECOND, 0);

                Alarm tempAlarm = new Alarm(alarm.getHour(), alarm.getMinute(), alarm.getSecond());
                if (tempAlarm.getTime() >= currentCalendar.getTimeInMillis()) {
                    alarm.setTime(alarm.getTime() + ((iDiff - 1) * ALARM_INTERVAL_DAY));
                 } else {
                    alarm.setTime(alarm.getTime() + (iDiff * ALARM_INTERVAL_DAY));
                 }
                //alarms.set(i, alarm);
                updateAlarm(context,alarm);
            } else {

                currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.SECOND, alarm.getSecond());
                currentCalendar.set(Calendar.MILLISECOND, 0);

                Alarm tempAlarm = new Alarm(alarm.getHour(), alarm.getMinute(), alarm.getSecond());
                if (tempAlarm.getTime() == currentCalendar.getTimeInMillis()) {
                  //  alarms.set(i, tempAlarm.addDayToAlarm());
                    updateAlarm(context,tempAlarm.addDayToAlarm());
                }

                else   if (tempAlarm.getTime() > currentCalendar.getTimeInMillis()) {
                    //in case of user has changes device date to date before
                    if ((alarm.getHour() == 0) && (alarm.getMinute() == 0))
                    {
                       // alarms.set(i, tempAlarm.addDayToAlarm());
                        updateAlarm(context,tempAlarm.addDayToAlarm());
                    }
                    else
                    {
                       // alarms.set(i, tempAlarm);
                        updateAlarm(context,tempAlarm);
                    }
                 } else {
                   // alarms.set(i, tempAlarm.addDayToAlarm());
                    updateAlarm(context,tempAlarm.addDayToAlarm());
                 }

            }
        }
    }

    private static void updateAlarm(Context context , Alarm alarm) {
       AlarmDatabase.getInstance(context).alarmDao().update(alarm);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                  AlarmDatabase.getInstance(context).alarmDao().update(alarm);
//            }
//        }).start();
    }


    private   void   sortAlarms( final  Context context  ) {
        getAlarmsFromDb(context, new OnGetAlarmsFromDb() {
            @Override
            public void onGetAlarmsFromDb(List<Alarm> alarms) {
                Collections.sort(alarms, new Alarm.AlarmTimeComparator());
                Collections.reverse(alarms);
            }
        });

    }

    private static void getAlarmsFromDb(final  Context context , final OnGetAlarmsFromDb onGetAlarmsFromDb )
    {
        //async method
//        ArrayList<Alarm> alarms = new ArrayList<>();
//        LiveData<List<Alarm>>  alarmsLiveData    = AlarmDatabase.getInstance(context).alarmDao().getAll() ;
//        Observer observer = new Observer<List<Alarm>>() {
//            @Override
//            public void onChanged(@Nullable List<Alarm> alarmsLoc) {
//                Log.e("cds","cds");
//                alarms.clear();
//                for (int i = 0; i < alarmsLoc.size(); i++) {
//                    alarms.add(alarmsLoc.get(i));
//                }
//                alarmsLiveData.removeObserver(this);
//                onGetAlarmsFromDb.onGetAlarmsFromDb(alarmsLoc);
//             }
//        };
//
//        alarmsLiveData.observeForever(observer);
//

        //sync method
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Alarm> alarms  = (ArrayList<Alarm>) AlarmDatabase.getInstance(context).alarmDao().getAllSync();
                onGetAlarmsFromDb.onGetAlarmsFromDb(alarms);
            }
        }).start();
    }

    public static String getNextAlarmTime(Context context)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Alarm_WIDGET_DATE_FORMAT);
        try {
            String nextAlarmTime = simpleDateFormat.format(Prefs.getLong(ALARM_SCHEDULED_ALARM_TIME,0));
            return nextAlarmTime;
        }
        catch (Exception e)
        {
         return context.getString(R.string.no_alarm_scheduled);
        }

    }
    public static String getTimeToNextAlarm(Context context)
    {
        try {
            long  nextAlarmTime = Prefs.getLong(ALARM_SCHEDULED_ALARM_TIME,0);
            long timeToNextAlarmTime=nextAlarmTime-System.currentTimeMillis();

            int seconds = (int) (timeToNextAlarmTime / 1000) % 60 ;
            int minutes = (int) ((timeToNextAlarmTime / (1000*60)) % 60);
            int hours   = (int) ((timeToNextAlarmTime / (1000*60*60)) % 24);
            return hours + " hours and " + minutes + " minutes and "+""+ seconds + " seconds";
        }
        catch (Exception e)
        {
            return context.getString(R.string.no_alarm_scheduled);
        }

    }
    public   interface OnGetAlarmsFromDb {
        public void onGetAlarmsFromDb(List<Alarm> alarms ) ;
    }

    public static void checkAlarmPermissions(Activity activity) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        final int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.VIBRATE
        );

        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_ALARM,
                    REQUEST_ALARM
            );
        }

    }


    public static String getReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String getAmPm(long time) {
        return AM_PM_FORMAT.format(time);
    }


    public static boolean isAlarmActiveInCurrentTime(Alarm alarm) {
       return isAlarmActiveInTime(alarm,Calendar.getInstance().getTimeInMillis());
    }
    public static boolean isAlarmActiveInTime(Alarm alarm,long time) {

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int calendarDay = calendar.get(Calendar.DAY_OF_WEEK);

        ArrayList<Integer>days=Alarm.convertDaysToArr(alarm.getDays());
        switch (calendarDay)
        {
            case Calendar.MONDAY:
                if(days.get(0)==1)
                {
                    return true ;
                }
                break;
            case Calendar.TUESDAY:
                if(days.get(1)==1)
                {
                    return true ;
                }
                break;
            case Calendar.WEDNESDAY:
                if(days.get(2)==1)
                {
                    return true ;
                }
                break;
            case Calendar.THURSDAY:
                if(days.get(3)==1)
                {
                    return true ;
                }
                break;
            case Calendar.FRIDAY:
                if(days.get(4)==1)
                {
                    return true ;
                }
                break;
            case Calendar.SATURDAY:
                if(days.get(5)==1)
                {
                    return true ;
                }
                break;
            case Calendar.SUNDAY:
                if(days.get(6)==1)
                {
                    return true ;
                }
                break;
        }
        return false ;
    }

    public static boolean isAlarmActiveInAnyDay(Alarm alarm) {

          ArrayList<Integer>days=Alarm.convertDaysToArr(alarm.getDays());
                 if(days.contains(1))
                {
                    return true ;
                }
                 return false;
    }

    public static int getNotificationId(Alarm alarm) {
        final long id = alarm.getId();
        return (int) (id^(id>>>32));
    }


}
