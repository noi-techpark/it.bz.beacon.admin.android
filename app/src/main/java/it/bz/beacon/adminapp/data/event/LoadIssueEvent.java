// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.IssueWithBeacon;

public interface LoadIssueEvent {
    void onSuccess(IssueWithBeacon issueWithBeacon);

    void onError();
}
