package it.bz.beacon.adminapp.ui.main.beacon;

import android.os.Bundle;

import java.util.ArrayList;

import it.bz.beacon.adminapp.data.entity.Beacon;

public class AllBeaconsFragment extends BaseBeaconsFragment {

    public AllBeaconsFragment() {
        // Required empty public constructor
    }

    public static AllBeaconsFragment newInstance() {
        AllBeaconsFragment fragment = new AllBeaconsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        viewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
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
    @Override
    protected ArrayList<Beacon> getBeacons() {
        ArrayList<Beacon> beacons = new ArrayList<>();

        Beacon b = new Beacon();
        b.setId(1L);
        b.setName("Beacon 1");
        b.setDescription("A2039847270");
        b.setBatteryLevel(45);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setName("Beacon 2");
        b.setDescription("A6143983537");
        b.setBatteryLevel(11);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setName("Beacon 3");
        b.setDescription("A3495783439");
        b.setBatteryLevel(87);
        beacons.add(b);

        b = new Beacon();
        b.setId(4L);
        b.setName("Beacon 4");
        b.setDescription("A4439847270");
        b.setBatteryLevel(5);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setName("Beacon 5");
        b.setDescription("A1143983537");
        b.setBatteryLevel(50);
        beacons.add(b);

        b = new Beacon();
        b.setId(6L);
        b.setName("Beacon 6");
        b.setDescription("A6547783439");
        b.setBatteryLevel(90);
        beacons.add(b);

        b = new Beacon();
        b.setId(1L);
        b.setName("Beacon 1");
        b.setDescription("A2039847270");
        b.setBatteryLevel(45);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setName("Beacon 2");
        b.setDescription("A6143983537");
        b.setBatteryLevel(11);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setName("Beacon 3");
        b.setDescription("A3495783439");
        b.setBatteryLevel(87);
        beacons.add(b);

        b = new Beacon();
        b.setId(4L);
        b.setName("Beacon 4");
        b.setDescription("A4439847270");
        b.setBatteryLevel(5);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setName("Beacon 5");
        b.setDescription("A1143983537");
        b.setBatteryLevel(50);
        beacons.add(b);

        b = new Beacon();
        b.setId(6L);
        b.setName("Beacon 6");
        b.setDescription("A6547783439");
        b.setBatteryLevel(90);
        beacons.add(b);

        return beacons;
    }

    @Override
    public void onRefresh() {

    }
}
