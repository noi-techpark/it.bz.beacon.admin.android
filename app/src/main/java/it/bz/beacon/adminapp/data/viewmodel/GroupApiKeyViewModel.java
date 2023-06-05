// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.GroupApiKey;
import it.bz.beacon.adminapp.data.event.LoadGroupApiKeyEvent;
import it.bz.beacon.adminapp.data.repository.GroupApiKeyRepository;

public class GroupApiKeyViewModel extends AndroidViewModel {

    private GroupApiKeyRepository repository;

    public void getByGroupId(Long groupId, LoadGroupApiKeyEvent loadEvent) {
        repository.getByGroupId(groupId, loadEvent);
    }

    public GroupApiKeyViewModel(Application application) {
        super(application);
        repository = new GroupApiKeyRepository(application);
    }

    public LiveData<List<GroupApiKey>> getAll() {
        return repository.getAll();
    }
}
