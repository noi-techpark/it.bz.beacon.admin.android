/*
 * com.suedtirol.android
 *
 * Created: Oct 27, 2017
 *
 * Copyright (C) 2017 Raiffeisen Online GmbH (Jürgen Sprenger, Aaron Falk) <info@raiffeisen.it>
 */

package it.bz.beacon.adminapp.ui.main.map;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import it.bz.beacon.adminapp.R;

public class BeaconInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View infoWindow = null;
    private LayoutInflater inflater;

    public BeaconInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getTitle() == null && marker.getSnippet() == null) {
            return null;
        }

        if (infoWindow == null) {
            infoWindow = inflater.inflate(R.layout.info_window_map, null);
        }

        TextView titleView = infoWindow.findViewById(R.id.info_window_title);
        TextView snippetView = infoWindow.findViewById(R.id.info_window_snippet);

        titleView.setText(marker.getTitle());
        snippetView.setText(marker.getSnippet());

        if (marker.getSnippet() == null || marker.getSnippet().equals("")) {
            snippetView.setVisibility(View.GONE);
        }

        return infoWindow;
    }
}
