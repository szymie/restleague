package org.tiwpr.szymie.models;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class ClubId {

    @DecimalMin("0")
    @NotNull(message = "{clubId.null}")
    private int clubId;

    public ClubId() {
    }

    public ClubId(int clubId) {
        this.clubId = clubId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
}
