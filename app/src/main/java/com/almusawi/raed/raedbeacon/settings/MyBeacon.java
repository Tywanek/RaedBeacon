package com.almusawi.raed.raedbeacon.settings;

import java.io.Serializable;

public class MyBeacon implements Serializable {

    private String id = "";
    private String name = "";
    private String soundPath = "";
    private String imagePath = "";

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void MyBeacon(String id, String name, String soundPath){
        this.setId(id);
        this.setName(name);
        this.setSoundPath(soundPath);
    }
    public void MyBeacon(String id, String name){
        this.setId(id);
        this.setName(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

}
