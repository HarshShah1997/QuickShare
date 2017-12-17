package com.example.harsh.quickshare.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a file in the device.
 * Created by Harsh on 17-Dec-17.
 */

public class DeviceFile {

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String path;

    private List<DeviceFile> children = new ArrayList<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<DeviceFile> getChildren() {
        return children;
    }

    public void setChildren(List<DeviceFile> children) {
        this.children = children;
    }
}
