package com.almusawi.raed.raedbeacon.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.almusawi.raed.raedbeacon.R;
import com.almusawi.raed.raedbeacon.RaedBeaconApplication;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class BeaconsSearchActivity extends Activity {

  private static final int REQUEST_ENABLE_BT = 1234;
  private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

  private BeaconManager beaconManager;
  private CircleProgressBar circleProgressBar;
  public static String FOUND_BEACON = "FOUND_BEACON";
  private static final long TIME_DELAY = 15000;
  public static final int DETAIL_ACTIVITY = 100;

  private boolean timerFinish = true;
  private Timer timer;
  private TimerTask timerTask;
  private LinearLayout mainLayout;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    //set ProgreBar
    mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
    mainLayout.setBackground(getResources().getDrawable(R.drawable.abcb));
    ImageView logo = (ImageView)findViewById(R.id.logo);

    logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
    logo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), SettingsListActivity.class));
        finish();
      }
    });
    circleProgressBar = (CircleProgressBar)findViewById(R.id.progress);
    circleProgressBar.setColorSchemeResources(R.color.purple);

    final Intent intent = new Intent(getApplicationContext(), DetailActivity.class);


    // Configure BeaconManager.
    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
        // Note that results are not delivered on UI thread.
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (beacons.size() > 0 && !((RaedBeaconApplication) getApplicationContext()).isDetailsVisible()
                    && (Utils.computeAccuracy(beacons.get(0)) < 1.5 && timerFinish)) {

              ((RaedBeaconApplication) getApplicationContext()).setDetailsVisible(true);

              intent.putExtra(BeaconsSearchActivity.FOUND_BEACON, beacons.get(0));
              startActivityForResult(intent, DETAIL_ACTIVITY);
            }

          }
        });
      }
    });
  }

  public void startDelayTimer(){
    timerFinish = false;
    timerTask = new TimerTask() {
      @Override
      public void run() {
        timerFinish = true;
        timer.cancel();
      }
    };
    timer = new Timer();
    timer.schedule(timerTask, TIME_DELAY);
  }

  @Override
  protected void onDestroy() {
    beaconManager.disconnect();
    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check if device supports Bluetooth Low Energy.
    if (!beaconManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    // If Bluetooth is not enabled, let user enable it.
    if (!beaconManager.isBluetoothEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      connectToService();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    circleProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected void onStop() {
    try {
      beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
    } catch (RemoteException e) {}

    super.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        connectToService();
      } else {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
      }
    }
    if(requestCode == DETAIL_ACTIVITY){
      if (resultCode == Activity.RESULT_OK){
        Toast.makeText(this, "Start timer now", Toast.LENGTH_SHORT).show();
        startDelayTimer();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void connectToService() {
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
          Toast.makeText(BeaconsSearchActivity.this, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      startActivity(new Intent(getApplicationContext(), SettingsListActivity.class));
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
