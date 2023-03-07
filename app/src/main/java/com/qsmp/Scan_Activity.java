package com.qsmp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.qsmplibrary.altbeacon.beacon.Beacon;
import com.qsmplibrary.altbeacon.beacon.BeaconManager;
import com.qsmplibrary.altbeacon.beacon.BeaconParser;
import com.qsmplibrary.altbeacon.beacon.Identifier;
import com.qsmplibrary.altbeacon.beacon.MonitorNotifier;
import com.qsmplibrary.altbeacon.beacon.RangeNotifier;
import com.qsmplibrary.altbeacon.beacon.Region;
import com.qsmplibrary.altbeacon.beacon.powersave.BackgroundPowerSaver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Scan_Activity extends AppCompatActivity implements MonitorNotifier {

    public static final Region wildcardRegion = new Region("all-beacons", null, null, null);
    private BackgroundPowerSaver backgroundPowerSaver;
    public static boolean insideRegion = false;
    private RecyclerView recyclerView;
    List<ProductBean> list =new ArrayList<>();
    FrameLayout frm_layout;
    String name,imageUrl;
    BeaconManager beaconManager;
    LinearLayout ll_scan_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        recyclerView=findViewById(R.id.idCourseRV);
        frm_layout=findViewById(R.id.frm_layout);
        ll_scan_back=findViewById(R.id.ll_scan_back);
        final RippleBackground rippleBackground= findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        //mInstance = this;
        BeaconManager.getInstanceForApplication(this).setMonitorNotifier(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);


        ll_scan_back.setOnClickListener(v -> {
            onBackPressed();
        });
       /* BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();

        // The example shows how to find iBeacon.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));


        //beaconManager.startMonitoring(new Region("region 1", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), Identifier.parse("1")));
        try {
            beaconManager.setMonitorNotifier(this);
            //beaconManager.startMonitoringBeaconsInRegion(new Region("all beacons region", null, null, null));
            beaconManager.startMonitoringBeaconsInRegion(new Region("region1", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), Identifier.parse("1")));
            beaconManager.startMonitoringBeaconsInRegion(new Region("region2", Identifier.parse("bf513d02-5ce1-411f-81f2-96d270f1cb2e"), Identifier.parse("1"), Identifier.parse("1")));
            beaconManager.startMonitoringBeaconsInRegion(new Region("region3", Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e1"), Identifier.parse("111"), Identifier.parse("22")));
            beaconManager.startMonitoringBeaconsInRegion(new Region("region4", Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e2"), Identifier.parse("333"), Identifier.parse("44")));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if(beaconManager.getForegroundServiceNotificationId() == 456){

        }else{

        }
        beaconManager.enableForegroundServiceScanning(start_foregroundservice_notificattion().build(), 456);

        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        beaconManager.startMonitoring(wildcardRegion);
        beaconManager.startRangingBeacons(wildcardRegion);*/







        // For the above foreground scanning service to be useful, you need to disable
        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
        // cycle that would otherwise be disallowed by the operating system.
        //


        /*Log.d(TAG, "setting up background monitoring in app onCreate");
        beaconManager.addMonitorNotifier(this);

        // If we were monitoring *different* regions on the last run of this app, they will be
        // remembered.  In this case we need to disable them here
        for (Region region: beaconManager.getMonitoredRegions()) {
            beaconManager.stopMonitoring(region);
        }*/


        // beaconManager.startMonitoring(wildcardRegion);


    }
    private boolean isNotificationVisible() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent test;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            test = PendingIntent.getActivity(getApplicationContext(), 456, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            test = PendingIntent.getActivity(getApplicationContext(), 456, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        }

        return test != null;
    }

    public Notification.Builder start_foregroundservice_notificattion()
    {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }

        return builder;
    }

    private void sendNotification(String title,String uuid) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Beacon Reference Notifications", "Beacon Reference Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, channel.getId());
        }
        else {
            builder = new Notification.Builder(this);
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //builder.setContentTitle("I detect a beacon");
        builder.setContentTitle(title);
        //builder.setContentText("Tap here to see details in the reference app");
        builder.setContentText(uuid);
        builder.setContentIntent(resultPendingIntent);
        Random notification_id = new Random();
        notificationManager.notify(notification_id.nextInt(100), builder.build());
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d("Office_b_add", "didEnterRegion");
        insideRegion = true;
        // Send a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        try {
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                Log.d("state","Your application is in background state");
                if(region.getId1().toString()!=null)
                {
                    Log.d("Office_bm_add", "Sending notification.");

                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());


                    // sendNotification("Enter beacon",region.getId1().toString());
                    ParseJsonData(region.getId1().toString(),true);
                }
            }else{
                Log.d("state","Application is in forground state");
                if(region.getId1().toString()!=null)
                {
                    Log.d("Office_bm_add", "Sending notification.");

                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());


                    // sendNotification("Enter beacon",region.getId1().toString());
                    ParseJsonData(region.getId1().toString(),false);
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void didExitRegion(Region region) {
        Log.d("Office_bm_add", "didExitRegion");
        insideRegion = false;

        try {
            if(region.getId1().toString()!=null)
            {
               // sendNotification("Exit beacon",region.getId1().toString());

                Log.d("Office_bm_add"," id : "+region.getUniqueId());
                Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                Log.d("Office_bm_add"," id : "+region.getId1());
                Log.d("Office_bm_add"," id : "+region.getId2());
                Log.d("Office_bm_add"," id : "+region.getId3());
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d("Office_bm_add", "didDetermineStateForRegion");
        Log.d("Office_bm_add"," state : "+state);





        if(state==1)
        {
            try {
                Log.d("Office_bm_add","Deter id : "+region.getUniqueId());
                Log.d("Office_bm_add","Deter id : "+region.getId1());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
        {

            try {
                Log.d("Office_bm_add"," Deter : "+region.getUniqueId());
                Log.d("Office_bm_add"," Deter : "+region.getId1());
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBluetooth(true);
        if(isLocationEnabled(Scan_Activity.this)){

///////
            getscan_allBeacons();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enable Loaction Service");
            builder.setMessage("Enable Location To Find SSID");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }

    }

    public void getscan_allBeacons()
    {
        beaconManager.getBeaconParsers().clear();

        // The example shows how to find iBeacon.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));


        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0)
                {
                    Log.d("scanbeacon", "didRangeBeaconsInRegion called with beacon count:  "+beacons.size());
                    Beacon firstBeacon = beacons.iterator().next();
                    try {

                        for(Beacon beacon:beacons)
                        {

                            String uuid=String.valueOf(beacon.getId1());
                            Log.d("scanbeacon"," uuid "+uuid);


                            JSONObject obj = new JSONObject(Constant.jsondata);
                            JSONArray m_jArry = obj.getJSONObject("products").getJSONArray("phones");

                            for(int i=0;i<m_jArry.length();i++)
                            {

                                JSONObject object = m_jArry.getJSONObject(i);
                                Log.d("uuid_for",""+object.getString("id"));

                                if(uuid.trim().equalsIgnoreCase(object.getString("id")))
                                {
                                    ProductBean bean = new ProductBean();
                                    bean.setId(object.getString("id"));
                                    bean.setName(object.getString("name"));
                                    bean.setImageurl(object.getString("imageUrl"));
                                    Log.d("uuid_if",""+object.getString("id"));
                                    Log.d("uuid_if_name",""+object.getString("name"));
                                    name = object.getString("name");
                                    imageUrl = object.getString("imageUrl");

                                  //  Log.d("check", String.valueOf(list.contains(bean)));
                                    if (!list.contains(bean))
                                    {
                                        Log.d("uuid_not_equal : ",""+uuid.trim());
                                        list.add(bean);

                                    }
                                    else
                                    {
                                        Log.d("uuid_equal : ",""+uuid.trim());
                                    }

                                }
                            }





                        }





                        if(list.size()>0)
                        {
                            Product_Adapter adapter = new Product_Adapter(list,Scan_Activity.this);
                            recyclerView.setAdapter(adapter);
                            frm_layout.setVisibility(View.GONE);
                        }else{
                            Product_Adapter adapter = new Product_Adapter(list,Scan_Activity.this);
                            recyclerView.setAdapter(adapter);
                            frm_layout.setVisibility(View.VISIBLE);
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    //logToDisplay("The first beacon " + firstBeacon.toString() + " is about " + firstBeacon.getDistance() + " meters away.");
                }
            }

        };

        beaconManager.startMonitoring(MyApplicationApollo.wildcardRegion);
        beaconManager.startRangingBeacons(MyApplicationApollo.wildcardRegion);
        beaconManager.addRangeNotifier(rangeNotifier);
        //beaconManager.startRangingBeacons(BeaconReferenceApplication.wildcardRegion);
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    public boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT);
                int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);
                return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.enable();
            }
            else
            {
                bluetoothAdapter.enable();
            }
        } else if (!enable && isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT);
                int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);
                return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.disable();
            }
            else
            {
                bluetoothAdapter.disable();
            }
        }
        return true;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("sample.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void  ParseJsonData(String uuid,boolean isbackground){
        try {
            Log.d("uuid_pars",uuid);
            JSONObject obj = new JSONObject(Constant.jsondata);
            JSONArray m_jArry = obj.getJSONObject("products").getJSONArray("phones");
            for(int i=0;i<m_jArry.length();i++)
            {

                JSONObject object = m_jArry.getJSONObject(i);
                Log.d("uuid_for",""+object.getString("id"));

                if(uuid.trim().equalsIgnoreCase(object.getString("id")))
                {
                    ProductBean bean = new ProductBean();
                    bean.setId(object.getString("id"));
                    bean.setName(object.getString("name"));
                    bean.setImageurl(object.getString("imageUrl"));
                    Log.d("uuid_if",""+object.getString("id"));
                    Log.d("uuid_if_name",""+object.getString("name"));
                    name = object.getString("name");
                    imageUrl = object.getString("imageUrl");
                    list.add(bean);
                }

            }

            if(isbackground){
                createNotification(name,imageUrl,"test");
            }else{
                runOnUiThread(() -> {
                    Log.d("list",""+list.size());
                    if(list.size()>0){
                        Product_Adapter adapter = new Product_Adapter(list,Scan_Activity.this);
                        recyclerView.setAdapter(adapter);
                        frm_layout.setVisibility(View.GONE);
                    }else{
                        Product_Adapter adapter = new Product_Adapter(list,Scan_Activity.this);
                        recyclerView.setAdapter(adapter);
                        frm_layout.setVisibility(View.VISIBLE);
                    }

                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void createNotification(String title, String imageUrl,
                                    String channedId) {
        Intent intent = new Intent(this, Scan_Activity.class);
        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channedId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setLights(Color.BLUE, 500, 500)
                        .setVibrate(new long[]{500, 500, 500})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        //setContentText(content)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        // Since android Oreo notification channel is needed.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channedId,
                    channedId,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

      //  String imageUrl = "https://raw.githubusercontent.com/TutorialsBuzz/cdn/main/android.jpg";

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //largeIcon
                       // notificationBuilder.setLargeIcon(resource);
                        //Big Picture
                        notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(resource));

                        Notification notification = notificationBuilder.build();
                        notificationManager.notify(NotificationID.getID(), notification);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    static class NotificationID {
        private final static AtomicInteger c = new AtomicInteger(100);

        public static int getID() {
            return c.incrementAndGet();
        }
    }

}