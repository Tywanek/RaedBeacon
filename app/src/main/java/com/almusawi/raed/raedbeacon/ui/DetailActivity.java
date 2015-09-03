package com.almusawi.raed.raedbeacon.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.almusawi.raed.raedbeacon.MainActivity;
import com.almusawi.raed.raedbeacon.R;
import com.almusawi.raed.raedbeacon.RaedBeaconApplication;
import com.almusawi.raed.raedbeacon.settings.BeaconList;
import com.almusawi.raed.raedbeacon.settings.SoundRecord;
import com.estimote.sdk.Beacon;

import java.util.Timer;
import java.util.TimerTask;


public class DetailActivity extends Activity {
    private BeaconList myBeaconHashMap = new BeaconList();
    private Beacon myActiveBeacon;
    private TextView name;
    private ImageView imageView;
    private Timer timer;
    private TimerTask timerTask;
    private Context context;
    private SoundRecord soundRecord = null;

    private static final long TIME_DELAY = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        context = this;
        name = (TextView)findViewById(R.id.name);
        imageView = (ImageView)findViewById(R.id.imageView);

        myActiveBeacon = getIntent().getParcelableExtra(BeaconsSearchActivity.FOUND_BEACON);
        myBeaconHashMap = MainActivity.myBeaconHashMap;

        if(myBeaconHashMap.containsKey(myActiveBeacon.getMacAddress())){
            name.setText(myBeaconHashMap.get(myActiveBeacon.getMacAddress()).getName());
            soundRecord = new SoundRecord(
                    myBeaconHashMap.get(myActiveBeacon.getMacAddress()).getName());
            soundRecord.onPlay(true);
            startTimer();
            if(!myBeaconHashMap.get(myActiveBeacon.getMacAddress()).getImagePath().equals("")){
                imageView.setImageBitmap(BitmapFactory
                        .decodeFile(myBeaconHashMap.get(myActiveBeacon.getMacAddress()).getImagePath()));
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundRecord.onPlay(true);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((RaedBeaconApplication)getApplicationContext()).setDetailsVisible(false);
    }

    private void startTimer(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                soundRecord.onPlay(false);
                ((Activity)context).finish();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, TIME_DELAY);
    }

}
