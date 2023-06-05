// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.eventbus;

public class StatusFilterEvent {
    private String status;

    public StatusFilterEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
