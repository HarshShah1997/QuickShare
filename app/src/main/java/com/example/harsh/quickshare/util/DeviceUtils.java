package com.example.harsh.quickshare.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.harsh.quickshare.constants.DeviceFileType;
import com.example.harsh.quickshare.type.DeviceFile;

import java.io.File;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Contains utilities to obtain device specific information.
 * Created by Harsh on 17-Dec-17.
 */

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";

    /**
     * Populates the list of shared files from the device based on preferences
     *
     * @param context Context to obtain preferences
     * @return List of files present in the device
     */
    public List<DeviceFile> getFilesFromDevice(Context context) {
        List<DeviceFile> filesList = new ArrayList<>();
        // TODO: Get directory based on preferences
        File directory = new File(Environment.getExternalStorageDirectory(), "/Music/");

        DeviceFile fileDirectory = new DeviceFile();
        fileDirectory.setFileName(directory.getName());
        fileDirectory.setPath(directory.getAbsolutePath());
        fileDirectory.setFileType(DeviceFileType.DIRECTORY);
        fileDirectory.setChildren(getFilesFromDirectory(directory));
        filesList.add(fileDirectory);

        return filesList;
    }

    // Recursively gets files from a directory, sets the children if its a directory
    private List<DeviceFile> getFilesFromDirectory(File directory) {
        List<DeviceFile> filesList = new ArrayList<>();
        if (directory != null) {
            File[] files = directory.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                DeviceFile deviceFile = new DeviceFile();
                deviceFile.setFileName(files[i].getName());
                deviceFile.setFileSize(files[i].length());
                deviceFile.setPath(files[i].getAbsolutePath());

                if (files[i].isDirectory()) {
                    deviceFile.setFileType(DeviceFileType.DIRECTORY);
                    deviceFile.setChildren(getFilesFromDirectory(files[i]));
                } else {
                    deviceFile.setFileType(DeviceFileType.FILE);
                }
                filesList.add(deviceFile);
            }
        }
        return filesList;
    }

    /**
     * Returns the device's current IP Address
     *
     * @param context Context
     * @return IP Address in form of a String
     */
    public String getDeviceIPAddress(Context context) {
        String found_ip_address = "";
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
            while (niEnum.hasMoreElements()) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        found_ip_address = interfaceAddress.getAddress().getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
        }
        return found_ip_address;
    }
}
