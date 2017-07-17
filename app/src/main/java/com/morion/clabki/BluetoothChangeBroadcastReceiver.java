package com.morion.clabki;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by morion on 17/07/17.
 */

public class BluetoothChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if(state == BluetoothAdapter.STATE_OFF) {
                Intent scannerIntent = new Intent(context, BLEScannerService.class);
                context.stopService(scannerIntent);
                Toast toast = Toast.makeText(context, "Recuerda que Clabki necesita de tu bluetooth" +
                        " para ayudar a más mascotas a volver a su hogar", Toast.LENGTH_LONG);
                toast.show();
            }
            else if(state == BluetoothAdapter.STATE_ON){
                Intent scannerIntent = new Intent(context, BLEScannerService.class);
                context.startService(scannerIntent);
            }
        }
    }
}
