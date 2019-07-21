package omar.apps923.simple_alarm.ui.AlarmLandingPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alarm.model.sqlite.Alarm;
import com.ui_base.BaseSupportFragment;

import butterknife.BindView;
import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.ui.Main.MainActivity;

import static com.alarm.util.AlarmManagerUtil.ALARM_ACTION_OBJECT;
import static com.alarm.util.AlarmManagerUtil.cancelThisAlarm;

public   class AlarmLandingPageFragment extends BaseSupportFragment implements View.OnClickListener {

    @BindView(R.id.load_main_activity_btn)
    Button loadMainActivityBtn;
    @BindView(R.id.dismiss_btn)
    Button dismissBtn;

    public static AlarmLandingPageFragment newInstance(Alarm alarm) {
        AlarmLandingPageFragment fragment = new AlarmLandingPageFragment();
             Bundle args = new Bundle();
            args.putParcelable(ALARM_ACTION_OBJECT, alarm);
            fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_alarm_landing_page;
    }

    @Override
    protected void configureUI() {
        loadMainActivityBtn.setOnClickListener(this);
        dismissBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.load_main_activity_btn) {
          startActivity(new Intent(getContext(), MainActivity.class));
            getContainerActivity().finish();
        }
               else
        if(view.getId()==   R.id.dismiss_btn)
        {

                cancelThisAlarm(getContext(), (Alarm) getArguments().getParcelable(ALARM_ACTION_OBJECT));
               // AlarmDatabase.getInstance(getContainerActivity()).alarmDao().delete(getArguments().getParcelable(ALARM_ACTION_OBJECT));
                //AlarmManagerUtil.scheduleNextAlarm(getContainerActivity());
                getContainerActivity().finish();

        }

    }
}
