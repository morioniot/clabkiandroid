package com.morion.clabki;

import android.bluetooth.BluetoothDevice;

/**
 * Created by morion on 28/06/17.
 */

public class ClabkiBeacon extends Beacon {

    final private static int UUID_SIZE = 4;
    final private static int UUID_INDEX = 11;
    final private static int MAJOR_INDEX = 15;
    final private static int MINOR_INDEX = 17;
    final private static int RSSI_AT_1M_INDEX = 19;

    private String UUID;
    private int major;
    private int minor;
    private byte RSSIAt1m;

    public ClabkiBeacon(BluetoothDevice beacon) {
        super(beacon);
        this.protocol = Beacon.CLABKI_PROTOCOL;
    }

    private static boolean isValidUUIDChunksArray(int[] chunkSizes) {
        int sum = 0;
        for(int i = 0; i < chunkSizes.length; i++)
            sum += chunkSizes[i];
        if(sum == ClabkiBeacon.UUID_SIZE)
            return true;
        return false;
    }

    public static String extractUUID(byte[] advertisedPackage) {
        final HexUtil hexTool = new HexUtil();
        final String[] packageInHexStrings = hexTool.byteArrayToHexStrArray(advertisedPackage);
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < UUID_SIZE; i++) {
            stringBuilder.append(packageInHexStrings[UUID_INDEX + i]);
        }
        return stringBuilder.toString();
    }

    public static String extractUUID(byte[] advertisedPackage, int[] chunkSizes) throws Exception {
        if(ClabkiBeacon.isValidUUIDChunksArray(chunkSizes)) {
            final HexUtil hexTool = new HexUtil();
            final String[] packageInHexStrings = hexTool.byteArrayToHexStrArray(advertisedPackage);
            final StringBuilder stringBuilder = new StringBuilder();
            int uuidIndex = UUID_INDEX;
            for (int i = 0; i < chunkSizes.length; i++) {
                for(int j = 0; j < chunkSizes[i]; j++) {
                    stringBuilder.append(packageInHexStrings[uuidIndex]);
                    uuidIndex++;
                }
                if(i < chunkSizes.length - 1)
                    stringBuilder.append('-');
            }
            return stringBuilder.toString();
        }
        else {
            throw new Exception("The chunk array is invalid");
        }
    }

    private static int extract2BytesId(byte[] advertisedPackage, int start) {
        int idRightByte = advertisedPackage[start + 1] & 0xFF;
        int idLeftByte = advertisedPackage[start] & 0xFF;

        //bits are shift to the left to get leftByte
        idLeftByte = idLeftByte << 8;

        int id = idLeftByte | idRightByte;
        return id;
    }

    public static int extractMajor(byte[] advertisedPackage) {
        return ClabkiBeacon.extract2BytesId(advertisedPackage, MAJOR_INDEX);
    }

    public static int extractMinor(byte[] advertisedPackage) {
        return ClabkiBeacon.extract2BytesId(advertisedPackage, MINOR_INDEX);
    }

    public static byte extractRSSIAt1m(byte[] advertisedPackage) {
        return advertisedPackage[RSSI_AT_1M_INDEX];
    }

    //*******************
    //Getters and Setters
    //*******************

    public String getUUID() {
        return this.UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public int getMajor() {
        return this.major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return this.minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public byte getRSSIAt1m() {
        return  this.RSSIAt1m;
    }

    public void setRSSIAt1m(byte rssiAt1m) {
        this.RSSIAt1m = rssiAt1m;
    }
}
