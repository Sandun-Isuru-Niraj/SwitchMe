package me.morasquad.switchme;

/**
 * Created by Sandun Isuru Niraj on 13/06/2018.
 */

public class Devices {

    public String deviceName, deviceId;

    public Devices() {
    }

    public Devices(String deviceName, String deviceId) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
