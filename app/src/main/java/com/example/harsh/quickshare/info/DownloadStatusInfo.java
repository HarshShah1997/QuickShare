package com.example.harsh.quickshare.info;

import com.example.harsh.quickshare.constants.TransferStatus;
import com.example.harsh.quickshare.type.DeviceFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the file downloading status.
 * Created by Harsh on 20-Dec-17.
 */

public class DownloadStatusInfo {

    /**
     * Represents no of parts of a file which will be downloaded concurrently
     */
    private Map<DeviceFile, Integer> fileParts = new HashMap<>();

    private Map<DeviceFile, List<String>> fileStatuses = new HashMap<>();

    /**
     * Adds a file to the list of currently downloading files.
     *
     * @param deviceFile File
     * @param parts No of parts of file that will be downloaded concurrently
     */
    public void addFile(DeviceFile deviceFile, Integer parts) {
        if (deviceFile == null || parts == null || parts <= 0) {
            return;
        }
        fileParts.put(deviceFile, parts);
        fileStatuses.put(deviceFile, new ArrayList<String>());
    }

    /**
     * Stores the result of one of the parts of file downloading.
     *
     * @param deviceFile File
     * @param result Result, must a one of the TransferStatus
     */
    public synchronized void storeResult(DeviceFile deviceFile, String result) {
        if (deviceFile == null || result == null) {
            return;
        }
        if (fileStatuses.get(deviceFile) == null) {
            return;
        }
        fileStatuses.get(deviceFile).add(result);
    }

    /**
     * Checks whether all parts of file has completed downloading
     *
     * @param deviceFile File
     * @return True if all parts have been completed
     */
    public boolean isComplete(DeviceFile deviceFile) {
        if (deviceFile == null) {
            return false;
        }
        if (fileStatuses.get(deviceFile) == null || fileParts.get(deviceFile) == null) {
            return false;
        }
        return fileStatuses.get(deviceFile).size() == fileParts.get(deviceFile);
    }

    /**
     * Removes the file from the list of currently downloading files
     *
     * @param deviceFile File
     */
    public void removeFile(DeviceFile deviceFile) {
        if (deviceFile == null) {
            return;
        }
        fileParts.remove(deviceFile);
        fileStatuses.remove(deviceFile);
    }

    /**
     * This method determines the result of a file downloading task
     *
     * @param deviceFile File
     * @return String - A type of file transfer status.
     */
    public String getResult(DeviceFile deviceFile) {
        if (deviceFile == null) {
            return "";
        }
        List<String> deviceFileStatuses = fileStatuses.get(deviceFile);
        if (deviceFileStatuses == null || deviceFileStatuses.size() == 0) {
            return "";
        }
        for (String status : deviceFileStatuses) {
            if (status.equals(TransferStatus.FAILED)) {
                return TransferStatus.FAILED;
            }
        }
        return TransferStatus.SUCCESS;
    }
}
