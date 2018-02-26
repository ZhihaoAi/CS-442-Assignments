package com.ai.zhihao.hw3;

import java.io.Serializable;

public class Note implements Serializable{

    private String title;
    private String time;
    private String content;

    public Note() {
        this.title = null;
        this.time = null;
        this.content = null;
    }

    public Note(String title, String time, String content) {
        this.title = title;
        this.time = time;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
