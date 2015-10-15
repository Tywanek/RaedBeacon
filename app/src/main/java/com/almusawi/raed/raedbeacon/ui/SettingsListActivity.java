package com.almusawi.raed.raedbeacon.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.almusawi.raed.raedbeacon.MainActivity;
import com.almusawi.raed.raedbeacon.R;
import com.almusawi.raed.raedbeacon.UserPreferences;
import com.almusawi.raed.raedbeacon.adapters.BeaconListAdapter;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.Collections;
import java.util.List;


public class SettingsListActivity extends Activity {

  private static final int REQUEST_ENABLE_BT = 1234;
  private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

  private BeaconManager beaconManager;
  private BeaconListAdapter adapter;
  private CircleProgressBar circleProgressBar;
  private LinearLayout mainLayout, listLayout;
  private EditText pinInput;
  private boolean isPinOk;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    pinInput = (EditText)findViewById(R.id.myPinInput);
    mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
    mainLayout.setBackground(getResources().getDrawable(R.drawable.min));
    listLayout = (LinearLayout)findViewById(R.id.listLayout);
    listLayout.setVisibility(View.GONE);
    if(pinCodeCheck()){
      listLayout.setVisibility(View.VISIBLE);
      pinInput.setVisibility(View.GONE);
    }

    //set ProgreBar
    circleProgressBar = (CircleProgressBar)findViewById(R.id.progress);
    circleProgressBar.setColorSchemeResources(R.color.purple);
    ImageView logo = (ImageView)findViewById(R.id.logo);
    logo.setImageDrawable(getResources().getDrawable(R.drawable.logo_settings));

    // Configure device list.
    adapter = new BeaconListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);

    // Configure BeaconManager.
    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
        // Note that results are not delivered on UI thread.
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // Note that beacons reported here are already sorted by estimated
            // distance between device and beacon.
            adapter.replaceWith(beacons);
            circleProgressBar.setVisibility(View.GONE);
          }
        });
      }
    });

    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BeaconListAdapter adapter = (BeaconListAdapter)parent.getAdapter();

        Intent settingsItent = new Intent(getApplicationContext(), Settings.class);
        settingsItent.putExtra(MainActivity.BEACON_ID_INTENT, adapter.getItem(position).getMacAddress());
        startActivity(settingsItent);
      }
    });
  }

  public void hideKeyboard() {
    InputMethodManager inputManager = (InputMethodManager)
            this.getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
  }

  private boolean pinCodeCheck(){
    isPinOk = false;
    pinInput.setVisibility(View.VISIBLE);
    pinInput.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
          Toast.makeText(SettingsListActivity.this, pinInput.getText(), Toast.LENGTH_SHORT).show();
          if (pinInput.getText().toString().equals(UserPreferences.getUserPassword(getApplicationContext())))
            isPinOk = true;
          pinInput.setVisibility(View.GONE);
          hideKeyboard();
        }else if(UserPreferences.getUserPassword(getApplicationContext()).equals("")){
          UserPreferences.setUserPassword(getApplicationContext(), pinInput.getText().toString());
          pinInput.setVisibility(View.GONE);
          hideKeyboard();
          isPinOk = true;
        }
        return true;
      }});

    return isPinOk;
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
        //Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void connectToService() {
    adapter.replaceWith(Collections.<Beacon>emptyList());
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
        } catch (RemoteException e) {
          Toast.makeText(SettingsListActivity.this, "Cannot start ranging, something terrible happened", Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    startActivity(new Intent(this, BeaconsSearchActivity.class));
  }
}
