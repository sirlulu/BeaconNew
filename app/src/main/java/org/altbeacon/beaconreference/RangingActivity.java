package org.altbeacon.beaconreference;

import java.util.Collection;

import android.app.Activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
    private Beacon greenBeacon;
    private Beacon blueBeacon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
    }

    @Override 
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override 
    protected void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override 
    protected void onResume() {
        super.onResume();
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        RangeNotifier rangeNotifier = new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

               if (beacons.size() > 0) {
                   //Log.d(TAG, "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());
                   //Beacon firstBeacon = beacons.iterator().next();
                   //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                  for (Beacon var : beacons) {
                      for (int i = 0; i < 10; i++) {
                          if (var.getId3().equals(Identifier.fromInt(60004))) {
                              greenBeacon = var;
                              //logToDisplay("Green beacon recognised\n");
                              //logToDisplay("The green beacon is about " + var.getDistance() + " meters away and has an RSSI of " + var.getRssi() + " dB\n");
                              //logToDisplay("Average RSSI is: " + greenBeacon.getRunningAverageRssi());

                          } else if (var.getId3().equals(Identifier.fromInt(5012))) {
                              blueBeacon = var;
                              //logToDisplay("Blue beacon recognised\n");
                              //logToDisplay("The blue beacon is about " + var.getDistance() + " meters away and has an RSSI of " + var.getRssi() + " dB\n");
                          }
                      }
                  }

               }
               //logToDisplay("For green beacon, average RSSI: "+ greenBeacon.getRunningAverageRssi() +
                 //    "\nFor blue beacon, average RSSI: "+ blueBeacon.getRunningAverageRssi() +"\n");

               if(greenBeacon.getRunningAverageRssi()>-70 && greenBeacon.getRunningAverageRssi()<-50
                     && blueBeacon.getRunningAverageRssi()>-90 && blueBeacon.getRunningAverageRssi()<-70)
                  logToDisplay("You are in your room! (What is your excuse for this mess?)\n");

               if(greenBeacon.getRunningAverageRssi()>-90 && greenBeacon.getRunningAverageRssi()<-80
                       && blueBeacon.getRunningAverageRssi()>-60 && blueBeacon.getRunningAverageRssi()<-40)
                   logToDisplay("You are in the kitchen! (Drink something, alcohol free)\n");

               if(greenBeacon.getRunningAverageRssi()>-75 && greenBeacon.getRunningAverageRssi()<-65
                       && blueBeacon.getRunningAverageRssi()>-90 && blueBeacon.getRunningAverageRssi()<-75)
                   logToDisplay("You are in Luca's room! (Congratulate him for passing APA) \n");
           }
        };
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            beaconManager.addRangeNotifier(rangeNotifier);
        } catch (RemoteException e) {   }
    }

    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);
                editText.append(line+"\n");
            }
        });
    }
}