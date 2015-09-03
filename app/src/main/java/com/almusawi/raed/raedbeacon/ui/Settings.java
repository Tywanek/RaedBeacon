package com.almusawi.raed.raedbeacon.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.almusawi.raed.raedbeacon.MainActivity;
import com.almusawi.raed.raedbeacon.R;
import com.almusawi.raed.raedbeacon.RaedBeaconApplication;
import com.almusawi.raed.raedbeacon.settings.BeaconList;
import com.almusawi.raed.raedbeacon.settings.MyBeacon;
import com.almusawi.raed.raedbeacon.settings.SoundRecord;

import java.io.IOException;

/**
 * Created by radek on 24.08.15.
 */
public class Settings extends Activity {

    private LinearLayout startRecord, stopRecord, playRecord, setImage, mainLayout, saveBeacon;
    private EditText soundName;
    private ImageView imageView;
    private SoundRecord beaconSound;
    private String BEACON_ID, filePath, picturePath="";
    private BeaconList myBeaconHashMap = new BeaconList();
    private static int RESULT_LOAD_IMAGE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        saveBeacon = (LinearLayout)findViewById(R.id.saveBeacon);
        soundName = (EditText)findViewById(R.id.soundName);
        startRecord = (LinearLayout)findViewById(R.id.record_icon);
        stopRecord = (LinearLayout)findViewById(R.id.stop_icon);
        playRecord = (LinearLayout)findViewById(R.id.play_icon);
        setImage = (LinearLayout)findViewById(R.id.setImage);
        imageView = (ImageView)findViewById(R.id.imageView);

        this.BEACON_ID = getIntent().getExtras().getString(MainActivity.BEACON_ID_INTENT);
        try {
            this.myBeaconHashMap = RaedBeaconApplication.getHashMapFromFile();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(myBeaconHashMap.containsKey(this.BEACON_ID)){
            soundName.setText(myBeaconHashMap.get(this.BEACON_ID).getName());
            imageView.setImageBitmap(BitmapFactory.decodeFile(myBeaconHashMap.get(this.BEACON_ID).getImagePath()));
        }


        soundName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                saveBeacon.setVisibility(View.VISIBLE);
                return false;
            }
        });
        startRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!soundName.getText().toString().equals("")) {
                    startRecord.setClickable(false);
                    beaconSound = new SoundRecord(soundName.getText().toString());
                    filePath = beaconSound.getmFileName();
                    beaconSound.onRecord(true);
                    startRecord.setVisibility(View.GONE);
                    stopRecord.setVisibility(View.VISIBLE);
                } else {
                    soundName.setError(getResources().getString(R.string.empty_file_name));
                }

            }
        });
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beaconSound.onRecord(false);
                stopRecord.setVisibility(View.GONE);
                startRecord.setVisibility(View.VISIBLE);
                startRecord.setClickable(true);
                playRecord.setVisibility(View.VISIBLE);
                saveBeacon.setVisibility(View.VISIBLE);
            }
        });
        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    beaconSound.onPlay(true);
            }
        });
        saveBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyBeacon myBeacon = new MyBeacon();
               // myBeacon = myBeaconHashMap.get(BEACON_ID);
                myBeacon.setId(BEACON_ID);
                myBeacon.setName(soundName.getText().toString());
                myBeacon.setSoundPath(filePath);
                myBeacon.setImagePath(picturePath);

                MainActivity.addBeaconToList(myBeacon);
                finish();
            }
        });
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if(!soundName.getText().toString().equals("")){
                    soundName.setError(null);
                }
            }
        });
        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBeacon.setVisibility(View.VISIBLE);
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

}
