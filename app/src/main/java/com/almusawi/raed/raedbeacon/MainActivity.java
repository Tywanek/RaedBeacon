package com.almusawi.raed.raedbeacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.almusawi.raed.raedbeacon.settings.BeaconList;
import com.almusawi.raed.raedbeacon.settings.MyBeacon;
import com.almusawi.raed.raedbeacon.ui.BeaconsSearchActivity;

import java.io.File;
import java.io.IOException;


public class MainActivity extends Activity {

    public static boolean FILE_DIR_EXIST = false;
    public static final String BEACON_ID_INTENT = "BEACON_ID";
    public static final String DIR_NAME = "raedbeacon";

    public static BeaconList myBeaconHashMap = new BeaconList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get saved files
        try {
            myBeaconHashMap = RaedBeaconApplication.getHashMapFromFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(createFileDir(MainActivity.DIR_NAME)){
            startActivity(new Intent(this, BeaconsSearchActivity.class));
            finish();
        }

    }


    private boolean createFileDir(String dirName){

        File folder = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        FILE_DIR_EXIST = success;

        return FILE_DIR_EXIST;
    }

    public static void addBeaconToList(MyBeacon myBeacon){
        if(myBeaconHashMap.containsKey(myBeacon.getId())){
            myBeaconHashMap.get(myBeacon.getId()).setName(myBeacon.getName());
            myBeaconHashMap.get(myBeacon.getId()).setSoundPath(myBeacon.getSoundPath());
            myBeaconHashMap.get(myBeacon.getId()).setImagePath(myBeacon.getImagePath());
        }else {
            myBeaconHashMap.put(myBeacon.getId(), myBeacon);
        }

        try {
            RaedBeaconApplication.saveHashMapToFile(myBeaconHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
