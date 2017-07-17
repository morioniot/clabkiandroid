package com.morion.clabki;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by morion on 16/06/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootBroadcastReceiver";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast toast = Toast.makeText(context, "Recuerda que Clabki necesita de tu bluetooth" +
                    " para ayudar a más mascotas a volver a su hogar", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Intent scannerIntent = new Intent(context, BLEScannerService.class);
            context.startService(scannerIntent);
        }
    }
}
