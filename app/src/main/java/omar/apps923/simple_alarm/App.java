package omar.apps923.simple_alarm;

import android.app.Application;
import android.content.ContextWrapper;

import com.alarm.model.Prefs;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Net15 on 13/12/2016.
 */
public class App extends Application {

    public App(){

    }


    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(false)
                .build();

        try {
            EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        }
        catch (Exception e)
        {
              //Log.e("ExceptionEventBusaddIndex",e.toString());
        }

    }


}
