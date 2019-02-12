package it.bz.beacon.adminapp.ui.main.beacon;

import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.bz.beacon.adminapp.AdminApplication;
import it.bz.beacon.adminapp.R;
import it.bz.beacon.adminapp.data.entity.Beacon;
import it.bz.beacon.adminapp.data.entity.BeaconMinimal;
import it.bz.beacon.adminapp.data.event.DataUpdateEvent;
import it.bz.beacon.adminapp.data.repository.BeaconRepository;
import it.bz.beacon.adminapp.eventbus.LogoutEvent;
import it.bz.beacon.adminapp.eventbus.PubSub;
import it.bz.beacon.adminapp.ui.adapter.BeaconAdapter;

public abstract class BaseBeaconsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.beacons_content)
    protected SwipeRefreshLayout swipeBeacons;

    @BindView(R.id.beacons_list)
    protected RecyclerView recyclerBeacons;

    @BindView(R.id.beacons_empty)
    protected TextView txtEmpty;

    private BeaconAdapter adapter;

    protected String statusFilter = Beacon.STATUS_ALL;
    protected String searchFilter = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);
        ButterKnife.bind(this, view);

        adapter = new BeaconAdapter(getContext());
        recyclerBeacons.setAdapter(adapter);
        recyclerBeacons.setHasFixedSize(true);
        recyclerBeacons.setItemAnimator(new DefaultItemAnimator());
        recyclerBeacons.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeBeacons.setOnRefreshListener(this);
        swipeBeacons.setColorSchemeResources(R.color.primary);

        loadData();
        return view;
    }

    private void loadData() {
        showLoading();
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

    protected void setSearchFilter(String filter) {
        Log.d(AdminApplication.LOG_TAG, "filter: " + filter);
        searchFilter = filter.replace('#', ' ');
        adapter.getFilter().filter(statusFilter + "#" + searchFilter);
    }

    protected void setStatusFilter(String filter) {
        Log.d(AdminApplication.LOG_TAG, "filter: " + filter);
        statusFilter = filter.replace('#', ' ');
        adapter.getFilter().filter(statusFilter + "#" + searchFilter);
    }

    private void showLoading() {
        txtEmpty.setVisibility(View.GONE);
        recyclerBeacons.setVisibility(View.GONE);
        swipeBeacons.setRefreshing(true);
    }

    private void showList() {
        txtEmpty.setVisibility(View.GONE);
        recyclerBeacons.setVisibility(View.VISIBLE);
        swipeBeacons.setRefreshing(false);
    }

    private void showNoData() {
        recyclerBeacons.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
        swipeBeacons.setRefreshing(false);
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
        BeaconRepository beaconRepository = new BeaconRepository(getContext());
        beaconRepository.refreshBeacons(new DataUpdateEvent() {
            @Override
            public void onSuccess() {
                Log.i(AdminApplication.LOG_TAG, "Beacons refreshed!");
            }

            @Override
            public void onError() {
                Log.e(AdminApplication.LOG_TAG, "Error refreshing beacons");
                swipeBeacons.setRefreshing(false);
            }

            @Override
            public void onAuthenticationFailed() {
                Log.e(AdminApplication.LOG_TAG, "Authentication failed");
                swipeBeacons.setRefreshing(false);
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
                .setTitle(getString(R.string.status_error))
                .setMessage(getString(R.string.error_authorization))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        PubSub.getInstance().post(new LogoutEvent());
                    }
                })
                .create();
        dialog.show();
    }
}
