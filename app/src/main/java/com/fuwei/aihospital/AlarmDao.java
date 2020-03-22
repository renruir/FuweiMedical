package com.fuwei.aihospital;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class AlarmDao extends BaseObservable {
    private String color;
    private String callType;
    private String location;
    private String notificationLevel;
    private int callDuration;

    public AlarmDao(String color, String callType, String location, String notificationLevel, int callDuration){
        this.color = color;
        this.callDuration = callDuration;
        this.callType = callType;
        this.notificationLevel =  notificationLevel;
        this.location = location;
    }

    @Bindable
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Bindable
    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    @Bindable
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Bindable
    public String getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(String notificationLevel) {
        this.notificationLevel = notificationLevel;
    }

    @Bindable
    public int getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(int callDuration) {
        this.callDuration = callDuration;
    }
}
