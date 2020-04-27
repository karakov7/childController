package ru.ivan.childcontroller;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

import java.util.List;

public class LocationUpdatesIntentService extends IntentService {
    private static final String ACTION_PROCESS_UPDATES =
            "ru.ivan.childcontroller.action.PROCESS_UPDATES";
    private static final String TAG = LocationUpdatesIntentService.class.getSimpleName();
//TIME_TICK

    public LocationUpdatesIntentService() {
        super(TAG);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Utils.setLocationUpdatesResult(this, locations);
                    Utils.sendNotification(this, Utils.getLocationResultTitle(this, locations));
                    Log.i("Location S", Utils.getLocationUpdatesResult(this));
                }
            }
        }
    }
}
