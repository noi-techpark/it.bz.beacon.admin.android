// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import it.bz.beacon.adminapp.data.entity.PendingSecureConfig;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.repository.PendingSecureConfigRepository;

public class PendingSecureConfigViewModel extends AndroidViewModel {

    private PendingSecureConfigRepository repository;

    public PendingSecureConfigViewModel(Application application) {
        super(application);
        repository = new PendingSecureConfigRepository(application);
    }

    public void deletePendingSecureConfig(PendingSecureConfig pendingSecureConfig) {
        repository.deletePendingSecureConfig(pendingSecureConfig);
    }

    public List<PendingSecureConfig> getAll() {
        return repository.getAll();
    }

    public void insert(PendingSecureConfig pendingSecureConfig, InsertEvent event) {
        repository.insert(pendingSecureConfig, event);
    }

    public void insert(PendingSecureConfig pendingSecureConfig) {
        repository.insert(pendingSecureConfig, null);
    }
}
