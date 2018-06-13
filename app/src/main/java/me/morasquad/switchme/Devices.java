package me.morasquad.switchme;

/**
 * Created by Sandun Isuru Niraj on 13/06/2018.
 */

public class Devices {

    public String deviceName, deviceID;

    public Devices() {
    }

    public Devices(String deviceName, String deviceID) {
        this.deviceName = deviceName;
        this.deviceID = deviceID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
