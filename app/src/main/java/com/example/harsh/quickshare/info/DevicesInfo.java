package com.example.harsh.quickshare.info;

import com.example.harsh.quickshare.constants.DeviceFileType;
import com.example.harsh.quickshare.type.Device;
import com.example.harsh.quickshare.type.DeviceFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores the information of devices and their files present in the network.
 * Created by Harsh on 16-Dec-17.
 */

public class DevicesInfo {

    private Set<Device> deviceList = new HashSet<>();

    private Map<DeviceFile, String> fileLocation = new HashMap<>();

    private Map<String, List<DeviceFile>> nodesContent = new HashMap<>();

    /**
     * Adds a device to the list of present devices.
     * Updates device's information if it is already present.
     *
     * @param device The device to be added
     */
    public void addDevice(Device device) {
        if (device != null) {
            if (deviceList.contains(device)) {
                removeDevice(device);
            }
            deviceList.add(device);
            nodesContent.put(device.getDeviceIPAddress(), new ArrayList<DeviceFile>());
            addDeviceFiles(device.getDeviceFiles(), device.getDeviceIPAddress());
        }
    }

    // Stores the device corresponding to a file
    private void addDeviceFiles(List<DeviceFile> deviceFiles, String node) {
        for (DeviceFile deviceFile : deviceFiles) {
            if (deviceFile.getFileType().equals(DeviceFileType.DIRECTORY)) {
                addDeviceFiles(deviceFile.getChildren(), node);
            } else {
                fileLocation.put(deviceFile, node);
                nodesContent.get(node).add(deviceFile);
            }
        }
    }

    /**
     * Removes the device and its corresponding file.
     *
     * @param device The device to be removed
     */
    public void removeDevice(Device device) {
        if (device != null) {
            deviceList.remove(device);
            removeNode(device.getDeviceIPAddress());
        }
    }

    /**
     * Retrieves all the devices containing the file
     *
     * @param file The given file
     * @return List of IP Addresses containing file
     */
    // TODO: Add multiple devices functionality
    public List<String> getDeviceContainingFile(DeviceFile file) {
        List<String> devices = new ArrayList<>();
        if (file != null && fileLocation.containsKey(file)) {
            devices.add(fileLocation.get(file));
        }
        return devices;
    }

    // Removes a device and all the files associated with it
    private void removeNode(String node) {
        for (DeviceFile deviceFile : nodesContent.get(node)) {
            fileLocation.remove(deviceFile);
        }
        nodesContent.remove(node);
    }
}
