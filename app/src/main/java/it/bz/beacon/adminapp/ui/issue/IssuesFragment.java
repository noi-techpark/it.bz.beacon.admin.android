package it.bz.beacon.adminapp.ui.issue;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.repository.BeaconIssueRepository;
import it.bz.beacon.adminapp.data.viewmodel.BeaconIssueViewModel;
import it.bz.beacon.adminapp.eventbus.LocationEvent;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.RadiusFilterEvent;
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

    private String searchFilter = "";
    private int radiusFilter = 0;
    private LatLng currentLocation = null;

    public IssuesFragment() {
        // Required empty public constructor
    }

    public static IssuesFragment newInstance(LatLng currentLocation) {
        IssuesFragment fragment = new IssuesFragment();
        fragment.setCurrentLocation(currentLocation);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        beaconIssueViewModel = ViewModelProviders.of(this).get(BeaconIssueViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PubSub.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issues, container, false);
        ButterKnife.bind(this, view);

        adapter = new BeaconIssueAdapter(getContext());
        adapter.setCurrentLocation(currentLocation);
        recyclerIssues.setAdapter(adapter);
        recyclerIssues.setHasFixedSize(true);
        recyclerIssues.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemAnimator animator = recyclerIssues.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        swipeIssues.setOnRefreshListener(this);
        swipeIssues.setColorSchemeResources(R.color.primary);

        loadData();
        return view;
    }

    private void setCurrentLocation(LatLng currentLocation) {
        this.currentLocation = currentLocation;
    }

    private void loadData() {
        showLoading();
        beaconIssueViewModel.getAllIssuesWithBeacon().observe(getViewLifecycleOwner(), new Observer<List<IssueWithBeacon>>() {
            @Override
            public void onChanged(List<IssueWithBeacon> issuesWithBeacon) {
                if (issuesWithBeacon == null || issuesWithBeacon.size() <= 0) {
                    showNoData();
                }
                else {
                    adapter.setIssues(issuesWithBeacon);
                    showList();
                }
            }
        });
    }

    @Subscribe
    public void onRadiusFilterChanged(RadiusFilterEvent event) {
        radiusFilter = event.getRadius();
        setRadiusFilter(radiusFilter);
    }

    @Subscribe
    public void onLocationChanged(LocationEvent event) {
        currentLocation = event.getLocation();
        adapter.setCurrentLocation(currentLocation);
    }

    void setSearchFilter(String filter) {
        searchFilter = filter.replace('#', ' ');
        adapter.getFilter().filter(searchFilter + '#' + String.valueOf(radiusFilter));
    }

    void setRadiusFilter(int filter) {
        radiusFilter = filter;
        adapter.getFilter().filter(searchFilter + '#' + String.valueOf(radiusFilter));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                setSearchFilter(s);
                return false;
            }
        });
    }

    private void showLoading() {
        txtEmpty.setVisibility(View.GONE);
        recyclerIssues.setVisibility(View.GONE);
        swipeIssues.setRefreshing(true);
    }

    private void showList() {
        txtEmpty.setVisibility(View.GONE);
        recyclerIssues.setVisibility(View.VISIBLE);
        swipeIssues.setRefreshing(false);
    }

    private void showNoData() {
        recyclerIssues.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
        swipeIssues.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        BeaconIssueRepository beaconIssueRepository = new BeaconIssueRepository(getContext());
        beaconIssueRepository.refreshBeaconIssues(null, new DataUpdateEvent() {
            @Override
            public void onSuccess() {
                Log.i(AdminApplication.LOG_TAG, "Issues refreshed!");
            }

            @Override
            public void onError() {
                Log.e(AdminApplication.LOG_TAG, "Error refreshing issues");
                swipeIssues.setRefreshing(false);
            }

            @Override
            public void onAuthenticationFailed() {
                Log.e(AdminApplication.LOG_TAG, "Authentication failed");
                swipeIssues.setRefreshing(false);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthenticationFailureDialog();
                    }
                });
            }
        });
    }

    private void showAuthenticationFailureDialog() {
        AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom))
                .setMessage(getString(R.string.error_authorization))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AdminApplication.renewLogin(getActivity());
                    }
                })
                .create();
        dialog.show();
    }
}
