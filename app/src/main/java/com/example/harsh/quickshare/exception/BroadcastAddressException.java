package com.example.harsh.quickshare.exception;

/**
 * This exception is thrown when broadcast address is null. This generally occurs when device
 * is not connected to a network.
 * Created by Harsh on 16-Dec-17.
 */

public class BroadcastAddressException extends RuntimeException {

    public BroadcastAddressException(RuntimeException e) {
        super(e);
    }
}
