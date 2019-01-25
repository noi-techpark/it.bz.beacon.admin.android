package it.bz.beacon.adminapp.ui.main.beacon;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import java.util.List;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.viewmodel.BeaconViewModel;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.eventbus.StatusFilterEvent;

public class AllBeaconsFragment extends BaseBeaconsFragment {

    private BeaconViewModel beaconViewModel;

    public AllBeaconsFragment() {
        // Required empty public constructor
    }

    public static AllBeaconsFragment newInstance() {
        AllBeaconsFragment fragment = new AllBeaconsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
//        setSearchFilter("");
        PubSub.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PubSub.getInstance().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        beaconViewModel = ViewModelProviders.of(this).get(BeaconViewModel.class);
    }

    @Override
    protected void getBeacons(Observer<List<BeaconMinimal>> observer) {
        beaconViewModel.getAll().observe(this, observer);
    }

    @Subscribe
    public void onStatusFilterChanged(StatusFilterEvent event) {
        setStatusFilter(event.getStatus());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);
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
}
