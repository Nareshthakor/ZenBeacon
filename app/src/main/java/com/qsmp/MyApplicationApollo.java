package com.qsmp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;

import com.qsmplibrary.altbeacon.beacon.Beacon;
import com.qsmplibrary.altbeacon.beacon.BeaconManager;
import com.qsmplibrary.altbeacon.beacon.BeaconParser;
import com.qsmplibrary.altbeacon.beacon.Identifier;
import com.qsmplibrary.altbeacon.beacon.MonitorNotifier;
import com.qsmplibrary.altbeacon.beacon.Region;

import com.qsmplibrary.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.Random;

/**
 * Created by admin on 09-08-2017.
 */

public class MyApplicationApollo extends Application
{
    private BackgroundPowerSaver backgroundPowerSaver;
    private static MyApplicationApollo mInstance;
    //public static final Region wildcardRegion = new Region("wildcardRegion", null, null, null);
     public static final Region wildcardRegion = new Region("all-beacons", null, null, null);

    public static boolean insideRegion = false;
    public void onCreate() {
        super.onCreate();

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        mInstance = this;

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();

        // The example shows how to find iBeacon.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        //beaconManager.setMonitorNotifier(this);
        //beaconManager.startMonitoring(new Region("region 1", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), Identifier.parse("1")));
        try {
          //  beaconManager.setMonitorNotifier(this);
            //beaconManager.startMonitoringBeaconsInRegion(new Region("all beacons region", null, null, null));
           // beaconManager.startMonitoringBeaconsInRegion(new Region("region1", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), Identifier.parse("1")));
           // beaconManager.startMonitoringBeaconsInRegion(new Region("region2", Identifier.parse("bf513d02-5ce1-411f-81f2-96d270f1cb2e"), Identifier.parse("1"), Identifier.parse("1")));
          //  beaconManager.startMonitoringBeaconsInRegion(new Region("region3", Identifier.parse("e2c56db5-dffb-48d2-b060-d0f5a71096e1"), Identifier.parse("111"), Identifier.parse("22")));
           // beaconManager.startMonitoringBeaconsInRegion(new Region("region4", Identifier.parse("5861636C-716E-2301-BE01-6C731222F001"), Identifier.parse("333"), Identifier.parse("44")));

        } catch (Exception e) {
            e.printStackTrace();
        }




        //beaconManager.setDebug(true);
      /*  beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);*/
        /*beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d("Office_b_add", "didEnterRegion");
                insideRegion = true;
                // Send a notification to the user whenever a Beacon
                // matching a Region (defined above) are first seen.
                Log.d("Office_bm_add", "Sending notification.");

                Log.d("Office_bm_add"," id : "+region.getUniqueId());
                Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                Log.d("Office_bm_add"," id : "+region.getId1());
                Log.d("Office_bm_add"," id : "+region.getId2());
                Log.d("Office_bm_add"," id : "+region.getId3());
                Log.d("Office_bm_add"," id : "+region.getUniqueId());


                sendNotification("I detect a beacon");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d("Office_bm_add", "didExitRegion");
                insideRegion = false;
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d("Office_bm_add", "didDetermineStateForRegion");
                Log.d("Office_bm_add"," state : "+state);



                if(state==1)
                {
                    Log.d("Office_bm_add"," id : "+region.getIdentifiers().size());

                    //Log.d("didDetermine"," id : "+region.getIdentifiers().get(0).toString());
                    //Log.d("Office_bm","uuid"+region.getId1().toString());
                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());
                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                }
                else
                {
                    sendNotification("Exit beacon");

                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());
                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                }
            }
        });*/



        /*beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d("Office_bm", "didEnterRegion");
                insideRegion = true;
                // Send a notification to the user whenever a Beacon
                // matching a Region (defined above) are first seen.
                Log.d("Office_bm", "Sending notification.");

                Log.d("Office_bm"," id : "+region.getUniqueId());
                Log.d("Office_bm"," id : "+region.getBluetoothAddress());
                Log.d("Office_bm"," id : "+region.getId1());
                Log.d("Office_bm"," id : "+region.getId2());
                Log.d("Office_bm"," id : "+region.getId3());
                Log.d("Office_bm"," id : "+region.getUniqueId());


                sendNotification("I detect a beacon");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d("Office_bm", "didExitRegion");
                insideRegion = false;
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d("Office_bm", "didDetermineStateForRegion");
                Log.d("Office_bm"," state : "+state);



                if(state==1)
                {
                    Log.d("Office_bm"," id : "+region.getIdentifiers().size());

                    //Log.d("didDetermine"," id : "+region.getIdentifiers().get(0).toString());
                    //Log.d("Office_bm","uuid"+region.getId1().toString());

                }
                else
                {
                    //sendNotification("Exit beacon");
                }


            }
        });*/


        //beaconManager.enableForegroundServiceScanning(start_foregroundservice_notificattion().build(), 456);


        // For the above foreground scanning service to be useful, you need to disable
        // JobScheduler-based scans (used on Android 8+) and set a fast background scan
        // cycle that would otherwise be disallowed by the operating system.
        //
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);



        /*Log.d(TAG, "setting up background monitoring in app onCreate");
        beaconManager.addMonitorNotifier(this);

        // If we were monitoring *different* regions on the last run of this app, they will be
        // remembered.  In this case we need to disable them here
        for (Region region: beaconManager.getMonitoredRegions()) {
            beaconManager.stopMonitoring(region);
        }*/


       // beaconManager.startMonitoring(wildcardRegion);
        beaconManager.startMonitoring(wildcardRegion);
        beaconManager.startRangingBeacons(wildcardRegion);


    }

    public static synchronized MyApplicationApollo getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver_Qn.ConnectivityReceiverListener listener) {
        ConnectivityReceiver_Qn.connectivityReceiverListener = listener;
    }

    /*public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/

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


   /* private void sendNotification(String title,String uuid) {
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

            if(region.getId1().toString()!=null)
            {
                Log.d("Office_bm_add", "Sending notification.");

                Log.d("Office_bm_add"," id : "+region.getUniqueId());
                Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                Log.d("Office_bm_add"," id : "+region.getId1());
                Log.d("Office_bm_add"," id : "+region.getId2());
                Log.d("Office_bm_add"," id : "+region.getId3());


                sendNotification("Enter beacon",region.getId1().toString());
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
                sendNotification("Exit beacon",region.getId1().toString());

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
    }*/
}