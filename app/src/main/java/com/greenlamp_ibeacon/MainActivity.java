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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    protected static final String TAG = "MainActivity";
    private BeaconManager mBM;
    private List<Beacon> mBeaconlist = new ArrayList<>();
    private final Region region = new Region("Greenlamp", null, null, null);
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @BindView(R.id.main_button)
    AppCompatButton mButton;

    @BindView(R.id.main_uuid)
    AppCompatTextView mUuid;

    @BindView(R.id.main_major)
    AppCompatTextView mMajor;

    @BindView(R.id.main_minor)
    AppCompatTextView mMinor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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
            mBM.startRangingBeaconsInRegion(region);
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
    
    /**
     *  클릭 시 비콘이 감지가 되면 정보 출력 후 탐색 끝냄
     * */

    public void onStartDetecting(View v) {
        Beacon bcGreen = null;
        String uuid = "";
        for (Beacon bc : mBeaconlist) {
            uuid = bc.getId1().toString();
            if ((uuid.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0"))) {
                bcGreen = bc;
            }
            Log.d(TAG, "@@@ BTID : " + bc.getBluetoothAddress());
            Log.d(TAG, "@@@ ID1 : " + bc.getId1());
            Log.d(TAG, "@@@ ID2 : " + bc.getId2());
            Log.d(TAG, "@@@ ID3 : " + bc.getId3());

        }

        if (bcGreen != null) {
            mUuid.setText("UUID : " + bcGreen.getId1());
            mMajor.setText("Major : " + bcGreen.getId2());
            mMinor.setText("Minor : " + bcGreen.getId3());
            onDestroy();
        } else {
            Toast.makeText(this, "비콘이 감지되지 않습니다.", Toast.LENGTH_SHORT).show();
        }

    }
}