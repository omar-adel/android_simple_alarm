package com.alarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alarm.Widgets.WigetUtil;
import com.alarm.util.AlarmManagerUtil;


public class BootAndTimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("=============== " ,action);

        if (null != action) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED))
            {
                WigetUtil.UpdateWidget(context);
                WigetUtil.widget_startBackGroundUpdate(context);

                 AlarmManagerUtil.handleBootComplete(context);
            }
            else // TIME_SET
            {
                 WigetUtil.UpdateWidget(context);
                WigetUtil.widget_startBackGroundUpdate(context);

                AlarmManagerUtil.handleTimeChange(context);

            }
        }
    }
}