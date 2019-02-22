package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.ui.main.beacon.AllBeaconsFragment;
import it.bz.beacon.adminapp.ui.main.beacon.NearbyBeaconsFragment;

public class BeaconTabsAdapter extends FragmentPagerAdapter {

    private static int TAB_COUNT = 2;
    private Context context;
    private String statusFilter;

    public BeaconTabsAdapter(FragmentManager fm, Context context, String statusFilter) {
        super(fm);
        this.context = context;
        this.statusFilter = statusFilter;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0 : return AllBeaconsFragment.newInstance(statusFilter);
            case 1 : return NearbyBeaconsFragment.newInstance(statusFilter);
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0 :
                return context.getResources().getString(R.string.all);
            case 1 :
                return context.getResources().getString(R.string.nearby);
        }
        return null;
    }
}
