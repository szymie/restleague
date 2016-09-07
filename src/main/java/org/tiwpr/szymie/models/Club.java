package org.tiwpr.szymie.models;

import org.tiwpr.szymie.models.validators.DatePattern;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Club implements Model {

    @DecimalMin("0")
    private int id;

    @NotNull(message = "{club.fullName.null}")
    @Size(min = 1, max = 50, message = "{club.fullName.length}")
    private String fullName;

    @NotNull(message = "{club.nickname.null}")
    @Size(min = 1, max = 50, message = "{club.nickname.length}")
    private String nickname;

    @NotNull(message = "{club.dateOfFounding.null}")
    @DatePattern(regexp = "dd-MM-yyyy", message = "{club.dateOfFounding.pattern}")
    private String dateOfFounding;

    @NotNull(message = "{club.stadium.null}")
    @Size(min = 1, max = 50, message = "{club.stadium.length}")
    private String stadium;

    public Club() {
    }

    public Club(int id, String fullName, String nickname, String dateOfFounding, String stadium) {
        this.id = id;
        this.fullName = fullName;
        this.nickname = nickname;
        this.dateOfFounding = dateOfFounding;
        this.stadium = stadium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDateOfFounding() {
        return dateOfFounding;
    }

    public void setDateOfFounding(String dateOfFounding) {
        this.dateOfFounding = dateOfFounding;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
}
