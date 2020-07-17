package com.greenlamp_ibeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    protected static final String TAG = "MainActivity";
    private BeaconManager mBM;
    private BluetoothAdapter mBluetooth;
    private List<Beacon> mBeaconlist = new ArrayList<>();
    private final Region region = new Region("Greenlamp", null, null, null);
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "@@@@@ App started up @@@@@");
        Button mbt = findViewById(R.id.main_button);

        checkVersion();

        mBM = BeaconManager.getInstanceForApplication(this);
        /**
         *  IBEACON
         * */
        mBM.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBM.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBM.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "Beacon Service Connected");

        mBM.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    mBeaconlist.clear();
                    mBeaconlist.addAll(beacons);
                }
            }
        });

        try {
            mBM.startRangingBeaconsInRegion(new Region("Greenlamp", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, "Remote Exception Error : ");
            e.printStackTrace();
        }
//        try {
//            mBM.startMonitoringBeaconsInRegion(region);
//        } catch (RemoteException e) {
//            Log.e(TAG, "Remote Exception Error : ");
//            e.printStackTrace();
//        }
    }

    /**
     *  안드로이드 버전확인 (M이상)
     * */
    public void checkVersion() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access" );
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok,null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void onStartDetecting(View v) {
        for (Beacon bc : mBeaconlist) {

            Log.d(TAG, "@@@ BTID : " + bc.getBluetoothAddress());
            Log.d(TAG, "@@@ ID1 : " + bc.getId1());
            Log.d(TAG, "@@@ ID2 : " + bc.getId2());
            Log.d(TAG, "@@@ ID3 : " + bc.getId3());
        }
    }
}