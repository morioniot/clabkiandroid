package com.morion.clabki;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

/**
 * Created by morion on 5/04/17.
 */

public class Beacon {

    public static final int UNKNOWN_PROTOCOL = 0;
    public static final int IBEACON_PROTOCOL = 1;
    public static final int CLABKI_PROTOCOL = 2;

    private BluetoothDevice beacon = null;
    private int txPowerLevel = 0;
    protected int protocol = UNKNOWN_PROTOCOL;

    public Beacon(@NonNull BluetoothDevice beacon) {
        this.beacon = beacon;
    }

    public static int figureOutProtocol(byte[] advertisedPackage) {
        if(advertisedPackage[3] == (byte)0x1A &&
                advertisedPackage[4] == (byte)0xFF &&
                advertisedPackage[7] == (byte)0x02 &&
                advertisedPackage[8] == (byte)0x15) {
            return Beacon.IBEACON_PROTOCOL;
        }
        if(advertisedPackage[3] == (byte)0x03 &&
                advertisedPackage[4] == (byte)0x03 &&
                advertisedPackage[5] == (byte)0xBF &&
                advertisedPackage[6] == (byte)0xCA &&
                advertisedPackage[7] == (byte)0x0D &&
                advertisedPackage[8] == (byte)0x16 &&
                advertisedPackage[9] == (byte)0xBF &&
                advertisedPackage[10] == (byte)0xCA) {
            return Beacon.CLABKI_PROTOCOL;
        }
        return Beacon.UNKNOWN_PROTOCOL;
    }

    public String getName() {
        final String name = this.beacon.getName();
        if(name == null)
            return "anonymous";
        return name;
    }

    public String getMacAddress() {
        return this.beacon.getAddress();
    }

    public void setTxPowerLevel(int txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
    }

    public int getTxPowerLevel() {
        return this.txPowerLevel;
    }

    public boolean isIBeacon() {
        return (this.protocol == IBEACON_PROTOCOL);
    }

    public boolean isClabkiBeacon() {
        return (this.protocol == CLABKI_PROTOCOL);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        Beacon beacon = (Beacon) o;
        if(this.getMacAddress().equals(beacon.getMacAddress()))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        String[] macAddressChunks = this.getMacAddress().split(":");
        int sum = 0;
        for(String chunk : macAddressChunks) {
            sum += Integer.parseInt(chunk, 16);
        }
        return sum;
    }
}
