package it.bz.beacon.adminapp.ui.main.beacon;

import java.util.ArrayList;

import it.bz.beacon.adminapp.data.entity.Beacon;

public class DisturbancesFragment extends BaseBeaconsFragment {

    public DisturbancesFragment() {
        // Required empty public constructor
    }

    public static DisturbancesFragment newInstance() {
        DisturbancesFragment fragment = new DisturbancesFragment();
        return fragment;
    }

    // TODO: Replace this fake method
    @Override
    protected ArrayList<Beacon> getBeacons() {
        ArrayList<Beacon> beacons = new ArrayList<>();

        Beacon b = new Beacon();
        b.setId(4L);
        b.setTitle("Beacon 42");
        b.setDescription("A2039847270");
        b.setBattery(5.5);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(5L);
        b.setTitle("Beacon 53");
        b.setDescription("A2223983537");
        b.setBattery(50);
        b.setWarning(true);
        beacons.add(b);

        return beacons;
    }
}
