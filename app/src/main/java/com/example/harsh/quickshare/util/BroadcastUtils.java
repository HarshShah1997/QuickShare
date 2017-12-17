package com.example.harsh.quickshare.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.example.harsh.quickshare.exception.BroadcastAddressException;

/**
 * Manages the sending and receiving of broadcasts
 * Created by Harsh on 16-Dec-17.
 */

public class BroadcastUtils {

    private static final int BROADCAST_PORT = 5001;
    private static final int BROADCAST_BUFF_SIZE = 4096 * 16;

    private static final String TAG = "BroadcastUtils";

    private IncomingPacketListener mIncomingPacketListener;
    private InetAddress broadcastAddress;

    /**
     * Must provide context to listen to incoming packets
     *
     * @param context - Context
     */
    public BroadcastUtils(Context context) {
        if (context instanceof IncomingPacketListener) {
            mIncomingPacketListener = (IncomingPacketListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement IncomingPacketListener");
        }
        acquireMultiCastLock(context);
        acquireBroadcastAddress();
    }

    /**
     * Prepares the application to receive broadcast. Packet listener is called upon
     * successful reception of packet.
     */
    public void startReceivingBroadcast() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"));
                    socket.setBroadcast(true);
                    while (true) {
                        byte[] receiveBuffer = new byte[BROADCAST_BUFF_SIZE];
                        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(packet);
                        String hostAddress = packet.getAddress().getHostAddress();
                        String data = new String(packet.getData()).trim();
                        Log.i(TAG, String.format("From:%s Data:%s", hostAddress, data));
                        mIncomingPacketListener.processIncomingPacket(hostAddress, data);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    closeSocket(socket);
                }
            }
        }).start();
    }

    /**
     * Sends the given message as UDP broadcast on the network
     *
     * @param message - Message to be broadcasted
     */
    public void sendBroadcast(final String message) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                            broadcastAddress, BROADCAST_PORT);
                    socket.send(sendPacket);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();
    }

    // Must acquire multicast lock to broadcast messages over the network
    private void acquireMultiCastLock(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock mcastLock = wifiManager.createMulticastLock(TAG);
        mcastLock.acquire();
    }

    // Finds out device's broadcast address and sets broadcastAddress field
    private void acquireBroadcastAddress() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
            while (niEnum.hasMoreElements()) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        broadcastAddress = interfaceAddress.getBroadcast();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // Closes the socket
    private void closeSocket(DatagramSocket socket) {
        if (socket != null) {
            socket.close();
        }
    }

    /**
     * An interface made for communication with the activity
     */
    public interface IncomingPacketListener {

        /**
         * Reads the packet and calls other methods accordingly.
         *
         * @param hostAddress - IP Address of sender
         * @param data        - Data present in the packet
         */
        void processIncomingPacket(String hostAddress, String data);
    }
}
