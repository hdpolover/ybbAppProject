package com.hdpolover.ybbproject.models;

public class ModelEvent {
    String eConfirmStatus, uid, eStart, eEnd, eDesc, eId, eImage, eLocation,
            eStatus, eTitle, eCategory, eSpeaker, eCreatedOn, eQuota;

    public ModelEvent() {

    }

    public ModelEvent(String eConfirmStatus, String uid, String eStart, String eEnd, String eDesc, String eId, String eImage, String eLocation, String eStatus, String eTitle, String eCategory, String eSpeaker, String eCreatedOn, String eQuota) {
        this.eConfirmStatus = eConfirmStatus;
        this.uid = uid;
        this.eStart = eStart;
        this.eEnd = eEnd;
        this.eDesc = eDesc;
        this.eId = eId;
        this.eImage = eImage;
        this.eLocation = eLocation;
        this.eStatus = eStatus;
        this.eTitle = eTitle;
        this.eCategory = eCategory;
        this.eSpeaker = eSpeaker;
        this.eCreatedOn = eCreatedOn;
        this.eQuota = eQuota;
    }

    public String geteConfirmStatus() {
        return eConfirmStatus;
    }

    public void seteConfirmStatus(String eConfirmStatus) {
        this.eConfirmStatus = eConfirmStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String geteStart() {
        return eStart;
    }

    public void seteStart(String eStart) {
        this.eStart = eStart;
    }

    public String geteEnd() {
        return eEnd;
    }

    public void seteEnd(String eEnd) {
        this.eEnd = eEnd;
    }

    public String geteDesc() {
        return eDesc;
    }

    public void seteDesc(String eDesc) {
        this.eDesc = eDesc;
    }

    public String geteId() {
        return eId;
    }

    public void seteId(String eId) {
        this.eId = eId;
    }

    public String geteImage() {
        return eImage;
    }

    public void seteImage(String eImage) {
        this.eImage = eImage;
    }

    public String geteLocation() {
        return eLocation;
    }

    public void seteLocation(String eLocation) {
        this.eLocation = eLocation;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public String geteTitle() {
        return eTitle;
    }

    public void seteTitle(String eTitle) {
        this.eTitle = eTitle;
    }

    public String geteCategory() {
        return eCategory;
    }

    public void seteCategory(String eCategory) {
        this.eCategory = eCategory;
    }

    public String geteSpeaker() {
        return eSpeaker;
    }

    public void seteSpeaker(String eSpeaker) {
        this.eSpeaker = eSpeaker;
    }

    public String geteCreatedOn() {
        return eCreatedOn;
    }

    public void seteCreatedOn(String eCreatedOn) {
        this.eCreatedOn = eCreatedOn;
    }

    public String geteQuota() {
        return eQuota;
    }

    public void seteQuota(String eQuota) {
        this.eQuota = eQuota;
    }
}

