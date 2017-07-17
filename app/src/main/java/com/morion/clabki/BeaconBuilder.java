package com.morion.clabki;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.support.annotation.NonNull;

/**
 * Created by morion on 5/04/17.
 */

public class BeaconBuilder {

    private ScanResult scanResult;

    public BeaconBuilder(@NonNull ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public IBeacon buildIBeacon() throws Exception {

        //BLE Characteristics
        BluetoothDevice beacon = this.scanResult.getDevice();
        byte[] advertisedPackage = this.scanResult.getScanRecord().getBytes();
        IBeacon iBeacon = new IBeacon(beacon);
        iBeacon.setTxPowerLevel(this.scanResult.getRssi());

        //iBeacon Characteristics
        int[] UUIDChunkSizes = new int[] {4,2,2,2,6};
        iBeacon.setUUID(IBeacon.extractUUID(advertisedPackage, UUIDChunkSizes));
        iBeacon.setMajor(IBeacon.extractMajor(advertisedPackage));
        iBeacon.setMinor(IBeacon.extractMinor(advertisedPackage));
        iBeacon.setRSSIAt1m(IBeacon.extractRSSIAt1m(advertisedPackage));

        return iBeacon;
    }

    public ClabkiBeacon buildClabkiBeacon() throws Exception {

        //BLE Characteristics
        BluetoothDevice beacon = this.scanResult.getDevice();
        byte[] advertisedPackage = this.scanResult.getScanRecord().getBytes();
        ClabkiBeacon clabkiBeacon = new ClabkiBeacon(beacon);
        clabkiBeacon.setTxPowerLevel(this.scanResult.getRssi());

        //Clabki Beacon Characteristics
        clabkiBeacon.setUUID(ClabkiBeacon.extractUUID(advertisedPackage));
        clabkiBeacon.setMajor(ClabkiBeacon.extractMajor(advertisedPackage));
        clabkiBeacon.setMinor(ClabkiBeacon.extractMinor(advertisedPackage));
        clabkiBeacon.setRSSIAt1m(ClabkiBeacon.extractRSSIAt1m(advertisedPackage));

        return clabkiBeacon;
    }
}
