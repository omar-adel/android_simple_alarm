package omar.apps923.simple_alarm.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.alarm.model.sqlite.Alarm;
import com.alarm.util.AlarmManagerUtil;
import com.alarm.util.NotificationUtils;
import com.alarm.util.ParcelableUtil;

import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.ui.AlarmLandingPage.AlarmLandingPageActivity;

import static com.alarm.util.AlarmManagerUtil.ALARM_ACTION_OBJECT;
import static com.alarm.util.AlarmManagerUtil.ALARM_TO_FIRE;
import static com.alarm.util.AlarmManagerUtil.getNotificationId;
import static com.alarm.util.AlarmManagerUtil.isAlarmActiveInCurrentTime;
import static com.alarm.util.NotificationUtils.ANDROID_CHANNEL_ID;


public class AlarmReceiver extends BroadcastReceiver {

    NotificationUtils notificationUtil;

    @Override
    public void onReceive(Context context, Intent intent) {
        byte[] bytes = intent.getByteArrayExtra(ALARM_TO_FIRE);
        Alarm alarm = ParcelableUtil.unmarshall(bytes, Alarm.CREATOR);
        if (isAlarmActiveInCurrentTime(alarm)) {
            notificationUtil = new NotificationUtils(context);

            final int id = getNotificationId(alarm);
            final Intent notifIntent = new Intent(context, AlarmLandingPageActivity.class);
            notifIntent.putExtra(ALARM_ACTION_OBJECT, alarm);
            notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            final PendingIntent pIntent = PendingIntent.getActivity(
                    context, id, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ANDROID_CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_alarm_white_24dp);
            builder.setColor(ContextCompat.getColor(context, R.color.accent));
            builder.setContentTitle(context.getString(R.string.app_name));
            builder.setContentText(alarm.getLabel());
            builder.setTicker(alarm.getLabel());
            builder.setVibrate(new long[]{1000, 500, 1000, 500, 1000, 500});
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            builder.setContentIntent(pIntent);
            builder.setAutoCancel(true);
            builder.setPriority(Notification.PRIORITY_HIGH);

            notificationUtil.getManager().notify(id, builder.build());
        }

        AlarmManagerUtil.scheduleNextAlarm(context);
    }


}
