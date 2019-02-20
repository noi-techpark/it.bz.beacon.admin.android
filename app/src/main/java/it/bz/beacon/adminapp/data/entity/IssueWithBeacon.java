package it.bz.beacon.adminapp.data.entity;

import androidx.room.DatabaseView;

@DatabaseView("SELECT BeaconIssue.id, BeaconIssue.problem, BeaconIssue.problemDescription, BeaconIssue.reportDate, " +
        " Beacon.id AS beaconId, Beacon.name, Beacon.batteryLevel, Beacon.lastSeen, Beacon.status FROM BeaconIssue INNER JOIN Beacon ON BeaconIssue.beaconId = Beacon.id")
public class IssueWithBeacon {

    private long id;
    private String problem;
    private String problemDescription;
    private Long reportDate;

    private long beaconId;
    private String name;
    private Integer batteryLevel;
    private Long lastSeen;
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public Long getReportDate() {
        return reportDate;
    }

    public void setReportDate(Long reportDate) {
        this.reportDate = reportDate;
    }

    public long getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(long beaconId) {
        this.beaconId = beaconId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
