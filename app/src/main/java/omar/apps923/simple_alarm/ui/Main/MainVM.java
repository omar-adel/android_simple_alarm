package omar.apps923.simple_alarm.ui.Main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.alarm.model.sqlite.Alarm;
import com.alarm.model.sqlite.AlarmDao;
import com.alarm.model.sqlite.AlarmDatabase;

import java.util.List;


/**
 * Created by guendouz on 15/02/2018.
 */

public class MainVM extends AndroidViewModel {

    private AlarmDao alarmDao;
    public MainVM(@NonNull Application application) {
        super(application);
        alarmDao = AlarmDatabase.getInstance(application).alarmDao();
    }

    LiveData<List<Alarm>> getAllAlarms() {
        return alarmDao.getAll();
    }


}
