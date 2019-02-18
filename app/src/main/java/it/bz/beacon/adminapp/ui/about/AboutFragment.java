package it.bz.beacon.adminapp.ui.about;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;

public class AboutFragment extends Fragment {


    @BindView(R.id.about_empty)
    protected TextView txtEmpty;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

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

    private void showData() {
        txtEmpty.setVisibility(View.GONE);
//        recyclerBeacons.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
//        recyclerBeacons.setVisibility(View.GONE);
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
