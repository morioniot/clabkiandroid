package com.morion.clabki;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by morion on 4/07/17.
 */

public class ClabkiScanner {

    private static final String TAG = "SCANNER_DEBUG_FLAG";

    private BluetoothLeScanner bleScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private Context context;
    private DetectionManager detectionManager;
    private boolean scanning = false;

    public ClabkiScanner(Context context, DetectionManager detectionManager) {
        this.context = context;
        this.detectionManager = detectionManager;
        getBLEScanner();
        setScanSettings();
        setFilterSettings();
    }

    private void getBLEScanner() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void setScanSettings() {
        ScanSettings.Builder settingsBuilder = new ScanSettings.Builder();
        settingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        settings = settingsBuilder.build();
    }

    private void setFilterSettings() {
        //Creating list of filters
        HexUtil hexTool = new HexUtil();
        filters = new ArrayList<>();
        /*ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
        final byte[] manufacturerSpecificDataConstant =
                hexTool.hexStringToByteArray("02150112233445566778899AABBCCDDEEFF0FFFFFFFFFF");
        final byte[] filterMask =
                hexTool.hexStringToByteArray("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0000000000");
        final int manufacturerId = 76;
        filterBuilder.setManufacturerData(manufacturerId, manufacturerSpecificDataConstant, filterMask);
        ScanFilter filter = filterBuilder.build();
        filters.add(filter);*/
    }

    public void scan (final boolean enable) {
        if(enable) {
            bleScanner.startScan(null, settings, leScanCallback);
            scanning = true;
            Toast toast = Toast.makeText(context, "Scanning for beacons", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            bleScanner.stopScan(leScanCallback);
            scanning = false;
            Toast toast = Toast.makeText(context, "Scanning has stopped", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scanning Error Code: " + errorCode);
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            byte[] advertisedPackage = result.getScanRecord().getBytes();
            int protocol = Beacon.figureOutProtocol( advertisedPackage );
            if(protocol == Beacon.CLABKI_PROTOCOL) {
                try {
                    ClabkiBeacon beacon = new BeaconBuilder(result).buildClabkiBeacon();

                    //Logging important info about detected beacon
                    Log.i(TAG, String.valueOf(beacon.isClabkiBeacon()));
                    Log.i(TAG, beacon.getName());
                    Log.i(TAG, beacon.getMacAddress());
                    Log.i(TAG, String.valueOf(beacon.getTxPowerLevel()));
                    Log.i(TAG, beacon.getUUID());
                    Log.i(TAG, String.valueOf(beacon.getMajor()));
                    Log.i(TAG, String.valueOf(beacon.getMinor()));
                    Log.i(TAG, String.valueOf(beacon.getRSSIAt1m()));

                    detectionManager.push(beacon);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.i(TAG, "The beacon detected doesn't use the Clabki protocol");
            }
        }
    };

    public boolean isScanning() {return scanning;}
}
