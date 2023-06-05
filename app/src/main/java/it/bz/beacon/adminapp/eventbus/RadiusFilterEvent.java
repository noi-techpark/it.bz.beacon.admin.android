// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.adminapp.eventbus;

public class RadiusFilterEvent {
    private int radius;

    public RadiusFilterEvent(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}
