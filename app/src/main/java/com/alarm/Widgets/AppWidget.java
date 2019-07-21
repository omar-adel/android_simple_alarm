/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alarm.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.alarm.util.AlarmManagerUtil;

import java.text.DateFormat;
import java.util.Date;

import omar.apps923.simple_alarm.R;

import static android.view.View.GONE;

/**
 * App widget provider class, to handle update broadcast intents and updates
 * for the app widget.
 */
public class AppWidget extends AppWidgetProvider {

    public static final String UPDATE_WIDGET_KEY = "UPDATE_WIDGET_KEY";
    public static final String UPDATE_WIDGET_VALUE = "UPDATE_WIDGET_VALUE";
     public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    @Override
    public void onEnabled(Context context)
    {
        // start alarm
        WigetUtil.widget_startBackGroundUpdate(context);
    }

    @Override
    public void onDisabled(Context context)
    {
        // stop alarm only if all widgets have been disabled
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        if (appWidgetIds.length == 0) {
            // stop alarm
           // WigetUtil.widget_stopBackGroundUpdate(context);
        }

    }

    /**
     * Override for onUpdate() method, to handle all widget update requests.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetIds     An array of the app widget IDs.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them.
        Log.e("widget onUpdate","widget onUpdate");
        Log.e("widget onUpdate package",context.getPackageName());


         for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,false,null);
        }
    }
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds,Object intentExtras) {
        // There may be multiple widgets active, so update all of them.
        Log.e("widget onUpdateintentExtras","widget onUpdate");
        Log.e("widget onUpdate packageintentExtras",context.getPackageName());

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,true,intentExtras);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("widget onReceive","widget onReceive");
        Log.e("widget onReceive package",context.getPackageName());
        Log.e("widget intent getAction",intent.getAction());
        if(intent.hasExtra(UPDATE_WIDGET_KEY))
        {
            Log.e("widget intent extras 1 update widget manual",intent.getStringExtra(UPDATE_WIDGET_KEY));

        }
        if ( (ACTION_AUTO_UPDATE.equals(intent.getAction())) ||(AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction()) ))
        {
            if(intent.hasExtra(UPDATE_WIDGET_KEY))
            {
                String intentExtrasStr=intent.getStringExtra(UPDATE_WIDGET_KEY);
                Object intentExtras=intentExtrasStr;
                Log.e("widget intent extras 1 update widget manual",intentExtras+"");
                onUpdate(context,intentExtras);

            }
            else
            {
                onUpdate(context);

            }
        }
        else super.onReceive(context, intent);
    }
    private void onUpdate(Context context,Object intentExtras) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds,intentExtras);
    }

    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisAppWidgetComponentName = new ComponentName(context.getPackageName(),getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }


    /**
     * Update a single app widget.  This is a helper method for the standard
     * onUpdate() callback that handles one widget update at a time.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetId      The current app widget id.
     */
    private void updateAppWidget(Context context,
                                 AppWidgetManager appWidgetManager,
                                 int appWidgetId,boolean isIntent,Object intentExtras) {



        // Construct the RemoteViews object.
        RemoteViews  remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.appwidget_id,
                String.valueOf(appWidgetId));

        remoteViews =   getUpdatedRemoteView(context,remoteViews,isIntent,  intentExtras);

        // Setup update button to send an update request as a pending intent.
        Intent intentUpdate = new Intent(context, AppWidget.class);

        // The intent action must be an app widget update.
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Include the widget ID to be updated as an intent extra.
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Wrap it all in a pending intent to send a broadcast.
        // Use the app widget ID as the request code (third argument) so that
        // each intent is unique.
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Assign the pending intent to the button onClick handler
        remoteViews.setOnClickPendingIntent(R.id.button_update, pendingUpdate);

        // Instruct the widget manager to update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }




    public static RemoteViews getUpdatedRemoteView(Context context,RemoteViews remoteViews ,
                                                   boolean isIntent,Object intentExtras ) {
        String dateString =
                DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

        if(isIntent)
        {
            dateString=dateString+" "+String.valueOf(intentExtras);
        }

        remoteViews.setTextViewText(R.id.last_time_val, dateString);
        String nextAlarmVal = AlarmManagerUtil.getNextAlarmTime(context);
        remoteViews.setTextViewText(R.id.next_alarm_val, nextAlarmVal);
        if(!nextAlarmVal.equals(context.getString(R.string.no_alarm_scheduled)))
        {
            String timeToNextAlarmVal = AlarmManagerUtil.getTimeToNextAlarm(context);
            remoteViews.setTextViewText(R.id.time_to_next_alarm_val, timeToNextAlarmVal);
        }
        else
        {
            remoteViews.setTextViewText(R.id.time_to_next_alarm_val, "");
            remoteViews.setViewVisibility(R.id.time_to_next_alarm_val, GONE);
        }
        return remoteViews ;
    }

}

