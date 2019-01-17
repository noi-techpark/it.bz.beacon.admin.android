package it.bz.beacon.adminapp.ui.main.beacon;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.ui.adapter.BeaconAdapter;

abstract class BaseBeaconsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.beacons_content)
    protected SwipeRefreshLayout swipeBeacons;

    @BindView(R.id.beacons_list)
    protected RecyclerView recyclerBeacons;

    @BindView(R.id.beacons_empty)
    protected TextView txtEmpty;

    @BindView(R.id.beacons_loading)
    protected ContentLoadingProgressBar progressBar;

    private BeaconAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

        swipeBeacons.setOnRefreshListener(this);
        swipeBeacons.setColorSchemeResources(R.color.primary);

        loadData();
        return view;
    }

    // TODO: use LiveData here
    private void loadData() {
        getBeacons(new Observer<List<BeaconMinimal>>() {
            @Override
            public void onChanged(@Nullable List<BeaconMinimal> beacons) {
                if (beacons == null || beacons.size() <= 0) {
                    showNoData();
                } else {
                    adapter.setBeacons(beacons);
                    showList();
                }
            }
        });

        showList();
    }

    abstract protected void getBeacons(Observer<List<BeaconMinimal>> observer);

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search:
                break;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onRefresh() {
        loadData();
        swipeBeacons.setRefreshing(false);
    }
}
