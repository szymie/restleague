package org.tiwpr.szymie.models;

import org.tiwpr.szymie.models.validators.DatePattern;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Season implements Model {

    @DecimalMin("0")
    private int id;

    @NotNull(message = "{season.name.null}")
    @Size(min = 1, max = 50, message = "{season.name.length}")
    private String name;

    @NotNull(message = "{season.startDate.null}")
    @DatePattern(regexp = "dd-MM-yyyy", message = "{season.startDate.pattern}")
    private String startDate;

    @NotNull(message = "{season.endDate.null}")
    @DatePattern(regexp = "dd-MM-yyyy", message = "{season.endDate.pattern}")
    private String endDate;

    @NotNull(message = "{season.status.null}")
    @Pattern(regexp = "in progress|completed", message = "{player.status.pattern}")
    private String status;

    public Season() {
    }

    public Season(int id, String name, String startDate, String endDate, String status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
