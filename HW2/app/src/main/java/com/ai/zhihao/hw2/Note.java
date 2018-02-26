package com.ai.zhihao.hw2;

/**
 * Created by Zhihao Ai on 1/26/18.
 */

public class Note {

    private String lastUpdateTime;
    private String notes;

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String toString() {
        return "Last Update: " + lastUpdateTime + "\n" + notes;
    }

}
