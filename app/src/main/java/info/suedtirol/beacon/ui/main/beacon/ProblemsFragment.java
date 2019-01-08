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

public class ProblemsFragment extends BaseBeaconsFragment {

    public ProblemsFragment() {
        // Required empty public constructor
    }

    public static ProblemsFragment newInstance() {
        ProblemsFragment fragment = new ProblemsFragment();
        return fragment;
    }

    // TODO: Replace this fake method
    @Override
    protected ArrayList<Beacon> getBeacons() {
        ArrayList<Beacon> beacons = new ArrayList<>();

        Beacon b = new Beacon();
        b.setId(1L);
        b.setTitle("Beacon 71");
        b.setDescription("A2039847270");
        b.setBattery(45.5);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(2L);
        b.setTitle("Beacon 22");
        b.setDescription("A6143983537");
        b.setBattery(11.2);
        b.setWarning(true);
        beacons.add(b);

        b = new Beacon();
        b.setId(3L);
        b.setTitle("Beacon 32");
        b.setDescription("A3495783439");
        b.setBattery(87);
        b.setWarning(true);
        beacons.add(b);

        return beacons;
    }
}
