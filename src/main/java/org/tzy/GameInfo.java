package org.tzy;

public class GameInfo {

    private String username;

    private String startTime;

    private String endTime;

    private int scores;

    public GameInfo(String username, String startTime) {
        this.username = username;
        this.startTime = startTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }
}
