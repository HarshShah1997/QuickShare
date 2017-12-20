package com.example.harsh.quickshare.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.harsh.quickshare.activity.MainActivity;
import com.example.harsh.quickshare.constants.FileTransferStatus;
import com.example.harsh.quickshare.type.TransferRequest;
import com.example.harsh.quickshare.type.TransferResult;
import com.example.harsh.quickshare.util.FileTransferUtils;

import java.io.IOException;

/**
 * An async task to download the file.
 *
 * Created by Harsh on 19-Dec-17.
 */

public class DownloadTask extends AsyncTask<Void, Integer, TransferResult> {

    private static final String TAG = "DownloadTask";

    private MainActivity mActivity;
    private TransferRequest transferRequest;
    private FileTransferUtils fileTransferUtils;

    /**
     * Initializes the download task.
     *
     * @param activity Activity to be used for callback
     * @param transferRequest TransferRequest
     * @param fileTransferUtils FileTransferUtils used for receiving a file
     */
    public DownloadTask(MainActivity activity, TransferRequest transferRequest, FileTransferUtils fileTransferUtils) {
        mActivity = activity;
        this.transferRequest = transferRequest;
        this.fileTransferUtils = fileTransferUtils;
    }

    @Override
    protected void onPreExecute() {
        mActivity.partDownloadStarted(transferRequest);
    }

    @Override
    protected TransferResult doInBackground(Void... params) {
        String status;
        try {
            fileTransferUtils.receiveFile(transferRequest);
            status = FileTransferStatus.SUCCESS;
        } catch (IOException e) {
            status = FileTransferStatus.FAILED;
            Log.e(TAG, e.getMessage());
        }
        TransferResult transferResult = new TransferResult();
        transferResult.setTransferRequest(transferRequest);
        transferResult.setTransferStatus(status);
        return transferResult;
    }

    @Override
    protected void onPostExecute(TransferResult transferResult) {
        mActivity.partDownloadFinished(transferResult);
    }
}
