package com.github.rahmnathan.localmovie.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AndroidPushClient {

    @Id
    private String deviceId;
    private String pushToken;

    public AndroidPushClient() {
    }

    public AndroidPushClient(String deviceId, String pushToken) {
        this.deviceId = deviceId;
        this.pushToken = pushToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
}