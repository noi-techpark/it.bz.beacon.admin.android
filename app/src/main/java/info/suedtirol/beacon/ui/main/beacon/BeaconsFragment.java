package info.suedtirol.beacon.ui.main.beacon;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

import info.suedtirol.beacon.data.entity.Beacon;

public class BeaconsFragment extends BaseBeaconsFragment {

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
        b.setTitle("Beacon 1");
        b.setDescription("A2039847270");
        b.setBattery(45.5);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setTitle("Beacon 2");
        b.setDescription("A6143983537");
        b.setBattery(11.2);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setTitle("Beacon 3");
        b.setDescription("A3495783439");
        b.setBattery(87);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(4L);
        b.setTitle("Beacon 4");
        b.setDescription("A4439847270");
        b.setBattery(5.5);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setTitle("Beacon 5");
        b.setDescription("A1143983537");
        b.setBattery(50);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(6L);
        b.setTitle("Beacon 6");
        b.setDescription("A6547783439");
        b.setBattery(90);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(1L);
        b.setTitle("Beacon 1");
        b.setDescription("A2039847270");
        b.setBattery(45.5);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setTitle("Beacon 2");
        b.setDescription("A6143983537");
        b.setBattery(11.2);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setTitle("Beacon 3");
        b.setDescription("A3495783439");
        b.setBattery(87);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(4L);
        b.setTitle("Beacon 4");
        b.setDescription("A4439847270");
        b.setBattery(5.5);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setTitle("Beacon 5");
        b.setDescription("A1143983537");
        b.setBattery(50);
        b.setWarning(false);
        beacons.add(b);

        b = new Beacon();
        b.setId(6L);
        b.setTitle("Beacon 6");
        b.setDescription("A6547783439");
        b.setBattery(90);
        b.setWarning(false);
        beacons.add(b);

        return beacons;
    }
}
