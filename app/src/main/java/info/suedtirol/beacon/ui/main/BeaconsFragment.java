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

import butterknife.BindView;
import butterknife.ButterKnife;
import info.suedtirol.beacon.R;
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

       // loadData();
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
