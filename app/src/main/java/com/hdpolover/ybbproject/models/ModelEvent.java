package com.hdpolover.ybbproject.models;

public class ModelEvent {
    String eConfirmStatus, uid, eDateFrom, eDateTo, eDesc, eld, eImage, eLocation,
            eStatus, eTimeFrom, eTimeTo, eTitle,eCategory, eSpeaker, eCreatedOn, eParticipants, eQuota;

    public ModelEvent() {

    }

    public ModelEvent(String eConfirmStatus, String uid, String eDateFrom, String eDateTo, String eDesc, String eld, String eImage, String eLocation, String eStatus, String eTimeFrom, String eTimeTo, String eTitle, String eCategory, String eSpeaker, String eCreatedOn, String eParticipants, String eQuota) {
        this.eConfirmStatus = eConfirmStatus;
        this.uid = uid;
        this.eDateFrom = eDateFrom;
        this.eDateTo = eDateTo;
        this.eDesc = eDesc;
        this.eld = eld;
        this.eImage = eImage;
        this.eLocation = eLocation;
        this.eStatus = eStatus;
        this.eTimeFrom = eTimeFrom;
        this.eTimeTo = eTimeTo;
        this.eTitle = eTitle;
        this.eCategory = eCategory;
        this.eSpeaker = eSpeaker;
        this.eCreatedOn = eCreatedOn;
        this.eParticipants = eParticipants;
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

    public String geteDateFrom() {
        return eDateFrom;
    }

    public void seteDateFrom(String eDateFrom) {
        this.eDateFrom = eDateFrom;
    }

    public String geteDateTo() {
        return eDateTo;
    }

    public void seteDateTo(String eDateTo) {
        this.eDateTo = eDateTo;
    }

    public String geteDesc() {
        return eDesc;
    }

    public void seteDesc(String eDesc) {
        this.eDesc = eDesc;
    }

    public String getEld() {
        return eld;
    }

    public void setEld(String eld) {
        this.eld = eld;
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

    public String geteTimeFrom() {
        return eTimeFrom;
    }

    public void seteTimeFrom(String eTimeFrom) {
        this.eTimeFrom = eTimeFrom;
    }

    public String geteTimeTo() {
        return eTimeTo;
    }

    public void seteTimeTo(String eTimeTo) {
        this.eTimeTo = eTimeTo;
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

    public String geteParticipants() {
        return eParticipants;
    }

    public void seteParticipants(String eParticipants) {
        this.eParticipants = eParticipants;
    }

    public String geteQuota() {
        return eQuota;
    }

    public void seteQuota(String eQuota) {
        this.eQuota = eQuota;
    }
}

