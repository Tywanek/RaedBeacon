package com.almusawi.raed.raedbeacon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.almusawi.raed.raedbeacon.R;
import com.almusawi.raed.raedbeacon.RaedBeaconApplication;
import com.almusawi.raed.raedbeacon.settings.BeaconList;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class BeaconListAdapter extends BaseAdapter {

  private ArrayList<Beacon> beacons;
  private LayoutInflater inflater;
  private BeaconList myBeaconHashMap = new BeaconList();

  public BeaconListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.beacons = new ArrayList<>();


    try {
      //get saved files
      this.myBeaconHashMap = RaedBeaconApplication.getHashMapFromFile();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void replaceWith(Collection<Beacon> newBeacons) {
    this.beacons.clear();
    this.beacons.addAll(newBeacons);
    try {
      this.myBeaconHashMap = RaedBeaconApplication.getHashMapFromFile();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return beacons.size();
  }

  @Override
  public Beacon getItem(int position) {
    return beacons.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

  private void bind(Beacon beacon, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.macTextView.setText(String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), Utils.computeAccuracy(beacon)));
    holder.majorTextView.setText("Major: " + beacon.getMajor());
    holder.minorTextView.setText("Minor: " + beacon.getMinor());
    holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
    holder.rssiTextView.setText("RSSI: " + beacon.getRssi());
    if(myBeaconHashMap.containsKey(beacon.getMacAddress())){
      holder.myName.setText("Name: "+myBeaconHashMap.get(beacon.getMacAddress()).getName());
    }
  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.beacon_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView majorTextView;
    final TextView minorTextView;
    final TextView measuredPowerTextView;
    final TextView rssiTextView;
    final TextView myName;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      majorTextView = (TextView) view.findViewWithTag("major");
      minorTextView = (TextView) view.findViewWithTag("minor");
      measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
      myName = (TextView)view.findViewWithTag("myName");
    }
  }
}
