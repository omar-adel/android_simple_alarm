package omar.apps923.simple_alarm.ui.AddEditAlarm;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.IntDef;

import com.alarm.model.sqlite.Alarm;
import com.ui_base.BaseAppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import omar.apps923.simple_alarm.R;


public  class AddEditAlarmActivity extends BaseAppCompatActivity {

     public static final String MODE_EXTRA = "mode_extra";
    public static final String ALARM_OBJECT = "ALARM_OBJECT";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({EDIT_ALARM,ADD_ALARM,UNKNOWN})
    @interface Mode{}
    public static final int EDIT_ALARM = 1;
    public static final int ADD_ALARM = 2;
    public static final int UNKNOWN = 0;


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_edit_alarm;
    }

    @Override
    protected void configureUI() {
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getToolbarTitle());

        final Alarm alarm = getAlarm();
        if(getSupportFragmentManager().findFragmentById(R.id.edit_alarm_frag_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.edit_alarm_frag_container, AddEditAlarmFragment.newInstance(alarm))
                    .commit();
        }
    }

    private Alarm getAlarm() {
        switch (getMode()) {
            case EDIT_ALARM:
                return (Alarm) getIntent().getParcelableExtra(ALARM_OBJECT);
            case ADD_ALARM:
                return null ;
            case UNKNOWN:
            default:
                throw new IllegalStateException("Mode supplied as intent extra for " +
                        AddEditAlarmActivity.class.getSimpleName() + " must match value in " +
                        Mode.class.getSimpleName());
        }
    }

    private @Mode int getMode() {
        final @Mode int mode = getIntent().getIntExtra(MODE_EXTRA, UNKNOWN);
        return mode;
    }

    private String getToolbarTitle() {
        int titleResId;
        switch (getMode()) {
            case EDIT_ALARM:
                titleResId = R.string.edit_alarm;
                break;
            case ADD_ALARM:
                titleResId = R.string.add_alarm;
                break;
            case UNKNOWN:
            default:
                throw new IllegalStateException("Mode supplied as intent extra for " +
                        AddEditAlarmActivity.class.getSimpleName() + " must match value in " +
                        Mode.class.getSimpleName());
        }
        return getString(titleResId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent buildAddEditAlarmActivityIntent(Context context, @Mode int mode) {
        final Intent i = new Intent(context, AddEditAlarmActivity.class);
        i.putExtra(MODE_EXTRA, mode);
        return i;
    }

}
