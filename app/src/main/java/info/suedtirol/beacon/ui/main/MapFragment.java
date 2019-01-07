package info.suedtirol.beacon.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import info.suedtirol.beacon.R;

public class MapFragment extends SupportMapFragment {

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (view != null && getContext() != null) {
            view.setBackground(getContext().getDrawable(R.drawable.top_rounded_bg));
        }
        return view;
    }
}
