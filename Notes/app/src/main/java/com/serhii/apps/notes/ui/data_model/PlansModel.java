package com.serhii.apps.notes.ui.data_model;

public class PlansModel {

    private String title = "";
    private String time = "";

    public PlansModel(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
