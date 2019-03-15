package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;

public interface LoadIssueEvent {
    void onSuccess(IssueWithBeacon issueWithBeacon);

    void onError();
}
