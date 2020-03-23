package com.fuwei.aihospital;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

public class AlarmDao extends BaseObservable  implements Serializable {
    private String color;
    private String callType;
    private String location;
    private String notificationLevel;
    private String callDuration;

    public AlarmDao() {

    }

    public AlarmDao(String color, String callType, String location, String notificationLevel, String callDuration) {
        this.color = color;
        this.callDuration = secondFormat(callDuration);
        this.callType = callType;
        this.notificationLevel = notificationLevel;
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
    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = secondFormat(callDuration);
    }

    public static String secondFormat(String sec) {
        int second = Integer.valueOf(sec);
        String h = String.valueOf((int) (second / 3600));
        String m = String.valueOf((int) ((second % 3600) / 60));
        String s = String.valueOf((int) (second % 60));
        if (h.length() <= 1) {
            h = "0" + h;
        }

        if (m.length() <= 1) {
            m = "0" + m;
        }

        if (s.length() <= 1) {
            s = "0" + s;
        }
        return h + ":" + m + ":" + s;
    }

}
