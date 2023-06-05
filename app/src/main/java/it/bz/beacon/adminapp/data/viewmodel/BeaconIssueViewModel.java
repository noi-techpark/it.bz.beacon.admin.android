// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import it.bz.beacon.adminapp.data.entity.BeaconIssue;
import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.event.LoadIssueEvent;
import it.bz.beacon.adminapp.data.repository.BeaconIssueRepository;

public class BeaconIssueViewModel extends AndroidViewModel {

    private BeaconIssueRepository repository;

    public BeaconIssueViewModel(Application application) {
        super(application);
        repository = new BeaconIssueRepository(application);
    }

    public LiveData<List<BeaconIssue>> getAll(String beaconId) {
        return repository.getAll(beaconId);
    }

    public LiveData<List<IssueWithBeacon>> getAllIssuesWithBeacon() {
        return repository.getAllIssuesWithBeacon();
    }

    public void getIssueWithBeaconById(Long id, LoadIssueEvent loadEvent) {
        repository.getIssueWithBeaconById(id, loadEvent);
    }

    public void insert(BeaconIssue beaconIssue, InsertEvent event) {
        repository.insert(beaconIssue, event);
    }

    public void insert(BeaconIssue beaconIssue) {
        repository.insert(beaconIssue, null);
    }
}
