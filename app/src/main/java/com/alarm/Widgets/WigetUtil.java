package com.alarm.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.alarm.BackGroundTasks.TimeChangedWorker;

import java.util.concurrent.TimeUnit;

import static com.alarm.BackGroundTasks.TimeChangedWorker.TimeChangedWorkerTag;
import static com.alarm.Widgets.AppWidget.UPDATE_WIDGET_KEY;
import static com.alarm.Widgets.AppWidget.UPDATE_WIDGET_VALUE;


public class WigetUtil {

    private static final int WIDGET_ALARM_ID = 0;
    private static final int WIDGET_INTERVAL_MILLIS = 120000;  //20 minutes update period-->


    //called at
    // enable widget
    // and in BootAndTimeChangeReceiver at time change and boot complete
    public static void widget_startBackGroundUpdate(Context context)
    {
        //method 1 alarmmanager
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MILLISECOND, WIDGET_INTERVAL_MILLIS);
//
//        Intent alarmIntent = new Intent(AppWidget.ACTION_AUTO_UPDATE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, WIDGET_ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        // RTC does not wake the device up
//        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), WIDGET_INTERVAL_MILLIS, pendingIntent);

        //method 2
        PeriodicWorkRequest timeChangedWorkerRequest = new PeriodicWorkRequest.Builder(TimeChangedWorker.class, 15, TimeUnit.MINUTES)
                .addTag(TimeChangedWorkerTag)
                .build();
        WorkManager.getInstance().enqueue(timeChangedWorkerRequest);
    }


    public static  void widget_stopBackGroundUpdate(Context context)
    {
        //method 1 alarmmanager
//        Intent alarmIntent = new Intent(AppWidget.ACTION_AUTO_UPDATE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, WIDGET_ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pendingIntent);
        //method 2

       // WorkManager.getInstance().cancelAllWorkByTag(TimeChangedWorkerTag);
    }

    //called at
    //  scheduleNextAlarm
    //  and in TimeChangedWorker periodic task
    // and in BootAndTimeChangeReceiver at time change and boot complete
    public static void UpdateWidget(Context  context)
    {
        Log.e("UpdateWidget package",context.getPackageName());


       // ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),AppWidget.class.getName());
        ComponentName thisAppWidgetComponentName = new ComponentName(context,AppWidget.class.getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
     int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);

        //method 1 correct
        Intent intent = new Intent(context, AppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        //pass data in intent and recive in onrecive
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
        intent.putExtra(UPDATE_WIDGET_KEY,UPDATE_WIDGET_VALUE);
         intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);

        //method 2 correct

//         RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//          remoteViews =   getUpdatedRemoteView(context,remoteViews,false,null);
//        appWidgetManager.updateAppWidget(thisAppWidgetComponentName, remoteViews);


        //method 3
        //In case you are working with a widget that uses a collection such as ListView, GridView, or StackView, to update the widget's items, do as follow:
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view);


    }



}
