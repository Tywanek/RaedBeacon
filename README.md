# RaedBeacon

###RaedBeaconApplication
- isDetailsVisible
- setDetailsVisible
- saveHashMapToFile
- getHashMapFromFile
- detailsVisible

class RaedBeaconApplication extends Application bacause instance of this class exist whole application life cykle.
detailsVisible keep boolean value does DetailActivity is visible for user.
We want to be sure that user closed details, before creating a new one.
Also RaedBeaconApplication provides static methods
saveHashMapToFile and getHashMapFromFile.


###MainActivity
- onCreate
- createFileDir
- addBeaconToList
- FILE_DIR_EXIST
- BEACON_ID_INTENT
- DIR_NAME
- myBeaconHashMap

MainActivity is first activity declared in Manifest. 
Class BeaconList extends  HashMap<String, MyBeacon> where String contain Beacon id and MyBecon contain id, name, soundPath, imagePath.
public static BeaconList myBeaconHashMap is inflating by static mathood RaedBeaconApplication.getHashMapFromFile().
Boolean function createFileDir(MainActivity.DIR_NAME) try to crete new folder on internal storage if not exist.
If it's true activity starts new activity BeaconsSearchActivity.
The static field names speak for themselves FILE_DIR_EXIST, DIR_NAME, BEACON_ID_INTENT is just int value used as flag for intents.


###BeaconsSearchActivity
- onCreate
- startDelayTimer
- onDestroy
- onStart
- onResume
- onStop
- onActivityResult
- connectToService
- onCreateOptionsMenu
- onOptionsItemSelected
- REQUEST_ENABLE_BT
- ALL_ESTIMOTE_BEACONS_REGION
- beaconManager
- circleProgressBar
- FOUND_BEACON
- TIME_DELAY
- DETAIL_ACTIVITY
- timerFinish

BeaconsSearchActivity create instane of BeaconManager class, part of estimote.sdk.
Important part of code is inside RangingListener where onBeaconsDiscovered returns List<Beacon> beacons.
We check if list contain any beacon, DetailsActivity is not active, distans betwen user and beacon is smaller then 1,5m, 
and delay timer is finished.

        if (beacons.size() > 0 && !((RaedBeaconApplication) getApplicationContext()).isDetailsVisible()
                    && (Utils.computeAccuracy(beacons.get(0)) < 1.5 && timerFinish)) {

              ((RaedBeaconApplication) getApplicationContext()).setDetailsVisible(true);

              intent.putExtra(BeaconsSearchActivity.FOUND_BEACON, beacons.get(0));
              startActivityForResult(intent, DETAIL_ACTIVITY);
            }

We're passing object beacon through Intent by .putExtra methood. We could send only id of beacon but memory usage is not relevant.
In method  onActivityResult we're waiting for response from intent enableBtIntent if bluetooth is anable, and DetailActivity which triggers timer controling interval between new messages for user.


###DetailActivity
- onCreate
- onStop
- startTimer
- returnResult
- myBeaconHashMap
- myActiveBeacon
- name
- imageView
- timer
- timerTask
- context
- soundRecord
- TIME_DELAY

DetailActivity
We receive BeaconList myBeaconHashMap from MainActivity and Beacon myActiveBeacon from intent.
Then we check if myBeaconHashMap (list made in Setting activity) contains recived beacon, if so we are trying use his methoods for user, such as name, recorded sound and image. Timer is set up to finish activity after TIME_DELAY ends.
onStop() method sets value to instance of application.

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


 
###Settings
- onCreate
- onActivityResult
- startRecord
- stopRecord
- playRecord
- setImage
- mainLayout
- saveBeacon
- soundName
- imageView
- beaconSound
- BEACON_ID
- filePath
- picturePath
- myBeaconHashMap
- RESULT_LOAD_IMAGE

Settings
Funcionality of this activity is mostly based on user interface and layout, each OnClickListener is creating part of MyBeacon object, and finaly in saveBeacon.setOnClickListener we are adding new beacon to list  MainActivity.addBeaconToList(myBeacon);
Sound recording is controled by instance of SoundRecord class.
