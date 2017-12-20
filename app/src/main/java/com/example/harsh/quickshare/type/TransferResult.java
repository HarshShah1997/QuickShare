package com.example.harsh.quickshare.type;

/**
 * Describe a result of transfer request.
 * Created by Harsh on 19-Dec-17.
 */

public class TransferResult {

    private TransferRequest transferRequest;

    private String transferStatus;

    public TransferRequest getTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(TransferRequest transferRequest) {
        this.transferRequest = transferRequest;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }
}
