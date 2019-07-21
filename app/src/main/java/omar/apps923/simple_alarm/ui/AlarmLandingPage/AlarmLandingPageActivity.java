package omar.apps923.simple_alarm.ui.AlarmLandingPage;

import android.os.Build;
import android.view.WindowManager;

import com.alarm.model.sqlite.Alarm;
import com.ui_base.BaseAppCompatActivity;

import omar.apps923.simple_alarm.R;

import static com.alarm.util.AlarmManagerUtil.ALARM_ACTION_OBJECT;

public   class AlarmLandingPageActivity extends BaseAppCompatActivity {




    @Override
    protected int getLayoutResource() {
        return R.layout.activity_alarm_landing_page;
    }


    @Override
    protected void configureUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        final Alarm alarm = getIntent().getParcelableExtra(ALARM_ACTION_OBJECT);
        if(getSupportFragmentManager().findFragmentById(R.id.alarm_action_frag_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.alarm_action_frag_container, AlarmLandingPageFragment.newInstance(alarm))
                    .commit();
        }
    }

}
