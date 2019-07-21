package omar.apps923.simple_alarm.ui.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alarm.model.sqlite.Alarm;

import butterknife.BindView;
import butterknife.ButterKnife;
import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.ui.AddEditAlarm.AddEditAlarmActivity;

import static com.alarm.util.AlarmManagerUtil.getAmPm;
import static com.alarm.util.AlarmManagerUtil.getReadableTime;
import static omar.apps923.simple_alarm.ui.AddEditAlarm.AddEditAlarmActivity.ALARM_OBJECT;

public class AlarmVH  extends RecyclerView.ViewHolder {

    @BindView(R.id.ar_time)
    TextView time;
    @BindView(R.id.ar_am_pm)
    TextView  amPm;
    @BindView(R.id.ar_label)
    TextView  label;
    @BindView(R.id.ar_days)
    TextView days;


    View itemView;
    int positionClicked;
    Object itemClicked;
    Context context;

    private String[] mDays;
    private int mAccentColor = -1;


    public AlarmVH(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        ButterKnife.bind(this, itemView);
    }

    public void bindData(final Object item, final int position) {

        final Alarm alarm = (Alarm) item;

        if(mAccentColor == -1) {
            mAccentColor = ContextCompat.getColor(context, R.color.accent);
        }

        if(mDays == null){
            mDays = context.getResources().getStringArray(R.array.days_abbreviated);
        }


         time.setText(getReadableTime(alarm.getTime()));
         amPm.setText(getAmPm(alarm.getTime()));
         label.setText(alarm.getLabel());
        days.setText(buildSelectedDays(alarm));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positionClicked = position;
                itemClicked = item;
                onClickItem(view, alarm);
            }
        });

    }

    public static View getView(Context context, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.alarm_row, viewGroup, false);
    }

    private Spannable buildSelectedDays(Alarm alarm) {

        final int numDays = 7;

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan span;

        int startIndex, endIndex;
        boolean isSelected=false;
        for (int i = 0; i < numDays; i++) {

            startIndex = builder.length();

            final String dayText = mDays[i];
            builder.append(dayText);
            builder.append(" ");

            endIndex = startIndex + dayText.length();
           switch (i)
           {
               case 0:
                  isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(0)==1;
                   break;
               case 1:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(1)==1;
                    break;
               case 2:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(2)==1;
                   break;
               case 3:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(3)==1;
                   break;
               case 4:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(4)==1;
                   break;
               case 5:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(5)==1;
                   break;
               case 6:
                   isSelected = Alarm.convertDaysToArr(alarm.getDays()).get(6)==1;
                   break;
           }
             if(isSelected) {
                span = new ForegroundColorSpan(mAccentColor);
                builder.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return builder;

    }


    public void onClickItem(View view, Alarm alarm) {

        final Intent launchEditAlarmIntent =
                AddEditAlarmActivity.buildAddEditAlarmActivityIntent(
                        context, AddEditAlarmActivity.EDIT_ALARM
                );
        launchEditAlarmIntent.putExtra(ALARM_OBJECT, alarm);
        context.startActivity(launchEditAlarmIntent);
    }
}


