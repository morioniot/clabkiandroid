package com.morion.clabki;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private boolean boundToScannerService = false;
    private static final String TAG = "BLE_DEBUG";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION};
    private BluetoothAdapter bluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @Override
    protected void onResume() {

        //Verifies if the bluetooth is on
        super.onResume();
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }

        //Verifies if the BLEScannerService is currently running to set info text
        final boolean scanning = isMyServiceRunning(BLEScannerService.class);
        setScanningInfoText(scanning);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }
    }

    @TargetApi(23)
    private void requestLocationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(TAG, "Explain why the permission is necessary");
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST) {
            Log.i(TAG, "Permission response has been received");
            if(grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScannerService();
            }
            else {
                Log.i(TAG, "permission denied");
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setScanningInfoText(boolean scanning) {
        TextView infoText = (TextView) findViewById(R.id.information_text);
        if(scanning) {
            infoText.setText("Looking for dogs");
            infoText.setTextColor(Color.GREEN);
        }
        else {
            infoText.setText("Press the scan button");
            infoText.setTextColor(Color.RED);
        }
    }

    public void startScannerService() {
        Intent scannerIntent = new Intent(this, BLEScannerService.class);
        startService(scannerIntent);
        setScanningInfoText(true);
        Log.d(TAG, "Scanner service started");
    }

    public void stopScannerService() {
        Intent scannerIntent = new Intent(this, BLEScannerService.class);
        stopService(scannerIntent);
        setScanningInfoText(false);
        Log.d(TAG, "Scanner service stopped");
    }

    public void handleStartScanButton(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
        else {
            startScannerService();
        }
    }

    public void handleStopScanButton(View view) {
        stopScannerService();
    }
}
