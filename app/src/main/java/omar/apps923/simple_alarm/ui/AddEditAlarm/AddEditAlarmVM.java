package omar.apps923.simple_alarm.ui.AddEditAlarm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.alarm.model.sqlite.Alarm;
import com.alarm.model.sqlite.AlarmDao;
import com.alarm.model.sqlite.AlarmDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import omar.apps923.simple_alarm.Events.NotifyEvent;

import static omar.apps923.simple_alarm.util.Constants.DELETE_EVENT;
import static omar.apps923.simple_alarm.util.Constants.INSERT_EVENT;
import static omar.apps923.simple_alarm.util.Constants.UPDATE_EVENT;


/**
 * Created by guendouz on 15/02/2018.
 */

public class AddEditAlarmVM extends AndroidViewModel {

    private AlarmDao alarmDao;
    public AddEditAlarmVM(@NonNull Application application) {
        super(application);
        alarmDao = AlarmDatabase.getInstance(application).alarmDao();
    }



    void insertAlarm(final Alarm alarm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long result = alarmDao.insert(alarm);
                alarm.setId(result);
                EventBus.getDefault().post(new NotifyEvent(INSERT_EVENT,String.valueOf(result),alarm));
            }
        }).start();
    }

    void updateAlarm(final Alarm alarm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result =  alarmDao.update(alarm);
                EventBus.getDefault().post(new NotifyEvent(UPDATE_EVENT,String.valueOf(result),alarm));
            }
        }).start();

    }

    void deleteAlarm(final Alarm alarm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result =  alarmDao.delete(alarm);
                EventBus.getDefault().post(new NotifyEvent(DELETE_EVENT,String.valueOf(result)));
            }
        }).start();

    }

    public Alarm setDays(Alarm alarm
                         ,boolean mon, boolean tue, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
        ArrayList<Integer> days = Alarm.getEmptyDaysArr() ;
        days.set(0,mon ? 1 : 0);
        days.set(1,tue ? 1 : 0);
        days.set(2,wed? 1 : 0);
        days.set(3,thur ? 1 : 0);
        days.set(4,fri ? 1 : 0);
        days.set(5,sat ? 1 : 0);
        days.set(6,sun ? 1 : 0);
        alarm.setDays(Alarm.convertDaysToStr(days));
        return alarm;
    }
}
