package com.example.harsh.quickshare.info;

import com.example.harsh.quickshare.constants.TransferType;
import com.example.harsh.quickshare.type.TransferHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores all transfer history.
 * Created by Harsh on 20-Dec-17.
 */

public class TransferHistoryInfo {

    private List<TransferHistory> transferHistoryList = new ArrayList<>();

    /**
     * Adds a download to the list of transfer history
     *
     * @param fileName File Name
     * @param result File Transfer Result
     */
    public void addDownloadHistory(String fileName, String result) {
        if (fileName == null || result == null) {
            return;
        }
        TransferHistory transferHistory = new TransferHistory();
        transferHistory.setFileName(fileName);
        transferHistory.setTransferType(TransferType.DOWNLOAD);
        transferHistory.setTransferResult(result);

        transferHistoryList.add(transferHistory);
    }
}
