package it.bz.beacon.adminapp.ui.main.beacon;

import java.util.ArrayList;

import it.bz.beacon.adminapp.data.entity.Beacon;

public class IssuesFragment extends BaseBeaconsFragment {

    public IssuesFragment() {
        // Required empty public constructor
    }

    public static IssuesFragment newInstance() {
        IssuesFragment fragment = new IssuesFragment();
        return fragment;
    }

    // TODO: Replace this fake method
    @Override
    protected ArrayList<Beacon> getBeacons() {
        ArrayList<Beacon> beacons = new ArrayList<>();

        Beacon b = new Beacon();
        b.setId(1L);
        b.setName("Beacon 71");
        b.setDescription("A2039847270");
        b.setBatteryLevel(45);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setName("Beacon 22");
        b.setDescription("A6143983537");
        b.setBatteryLevel(11);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setName("Beacon 32");
        b.setDescription("A3495783439");
        b.setBatteryLevel(87);
        beacons.add(b);

        return beacons;
    }

    @Override
    public void onRefresh() {

    }
}
