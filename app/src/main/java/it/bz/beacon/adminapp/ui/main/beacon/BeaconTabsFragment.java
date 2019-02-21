package it.bz.beacon.adminapp.ui.main.beacon;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
}
