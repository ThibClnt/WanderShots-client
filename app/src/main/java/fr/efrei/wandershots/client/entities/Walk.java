package fr.efrei.wandershots.client.entities;

import java.util.Date;

public class Walk {

    private int walkId;
    private String title;
    private Date startTime;
    private long duration;
    private double distance;

    public Walk() {
    }

    public Walk(int walkId, String title, Date startTime, long duration, double distance) {
        this.walkId = walkId;
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.distance = distance;
    }

    public int getWalkId() {
        return walkId;
    }

    public void setWalkId(int walkId) {
        this.walkId = walkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
