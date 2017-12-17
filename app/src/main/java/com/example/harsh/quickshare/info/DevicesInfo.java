package com.example.harsh.quickshare.info;

import com.example.harsh.quickshare.type.Device;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores the information of devices present in the network.
 * Created by Harsh on 16-Dec-17.
 */

public class DevicesInfo {

    private Set<Device> deviceList = new HashSet<>();

    /**
     * Adds a device to the list of present devices.
     * Updates device's information if it is already present.
     *
     * @param device - The device to be added
     */
    public void addDevice(Device device) {
        if (device != null) {
            if (deviceList.contains(device)) {
                deviceList.remove(device);
            }
            deviceList.add(device);
        }
    }
}
