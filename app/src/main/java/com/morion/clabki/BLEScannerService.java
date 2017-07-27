package com.morion.clabki;


import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class BLEScannerService extends Service {

    public static final String TAG = "BACKGROUND_DEBUG_FLAG";
    private Context context; //This is the application context
    private ClabkiScanner scanner;
    private DetectionManager detectionManager;
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {return binder;}

    @Override
    public void onCreate() {

        context = getApplicationContext();
        detectionManager = new DetectionManager(context);
        scanner = new ClabkiScanner(context, detectionManager);

        Toast toast = Toast.makeText(context, "Service created", Toast.LENGTH_SHORT);
        toast.show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scanner.scan(true);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast toast = Toast.makeText(context, "Service destroyed", Toast.LENGTH_SHORT);
        toast.show();
        scanner.scan(false);
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public ClabkiScanner getClabkiScanner() {
            return scanner;
        }
    }
}
