package info.suedtirol.beacon.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.suedtirol.beacon.R;
import info.suedtirol.beacon.data.entity.Beacon;
import info.suedtirol.beacon.ui.main.adapter.BeaconAdapter;

public class BeaconsFragment extends Fragment {

    @BindView(R.id.beacons_list)
    protected RecyclerView recyclerBeacons;

    @BindView(R.id.beacons_empty)
    protected TextView txtEmpty;

    private BeaconAdapter adapter;

    public BeaconsFragment() {
        // Required empty public constructor
    }

    public static BeaconsFragment newInstance() {
        BeaconsFragment fragment = new BeaconsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        viewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);
        ButterKnife.bind(this, view);

        adapter = new BeaconAdapter(getContext());
        recyclerBeacons.setAdapter(adapter);
        recyclerBeacons.setItemAnimator(new DefaultItemAnimator());
        recyclerBeacons.setLayoutManager(new LinearLayoutManager(getContext()));

        loadData();
        return view;
    }

//    private void loadData() {
//        showLoading();
//        final LiveData<List<IncidentHistory>> liveData = viewModel.getAllByMatriculationNumber(matriculationNumber);
//        liveData.observe(this, new Observer<List<IncidentHistory>>() {
//            @Override
//            public void onChanged(@Nullable List<IncidentHistory> incidentHistories) {
//                liveData.removeObserver(this);
//                if (incidentHistories != null) {
//                    adapter.setIncidentHistorys(incidentHistories);
//                    if (incidentHistories.size() > 0) {
//                        showList();
//                    }
//                    else {
//                        showNoData();
//                    }
//                }
//                else {
//                    showNoData();
//                }
//            }
//        });
//    }

    // TODO: Replace this fake method
    private void loadData() {
        showLoading();
        List<Beacon> beacons = new ArrayList<>();

        Beacon b = new Beacon();
        b.setId(1L);
        b.setTitle("Beacon 1");
        b.setDescription("A2039847270");
        b.setBattery(45.5);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setTitle("Beacon 2");
        b.setDescription("A6143983537");
        b.setBattery(87);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setTitle("Beacon 3");
        b.setDescription("A3495783439");
        b.setBattery(11.2);
        beacons.add(b);

        b = new Beacon();
        b.setId(4L);
        b.setTitle("Beacon 4");
        b.setDescription("A2039847270");
        b.setBattery(5.5);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setTitle("Beacon 5");
        b.setDescription("A6143983537");
        b.setBattery(50);
        beacons.add(b);

        b = new Beacon();
        b.setId(6L);
        b.setTitle("Beacon 6");
        b.setDescription("A3495783439");
        b.setBattery(90);
        beacons.add(b);

        adapter.setBeacons(beacons);
        showList();
    }

    private void showLoading() {
        txtEmpty.setVisibility(View.GONE);
        //  loader.setVisibility(View.VISIBLE);
    }

    private void showList() {
        txtEmpty.setVisibility(View.GONE);
        recyclerBeacons.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
        recyclerBeacons.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
