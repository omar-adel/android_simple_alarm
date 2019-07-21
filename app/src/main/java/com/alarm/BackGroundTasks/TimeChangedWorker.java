package com.alarm.BackGroundTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.alarm.Widgets.WigetUtil;
import com.alarm.util.AlarmManagerUtil;


public class TimeChangedWorker extends Worker {

    public static  String TimeChangedWorkerTag="TimeChangedWorkerTag";
    public TimeChangedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        Log.e("TimeChangedWorker", "TimeChangedWorker");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                AlarmManagerUtil.scheduleNextAlarm(getApplicationContext());
            }
        });

        WigetUtil.UpdateWidget(getApplicationContext());

        return Result.success();
    }

}

