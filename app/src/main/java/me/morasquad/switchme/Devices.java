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


    public String getDeviceId() {
        return deviceId;
    }

}
