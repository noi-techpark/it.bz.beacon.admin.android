package info.suedtirol.beacon.ui.main.beacon;

import android.content.Context;
import android.os.Bundle;
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
import info.suedtirol.beacon.ui.adapter.BeaconAdapter;

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
