package com.example.harsh.quickshare.type;

/**
 * Contains information necessary to send a file from one device to another
 * Created by Harsh on 17-Dec-17.
 */

public class TransferRequest {

    private DeviceFile deviceFile;

    private String fromIPAddress;

    private String toIPAddress;

    private Long startOffset;

    private Long size;

    public DeviceFile getDeviceFile() {
        return deviceFile;
    }

    public void setDeviceFile(DeviceFile deviceFile) {
        this.deviceFile = deviceFile;
    }

    public String getFromIPAddress() {
        return fromIPAddress;
    }

    public void setFromIPAddress(String fromIPAddress) {
        this.fromIPAddress = fromIPAddress;
    }

    public String getToIPAddress() {
        return toIPAddress;
    }

    public void setToIPAddress(String toIPAddress) {
        this.toIPAddress = toIPAddress;
    }

    public Long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Long startOffset) {
        this.startOffset = startOffset;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
