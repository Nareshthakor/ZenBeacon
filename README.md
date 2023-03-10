# QuantumUSB-BluetoothBeacon

[![](https://jitpack.io/v/Nareshthakor/ZenBeacon.svg)](https://jitpack.io/#Nareshthakor/ZenBeacon)

The leading library for detecting beacons on Android. 

- It allows Android devices to use beacons much like iOS devices do
- An Android library providing APIs to interact with beacons.
- An app can request to get notifications when one or more beacons appear or disappear
- An app can also request to get a ranging update from one or more beacons at a frequency of approximately 1Hz
- It also allows Android devices to send beacon transmissions, even in the background.



## Dependency

```gradle
// Root build.gradle:
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

// Target module's build.gradle:
dependencies {
    implementation 'com.github.Nareshthakor:ZenBeacon:1.0.0'
}
```

## Permission Requirements

The specific runtime permissions you request depend on the Android SDK version you are targeting (the “targetSdkVeion” in build.gradle”) and the version of Android on which your app runs. If

You must manually add this permission to the ApplicationManifest.xml:

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
If you target Android 12 or higher (SDK 31+) you must also request:

    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
    <!-- Below is only needed if you want to read the device name or establish a bluetooth connection-->
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
    <!-- Below is only needed if you want to emit beacon transmissions -->
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE/>
If you want to detect beacons in the background, you must also add:

    <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"
    
 ## Samples Code

```
public interface Beacon_Model
{
    // This is just a regular method so it can return something or
    // take arguments if you like.
    String Beacon_Detail(final String message);
}


public BeaconController(Beacon_Model model,
                        Context context,
                        String security_key,
                        Class<?> notification_click_Class_name,
                        boolean background_beacon_scan_mode)
```

## Add following code to MainActivity or any other Activty


```
public class MainActivity extends AppCompatActivity implements Beacon_Model
{

    BeaconController Beacon_controll;
    public static final String MESSAGE_TO_PRINT = "hello world";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_interface_scan_method);

        Beacon_controll = new BeaconController(new Call_Interface_Scan_Method_Activity(),Call_Interface_Scan_Method_Activity.this,"12345678",Qsmp_Dashboard_Activity.class,true);

   

        getBEaconData();

    }


    public void getBEaconData()
    {
        String data_return=Beacon_controll.Beacon_Detail(MESSAGE_TO_PRINT);
        Log.d("Call_InterFace",""+data_return);
    }


    @Override
    public String Beacon_Detail(String message)
    {
        Log.d("Res_main_class",""+message);
        return null;
    }
}
```



