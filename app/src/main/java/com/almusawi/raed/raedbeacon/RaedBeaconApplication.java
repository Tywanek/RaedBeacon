package com.almusawi.raed.raedbeacon;

import android.app.Application;
import android.os.Environment;

import com.almusawi.raed.raedbeacon.settings.BeaconList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RaedBeaconApplication extends Application {


    private boolean detailsVisible;

    public boolean isDetailsVisible() {
        return detailsVisible;
    }

    public void setDetailsVisible(boolean detailsVisible) {
        this.detailsVisible = detailsVisible;
    }

    public static void saveHashMapToFile(BeaconList beaconList) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream(Environment
                .getExternalStorageDirectory()+"/"+MainActivity.DIR_NAME +"/"+MainActivity.DIR_NAME);

        ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(beaconList);
        objectOutputStream.close();
    }

    public static BeaconList getHashMapFromFile() throws IOException, ClassNotFoundException {

        FileInputStream fileInputStream = new FileInputStream(Environment
                .getExternalStorageDirectory()+"/"+MainActivity.DIR_NAME +"/"+MainActivity.DIR_NAME);

        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        BeaconList myHashMap = (BeaconList) objectInputStream.readObject();
        objectInputStream.close();

        return myHashMap;
    }

}
