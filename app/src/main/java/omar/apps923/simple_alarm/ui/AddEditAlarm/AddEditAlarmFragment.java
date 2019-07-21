package omar.apps923.simple_alarm.ui.AddEditAlarm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import com.alarm.model.sqlite.Alarm;
import com.alarm.util.AlarmManagerUtil;
import com.ui_base.BaseSupportFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import omar.apps923.simple_alarm.Events.NotifyEvent;
import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.util.ViewUtils;

import static com.alarm.util.AlarmManagerUtil.cancelThisAlarm;
import static com.alarm.util.AlarmManagerUtil.scheduleNextAlarm;
import static omar.apps923.simple_alarm.ui.AddEditAlarm.AddEditAlarmActivity.ALARM_OBJECT;
import static omar.apps923.simple_alarm.util.Constants.DELETE_EVENT;
import static omar.apps923.simple_alarm.util.Constants.INSERT_EVENT;
import static omar.apps923.simple_alarm.util.Constants.UPDATE_EVENT;

public final class AddEditAlarmFragment extends BaseSupportFragment {

    @BindView(R.id.edit_alarm_time_picker)
    TimePicker editAlarmTimePicker;
    @BindView(R.id.edit_alarm_label)
    EditText editAlarmLabel;
    @BindView(R.id.edit_alarm_mon)
    CheckBox editAlarmMon;
    @BindView(R.id.edit_alarm_tues)
    CheckBox editAlarmTues;
    @BindView(R.id.edit_alarm_wed)
    CheckBox editAlarmWed;
    @BindView(R.id.edit_alarm_thurs)
    CheckBox editAlarmThurs;
    @BindView(R.id.edit_alarm_fri)
    CheckBox editAlarmFri;
    @BindView(R.id.edit_alarm_sat)
    CheckBox editAlarmSat;
    @BindView(R.id.edit_alarm_sun)
    CheckBox editAlarmSun;

    AddEditAlarmVM addEditAlarmVM ;
    public static AddEditAlarmFragment newInstance(Alarm alarm) {

        AddEditAlarmFragment fragment = new AddEditAlarmFragment();
        if (alarm != null) {
            //edit
            Bundle args = new Bundle();
            args.putParcelable(ALARM_OBJECT, alarm);
            fragment.setArguments(args);
        } else {
            //add
        }

        return fragment;
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_add_edit_alarm;
    }

    @Override
    protected void configureUI() {
        addEditAlarmVM = ViewModelProviders.of(this).get(AddEditAlarmVM.class);


        setHasOptionsMenu(true);
        final Alarm alarm = getAlarm();
          if (alarm != null) {
             ViewUtils.setTimePickerTime(editAlarmTimePicker, alarm.getTime());
             editAlarmLabel.setText(alarm.getLabel());
              setDayCheckboxes(alarm);
          }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_alarm_menu, menu);
        if (getAlarm() == null) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Alarm getAlarm() {
        if(getArguments()!=null)
        {
            if (getArguments().containsKey(ALARM_OBJECT)) {
                return (Alarm) getArguments().getParcelable(ALARM_OBJECT);
            }
        }

        return null;
    }

    private void setDayCheckboxes(Alarm alarm) {
        editAlarmMon.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(0) == 1);
        editAlarmTues.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(1) == 1);
        editAlarmWed.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(2) == 1);
        editAlarmThurs.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(3) == 1);
        editAlarmFri.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(4) == 1);
        editAlarmSat.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(5) == 1);
        editAlarmSun.setChecked(Alarm.convertDaysToArr(alarm.getDays()).get(6) == 1);

    }

    private void save() {

         int hours=ViewUtils.getTimePickerHour(editAlarmTimePicker);
        int minutes=ViewUtils.getTimePickerMinute(editAlarmTimePicker);
           Alarm alarm = getAlarm();
            if(alarm==null)
            {
                alarm=new Alarm(hours,minutes,0);
             }
        else
            {
                alarm.editAlarm(hours,minutes);
             }

        alarm.setLabel(editAlarmLabel.getText().toString());

        alarm = addEditAlarmVM.setDays(alarm,editAlarmMon.isChecked(),editAlarmTues.isChecked(),editAlarmWed.isChecked()
        ,editAlarmThurs.isChecked(),editAlarmFri.isChecked(),editAlarmSat.isChecked()
        ,editAlarmSun.isChecked());
         if(getAlarm()==null)
        {
            addEditAlarmVM.insertAlarm(alarm);
        }
        else
        {
            cancelThisAlarm(getContainerActivity(),alarm);
           addEditAlarmVM.updateAlarm(alarm);
        }
        getContainerActivity().finish();
    }

    private void delete() {

        final Alarm alarm = getAlarm();
        if (alarm != null) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext(), R.style.DeleteAlarmDialogTheme);
            builder.setTitle(R.string.delete_dialog_title);
            builder.setMessage(R.string.delete_dialog_content);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //Cancel any pending notifications for this alarm
                    cancelThisAlarm(getContext(), alarm);

                      addEditAlarmVM.deleteAlarm(alarm);
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        }

    }

    @Subscribe
    public void onNotifyEvent(NotifyEvent event) {
        if(event.getEvent().equals(INSERT_EVENT))
        {
            final int messageId = (Long.valueOf(event.getValue()) > 0 ) ? R.string.update_complete : R.string.update_failed;
            getContainerActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
                    if(Long.valueOf(event.getValue())>0)
                    {
                        scheduleNextAlarm(getContext());
                    }
                }
            });


         }
        else
        if(event.getEvent().equals(UPDATE_EVENT))
        {
            if( Integer.valueOf(event.getValue())>=0)
            {
                getContainerActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.update_complete, Toast.LENGTH_SHORT).show();

                        scheduleNextAlarm(getContext());
                    }
                });
            }
            else
            {
                getContainerActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.update_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
         }
        else
        if(event.getEvent().equals(DELETE_EVENT))
        {

            int messageId;
            if (Integer.valueOf(event.getValue()) >0) {
                messageId = R.string.delete_complete;
                getContainerActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
                        AlarmManagerUtil.scheduleNextAlarm(getContainerActivity());
                    }
                });
                getContainerActivity().finish();
            } else {
                messageId = R.string.delete_failed;
                getContainerActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
                    }
                });
            }

         }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
