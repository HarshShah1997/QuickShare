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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferRequest that = (TransferRequest) o;

        if (!deviceFile.equals(that.deviceFile)) return false;
        if (!fromIPAddress.equals(that.fromIPAddress)) return false;
        if (!toIPAddress.equals(that.toIPAddress)) return false;
        if (!startOffset.equals(that.startOffset)) return false;
        return size.equals(that.size);

    }

    @Override
    public int hashCode() {
        int result = deviceFile.hashCode();
        result = 31 * result + fromIPAddress.hashCode();
        result = 31 * result + toIPAddress.hashCode();
        result = 31 * result + startOffset.hashCode();
        result = 31 * result + size.hashCode();
        return result;
    }
}
