// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.ui.map;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import it.bz.beacon.adminapp.R;

public class LocationDisabledFragment extends Fragment {

    protected OnRetryLoadMapListener listener;

    public static LocationDisabledFragment newInstance(OnRetryLoadMapListener listener) {
        LocationDisabledFragment fragment = new LocationDisabledFragment();
        fragment.setListener(listener);
        return fragment;
    }

    private void setListener(OnRetryLoadMapListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_disabled, container, false);

        if (view != null) {
            ButterKnife.bind(this, view);
        }

        return view;
    }

    @OnClick(R.id.reload)
    public void onReload() {
        if (listener != null) {
            listener.onRetry();
        }
    }
}
