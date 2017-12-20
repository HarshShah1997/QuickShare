package com.example.harsh.quickshare.util;

import android.os.Environment;
import android.util.Log;

import com.example.harsh.quickshare.type.TransferRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Contains utilities for transferring files between devices
 * Created by Harsh on 18-Dec-17.
 */

public class FileTransferUtils {

    private static final int FILE_TRANSFER_PORT = 5003;
    private static final String READ_MODE = "r";
    private static final String WRITE_MODE = "rw";
    private static final int BUFFER_SIZE = 8192;
    private static final String DOWNLOAD_DIRECTORY = "/P2PDownload/";
    private static final String TAG = "FileTransferUtils";

    private ServerSocket mServerSocket;

    public FileTransferUtils() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(FILE_TRANSFER_PORT);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Returns the receiving server socket.
     *
     * @return Serversocket from which files can be received
     */
    public ServerSocket getReceivingSocket() {
        return mServerSocket;
    }

    public String getWriteMode() {
        return WRITE_MODE;
    }

    public int getBufferSize() {
        return BUFFER_SIZE;
    }

    public String getDownloadDirectory() {
        return DOWNLOAD_DIRECTORY;
    }

    /**
     * Sends the part of file starting from offset to the given toIPAddress of transfer request.
     * Amount of file part to be sent is specified by size
     *
     * @param transferRequest TransferRequest
     * @throws IOException This exception is thrown if there is any failure in transfer of the file
     */
    public void sendFile(TransferRequest transferRequest) throws IOException {
        if (transferRequest == null) {
            return;
        }
        Socket socket = null;
        OutputStream outgoingStream = null;
        RandomAccessFile file = null;

        try {
            socket = new Socket(transferRequest.getToIPAddress(), FILE_TRANSFER_PORT);
            Log.d(TAG, "Socket opened");
            outgoingStream = socket.getOutputStream();

            file = new RandomAccessFile(transferRequest.getDeviceFile().getPath(), READ_MODE);
            file.seek(transferRequest.getStartOffset());

            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesRead = 0;
            while (bytesRead < transferRequest.getSize()) {
                int bytesToRead = getBytesToRead(BUFFER_SIZE, transferRequest.getSize() - bytesRead);
                int length = file.read(buffer, 0, bytesToRead);
                bytesRead += length;
                outgoingStream.write(buffer, 0, length);
            }
            Log.d(TAG, "Bytes Sent:" + bytesRead);
        } finally {
            closeFile(file);
            closeOutputStream(outgoingStream);
            closeSocket(socket);
        }
    }

    /**
     * Starts procedure for receiving parts of file, file is stored as specified by download directory
     *
     * @param transferRequest Transfer Request
     * @throws IOException This exception is thrown if there is any failure in transfer of the file
     */
    public void receiveFile(TransferRequest transferRequest) throws IOException {
        if (transferRequest == null || mServerSocket == null) {
            return;
        }
        RandomAccessFile file = null;
        Socket clientSocket = null;
        InputStream incomingStream = null;

        try {
            File directory = new File(Environment.getExternalStorageDirectory(), DOWNLOAD_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdir();
            }
            file = new RandomAccessFile(Environment.getExternalStorageDirectory() +
                    DOWNLOAD_DIRECTORY + transferRequest.getDeviceFile().getFileName(), WRITE_MODE);
            FileChannel fileChannel = file.getChannel();
            fileChannel.position(transferRequest.getStartOffset());

            clientSocket = mServerSocket.accept();
            Log.d(TAG, "Client accepted");

            incomingStream = clientSocket.getInputStream();
            long bytesRead = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while (bytesRead < transferRequest.getSize()) {
                int length = incomingStream.read(buffer);
                bytesRead += length;
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, length);
                while (byteBuffer.hasRemaining()) {
                    fileChannel.write(byteBuffer);
                }
            }
            Log.d(TAG, "Bytes written:" + bytesRead);
        } finally {
            closeInputStream(incomingStream);
            closeFile(file);
            closeSocket(clientSocket);
        }
    }

    private int getBytesToRead(int bufferSize, long remaining) {
        if (remaining > bufferSize) {
            return bufferSize;
        } else {
            return (int) remaining;
        }
    }

    public void closeInputStream(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void closeOutputStream(OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

    public void closeFile(RandomAccessFile file) throws IOException {
        if (file != null) {
            file.close();
        }
    }

    public void closeSocket(Socket socket) throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}
