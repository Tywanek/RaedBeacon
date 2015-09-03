package com.almusawi.raed.raedbeacon.settings;

import java.io.Serializable;
import java.util.HashMap;


public class BeaconList extends HashMap<String, MyBeacon> implements Serializable{
    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }
}
