package com.example.harsh.quickshare.type;

/**
 * Represents a transfer history, which can be used to store in a database.
 * Created by Harsh on 20-Dec-17.
 */

public class TransferHistory {

    private String fileName;

    private String transferType;

    private String transferResult;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferResult() {
        return transferResult;
    }

    public void setTransferResult(String transferResult) {
        this.transferResult = transferResult;
    }
}
