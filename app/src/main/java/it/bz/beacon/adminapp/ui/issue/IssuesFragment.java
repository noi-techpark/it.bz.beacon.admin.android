package it.bz.beacon.adminapp.ui.issue;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.ui.adapter.BeaconIssueAdapter;

public class IssuesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.issues_content)
    protected SwipeRefreshLayout swipeIssues;

    @BindView(R.id.issues_list)
    protected RecyclerView recyclerIssues;

    @BindView(R.id.issues_empty)
    protected TextView txtEmpty;

    private BeaconIssueAdapter adapter;

    private BeaconIssueViewModel beaconIssueViewModel;

    public IssuesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setHasOptionsMenu(true);
        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issues, container, false);
        ButterKnife.bind(this, view);

        adapter = new BeaconIssueAdapter(getContext());
        recyclerIssues.setAdapter(adapter);
        recyclerIssues.setHasFixedSize(true);
        recyclerIssues.setItemAnimator(new DefaultItemAnimator());
        recyclerIssues.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeIssues.setOnRefreshListener(this);
        swipeIssues.setColorSchemeResources(R.color.primary);

        loadData();
        return view;
    }

    private void loadData() {
//        showLoading();
//        getIssues(new Observer<List<IssueMinimal>>() {
//            @Override
//            public void onChanged(@Nullable List<IssueMinimal> issues) {
//                if (issues == null || issues.size() <= 0) {
//                    showNoData();
//                } else {
//                    adapter.setIssues(issues);
//                    showList();
//                }
//            }
//        });
//        showList();
        showNoData();
    }

    public static IssuesFragment newInstance() {
        IssuesFragment fragment = new IssuesFragment();
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.beacons, menu);
    }

    private void showLoading() {
        txtEmpty.setVisibility(View.GONE);
        recyclerIssues.setVisibility(View.GONE);
        swipeIssues.setRefreshing(true);
    }

    private void showList() {
        txtEmpty.setVisibility(View.GONE);
        swipeIssues.setVisibility(View.VISIBLE);
        swipeIssues.setRefreshing(false);
    }

    private void showNoData() {
        swipeIssues.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
        swipeIssues.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        loadData();
    }
}
