package com.example.harsh.quickshare.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.harsh.quickshare.activity.MainActivity;
import com.example.harsh.quickshare.constants.FileTransferStatus;
import com.example.harsh.quickshare.type.TransferRequest;
import com.example.harsh.quickshare.type.TransferResult;
import com.example.harsh.quickshare.util.FileTransferUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
            receiveFile(transferRequest);
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        mActivity.updatePartProgress(transferRequest, values[0]);
    }

    /**
     * Starts procedure for receiving parts of file, file is stored as specified by download directory
     *
     * @param transferRequest Transfer Request
     * @throws IOException This exception is thrown if there is any failure in transfer of the file
     */
    private void receiveFile(TransferRequest transferRequest) throws IOException {
        if (transferRequest == null || fileTransferUtils.getReceivingSocket() == null) {
            return;
        }
        RandomAccessFile file = null;
        Socket clientSocket = null;
        InputStream incomingStream = null;

        try {
            File directory = new File(Environment.getExternalStorageDirectory(), fileTransferUtils.getDownloadDirectory());
            if (!directory.exists()) {
                directory.mkdir();
            }
            file = new RandomAccessFile(Environment.getExternalStorageDirectory() +
                    fileTransferUtils.getDownloadDirectory() + transferRequest.getDeviceFile().getFileName(),
                    fileTransferUtils.getWriteMode());
            FileChannel fileChannel = file.getChannel();
            fileChannel.position(transferRequest.getStartOffset());

            clientSocket = fileTransferUtils.getReceivingSocket().accept();
            Log.d(TAG, "Client accepted");

            incomingStream = clientSocket.getInputStream();
            long bytesRead = 0;
            byte[] buffer = new byte[fileTransferUtils.getBufferSize()];
            while (bytesRead < transferRequest.getSize()) {
                int length = incomingStream.read(buffer);
                bytesRead += length;
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, length);
                while (byteBuffer.hasRemaining()) {
                    fileChannel.write(byteBuffer);
                }
                publishProgress((int)(((float)bytesRead / transferRequest.getSize()) * 100));
            }
            Log.d(TAG, "Bytes written:" + bytesRead);
        } finally {
            fileTransferUtils.closeInputStream(incomingStream);
            fileTransferUtils.closeFile(file);
            fileTransferUtils.closeSocket(clientSocket);
        }
    }
}
