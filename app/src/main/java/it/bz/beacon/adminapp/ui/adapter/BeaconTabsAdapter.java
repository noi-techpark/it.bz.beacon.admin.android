package it.bz.beacon.adminapp.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.ui.about.AboutFragment;
import it.bz.beacon.adminapp.ui.issue.IssuesFragment;
import it.bz.beacon.adminapp.ui.main.beacon.AllBeaconsFragment;
import it.bz.beacon.adminapp.ui.main.beacon.NearbyBeaconsFragment;

public class BeaconTabsAdapter extends FragmentPagerAdapter {

    private static int TAB_COUNT = 2;
    private Context context;

    public BeaconTabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position){
            case 0 : return new AllBeaconsFragment();
            case 1 : return new IssuesFragment();
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
