package it.bz.beacon.adminapp.ui.main.beacon;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.ui.adapter.BeaconTabsAdapter;

public class BeaconTabsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public BeaconTabsFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment BeaconTabsFragment.
     */
    public static BeaconTabsFragment newInstance() {
        BeaconTabsFragment fragment = new BeaconTabsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tabView = inflater.inflate(R.layout.fragment_beacon_tabs, container, false);
        tabLayout = tabView.findViewById(R.id.tabs);
        viewPager = tabView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new BeaconTabsAdapter(getChildFragmentManager(), getActivity()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        return tabView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.beacons, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }
}
