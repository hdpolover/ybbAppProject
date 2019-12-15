package com.hdpolover.ybbproject.models;

public class ModelEvent {
    String uid, imgEvent, titleEvent, dateEventFrom, timeEventFrom, dateEventTo, timeEventTo, catEvent, loctEvent, descEvent;

    public ModelEvent(String uid, String imgEvent,String titleEvent, String dateEventFrom, String timeEventFrom,
                       String dateEventTo, String timeEventTo, String catEvent, String loctEvent, String descEvent){
        this.uid = uid;
        this.imgEvent = imgEvent;
        this.dateEventFrom = dateEventFrom;
        this.timeEventFrom = timeEventFrom;
        this.dateEventTo = dateEventTo;
        this.timeEventTo = timeEventTo;
        this.catEvent = catEvent;
        this.loctEvent = loctEvent;
        this.descEvent = descEvent;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getImgEvent() {
        return imgEvent;
    }

    public void setImgEvent(String imgEvent) {
        this.imgEvent = imgEvent;
    }

    public String getTitleEvent() {
        return titleEvent;
    }

    public void setTitleEvent(String titleEvent) {
        this.titleEvent = titleEvent;
    }

    public String getDateEventFrom() {
        return dateEventFrom;
    }

    public void setDateEventFrom(String dateEventFrom) {
        this.dateEventFrom = dateEventFrom;
    }

    public String getTimeEventFrom() {
        return timeEventFrom;
    }

    public void setTimeEventFrom(String timeEventFrom) {
        this.timeEventFrom = timeEventFrom;
    }

    public String getDateEventTo() {
        return dateEventTo;
    }

    public void setDateEventTo(String dateEventTo) {
        this.dateEventFrom = dateEventTo;
    }

    public String getTimeEventTo() {
        return timeEventTo;
    }

    public void setTimeEventTo(String timeEventTo) {
        this.timeEventTo = timeEventTo;
    }

    public String getCatEvent() {
        return catEvent;
    }

    public void setCatEvent(String catEvent) {
        this.catEvent = catEvent;
    }

    public String getLoctEvent() {
        return loctEvent;
    }

    public void setLoctEvent(String loctEvent) {
        this.loctEvent = loctEvent;
    }

    public String getDescEvent() {
        return descEvent;
    }

    public void setDescEvent(String descEvent) {
        this.descEvent = descEvent;
    }
}
