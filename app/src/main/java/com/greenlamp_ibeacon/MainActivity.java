package com.greenlamp_ibeacon;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    protected static final String TAG = "MainActivity";
    private BeaconManager mBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "App started up");

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {

            // Device support Bluetooth

        } else {

            // Device not support Bluetooth

        }

        mBM = BeaconManager.getInstanceForApplication(this);
        mBM.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBM.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBM.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "Beacon detected!!!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG, "Beacon not detected!!");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(TAG, String.valueOf(state));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBM.unbind(this);
    }
}