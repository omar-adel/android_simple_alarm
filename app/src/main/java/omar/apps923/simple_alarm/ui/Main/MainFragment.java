package omar.apps923.simple_alarm.ui.Main;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alarm.model.sqlite.Alarm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ui_base.BaseSupportFragment;

import java.util.List;

import butterknife.BindView;
import omar.apps923.simple_alarm.R;
import omar.apps923.simple_alarm.ui.AddEditAlarm.AddEditAlarmActivity;
import omar.apps923.simple_alarm.ui.CustomViews.EmptyRecyclerView;
import omar.apps923.simple_alarm.ui.ViewHolders.AlarmVH;
import omar.apps923.simple_alarm.ui.adapters.GenericRecyclerViewAdapter;
import omar.apps923.simple_alarm.util.DividerItemDecoration;

import static com.alarm.util.AlarmManagerUtil.checkAlarmPermissions;

public final class MainFragment extends BaseSupportFragment {
    @BindView(R.id.recycler)
    EmptyRecyclerView recycler;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private GenericRecyclerViewAdapter mAdapter;
    MainVM mainVM ;




    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_main;
    }

    @Override
    protected void configureUI() {
        mainVM = ViewModelProviders.of(this).get(MainVM.class);

         mAdapter = new GenericRecyclerViewAdapter(getContainerActivity(), new GenericRecyclerViewAdapter.AdapterDrawData() {
            @Override
            public RecyclerView.ViewHolder getView(ViewGroup parent,int viewType) {

                return new AlarmVH(getContainerActivity(),
                        AlarmVH.getView(getContainerActivity(), parent));
            }

            @Override
            public void bindView(GenericRecyclerViewAdapter genericRecyclerViewAdapter,
                                 RecyclerView.ViewHolder holder, Object item, int position) {
                ((AlarmVH) holder).bindData(
                        genericRecyclerViewAdapter.getItem(position), position);
            }
        });
        recycler.setEmptyView(emptyView);
        recycler.setAdapter(mAdapter);
        recycler.addItemDecoration(new DividerItemDecoration(getContext()));
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemAnimator(new DefaultItemAnimator());

         fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAlarmPermissions(getContainerActivity());
                final Intent i =
                        AddEditAlarmActivity.buildAddEditAlarmActivityIntent(
                                getContext(), AddEditAlarmActivity.ADD_ALARM
                        );
                startActivity(i);
            }
        });
       // mainVM.getAllAlarms().observe(this, alarmsObserver);
        mainVM.getAllAlarms().observe(this, this::onAlarmsChanged );


    }
    // Create the observer which updates the UI.
    Observer<List<Alarm>> alarmsObserver = new Observer<List<Alarm>>() {
        @Override
        public void onChanged(@Nullable   List<Alarm> alarms) {
            // Update the UI
            mAdapter.setAll(alarms);
        }
    };
    private void onAlarmsChanged(List<Alarm> alarms) {
        // Update the UI
        mAdapter.setAll(alarms);
     }

    @Override
    public void onStart() {
        super.onStart();
      }

    @Override
    public void onStop() {
        super.onStop();
     }



}
