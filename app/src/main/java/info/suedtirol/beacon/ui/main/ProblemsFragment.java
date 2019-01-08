package info.suedtirol.beacon.ui.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ImageViewCompat;
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

public class ProblemsFragment extends Fragment {

    @BindView(R.id.beacons_list)
    protected RecyclerView recyclerBeacons;

    @BindView(R.id.beacons_empty)
    protected TextView txtEmpty;

    private BeaconAdapter adapter;

    public ProblemsFragment() {
        // Required empty public constructor
    }

    public static ProblemsFragment newInstance() {
        ProblemsFragment fragment = new ProblemsFragment();
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
        View view = inflater.inflate(R.layout.fragment_problems, container, false);
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
