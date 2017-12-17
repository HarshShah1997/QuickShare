package com.example.harsh.quickshare.type;

import java.util.List;

/**
 * Represents an android device.
 * Created by Harsh on 16-Dec-17.
 */

public class Device {

    private String deviceName;

    private String deviceIPAddress;

    private List<DeviceFile> deviceFiles;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIPAddress() {
        return deviceIPAddress;
    }

    public void setDeviceIPAddress(String deviceIPAddress) {
        this.deviceIPAddress = deviceIPAddress;
    }

    public List<DeviceFile> getDeviceFiles() {
        return deviceFiles;
    }

    public void setDeviceFiles(List<DeviceFile> deviceFiles) {
        this.deviceFiles = deviceFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return getDeviceIPAddress().equals(device.getDeviceIPAddress());

    }

    @Override
    public int hashCode() {
        return getDeviceIPAddress().hashCode();
    }
}
