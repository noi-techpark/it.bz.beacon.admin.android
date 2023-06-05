// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.bz.beacon.adminapp.data.entity.Group;
import it.bz.beacon.adminapp.data.event.InsertEvent;
import it.bz.beacon.adminapp.data.repository.GroupRepository;

public class GroupViewModel extends AndroidViewModel {

    private GroupRepository repository;

    public GroupViewModel(Application application) {
        super(application);
        repository = new GroupRepository(application);
    }

    public LiveData<List<Group>> getAll() {
        return repository.getAll();
    }

    public void insert(Group group, InsertEvent event) {
        repository.insert(group, event);
    }

    public void insert(Group group) {
        repository.insert(group, null);
    }
}
