// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.data.event;

import it.bz.beacon.adminapp.data.entity.Beacon;

public interface LoadBeaconEvent {
    void onSuccess(Beacon beacon);

    void onError();
}
