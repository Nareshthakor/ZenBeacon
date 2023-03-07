package com.qsmp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.qsmp.Cardviewpager_extra.Scan_Adapter;
import com.qsmplibrary.altbeacon.beacon.BeaconManager;
import com.qsmplibrary.altbeacon.beacon.BeaconParser;
import com.qsmplibrary.altbeacon.beacon.Identifier;
import com.qsmplibrary.altbeacon.beacon.MonitorNotifier;
import com.qsmplibrary.altbeacon.beacon.Region;
import com.qsmplibrary.altbeacon.beacon.powersave.BackgroundPowerSaver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class LocationTracker extends Service implements MonitorNotifier {



    private final int NOTIFICATION_ID = 9083150;
    private final String CHANNEL_ID = "test123";
    private final String CHANNEL_ID_NAME = "test123";
    public static final Region wildcardRegion = new Region("all-beacons", null, null, null);
    private BackgroundPowerSaver backgroundPowerSaver;
    SharedPreferences sp_userdetail;
    String saml_token;
    private static AsyncHttpClient client ;
    BeaconManager beaconManager;
    @Override
    public void onCreate() {
        super.onCreate();
        try {

            sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);
            client= new AsyncHttpClient();

            saml_token=sp_userdetail.getString("saml_token",null);

            backgroundPowerSaver = new BackgroundPowerSaver(getApplicationContext());
           // mInstance = this;
            BeaconManager.getInstanceForApplication(getApplicationContext()).setMonitorNotifier(this);
            beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());

            //beaconManager = BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().clear();

            // The example shows how to find iBeacon.
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

            //beaconManager.setMonitorNotifier(this);


            Notification notification = null;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(null, null);
                channel.setShowBadge(false);
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                //notificationManager.deleteNotificationChannel(CHANNEL_ID);
                notificationManager.createNotificationChannel(channel);

                 notification = createNotification(getApplicationContext(), CHANNEL_ID, 0);
                if (notification == null) {
                    notification = new NotificationCompat.Builder(this, CHANNEL_ID).build();
                }
                startForeground(NOTIFICATION_ID, notification);

            }

            //beaconManager.startMonitoringBeaconsInRegion(new Region("region2", Identifier.parse("bf513d02-5ce1-411f-81f2-96d270f1cb2e"), Identifier.parse("1"), Identifier.parse("1")));
            beaconManager.enableForegroundServiceScanning(notification, NOTIFICATION_ID);
            beaconManager.setEnableScheduledScanJobs(false);
            beaconManager.setBackgroundBetweenScanPeriod(0);
            beaconManager.setBackgroundScanPeriod(1100);





        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private Notification createNotification(Context context, String channelid, int type) {
        try {
            return new NotificationCompat.Builder(context, channelid)
                    .setContentText("Location Tracking")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("call service","ok");
        if (intent != null) {

            if(intent.getAction()!=null){


                if(intent.getAction().equalsIgnoreCase("stoplocationupdate")){
                    Log.d("service","stop");
                    stopLocationService(this);
                }else{
                    //init();
                    Call_beaconlist();
                    beaconManager.startMonitoring(wildcardRegion);
                    beaconManager.startRangingBeacons(wildcardRegion);
                    Log.d("service","start");
                }
            }else{
               // init();
                Call_beaconlist();
                beaconManager.startMonitoring(wildcardRegion);
                beaconManager.startRangingBeacons(wildcardRegion);
                Log.d("service else","start");
            }
        }



        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onDestroy() {
        // WakeLocker.releasePartialLock();
        super.onDestroy();
        Log.d("onDestroy","onDestroy");

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("onLowMemory","onLowMemory");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("onTaskRemoved","onTaskRemoved");

    }

    public void stopLocationService(Context context) {
        try {

            stopForeground(true);

            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void didEnterRegion(Region region) {
        Log.d("Service_call", "didEnterRegion");
        // Send a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.

        try {
            if(region.getId1().toString()!=null)
            {
                Log.d("Service_call", "Sending notification.");

                Log.d("Service_call"," id : "+region.getUniqueId());
                Log.d("Service_call"," id : "+region.getBluetoothAddress());
                Log.d("Service_call"," id : "+region.getId1());
                Log.d("Service_call"," id : "+region.getId2());
                Log.d("Service_call"," id : "+region.getId3());


                ParseJsonData(region.getId1().toString(),true);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        /*try {
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                Log.d("Service_call","Your application is in background state");
                if(region.getId1().toString()!=null)
                {
                    Log.d("Service_call", "Sending notification.");

                    Log.d("Service_call"," id : "+region.getUniqueId());
                    Log.d("Service_call"," id : "+region.getBluetoothAddress());
                    Log.d("Service_call"," id : "+region.getId1());
                    Log.d("Service_call"," id : "+region.getId2());
                    Log.d("Service_call"," id : "+region.getId3());


                    ParseJsonData(region.getId1().toString(),true);
                }
            }else{
                Log.d("Service_call","Application is in forground state");

                if(region.getId1().toString()!=null)
                {
                    Log.d("Service_call", "Sending notification.");

                    Log.d("Service_call"," id : "+region.getUniqueId());
                    Log.d("Service_call"," id : "+region.getBluetoothAddress());
                    Log.d("Service_call"," id : "+region.getId1());
                    Log.d("Service_call"," id : "+region.getId2());
                    Log.d("Service_call"," id : "+region.getId3());


                    ParseJsonData(region.getId1().toString(),true);
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }*/


    }

    @Override
    public void didExitRegion(Region region) {



    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
       /* Log.d("Office_bm_add", "didDetermineStateForRegion");
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

        }*/
    }


    private void  ParseJsonData(String uuid,boolean isbackground){
        try {
            Log.d("uuid_pars",uuid);
            if(isbackground)
            {
                try {
                    //{"api_unique_key":"f06a8c12f45f80721e5e4777bd8c5df1","uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"}
                    JSONObject Beacon_Jobj=new JSONObject();

                    Beacon_Jobj.put("api_unique_key",saml_token);
                    Beacon_Jobj.put("uuid",uuid);

                    Log.d("Beacon_Jason",Beacon_Jobj.toString());

                    Get_Beacon_Notification_Data(Beacon_Jobj);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                //createNotification(name,imageUrl,"test");
            }
            else
            {
                //app is running
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void Get_Beacon_Notification_Data(JSONObject jsonObject) throws UnsupportedEncodingException
    {
        RequestParams params = new RequestParams();
        params.put("",jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        final String url= ConfigQn.Main_URL_Beacon+"get-campaign-detail";
        Log.d("url_service_detail",url);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MyCustomSSLFactory socketFactory = new MyCustomSSLFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(socketFactory);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        client.setTimeout(20*1000);
        responseHandler.setUsePoolThread(true);
        client.post(getApplicationContext(),url,entity,"application/json",responseHandler);

    }
    private JsonHttpResponseHandler  responseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d("res_service_detail",response.toString());

            try{
                if(response.length()>0)
                {
                    if(response.getInt("responseCode")==200)
                    {

                        //Toast.makeText(SplashActivityQN.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();


                        JSONObject responseData=response.getJSONObject("responseData");
                        String Title_Name=responseData.getString("name");

                        JSONArray BeaconArray=responseData.getJSONArray("Advertise_Detail");
                        Log.d("BeaconArray",""+BeaconArray.toString());


                        if(BeaconArray.length()>0)
                        {


                            JSONObject jsonObject=BeaconArray.getJSONObject(0);

                            String media_type=jsonObject.getString("media_type");
                            String media_text=jsonObject.getString("media_text");
                            String media_url=jsonObject.getString("media_url");

                            send_createNotification(media_text,media_url,"test");


                             /*   for (int i = 0; i < BeaconArray.length(); i++)
                                {
                                    Scan_Beacon_Bean itemData=null;
                                    JSONObject jsonObject=BeaconArray.getJSONObject(0);

                                    String media_type=jsonObject.getString("media_type");
                                    String media_text=jsonObject.getString("media_text");
                                    String media_url=jsonObject.getString("media_url");

                                    itemData = new Scan_Beacon_Bean(media_text,media_text,media_url);
                                    adapter.addCardItem(itemData);

                                    Log.d("namedia_typeme",""+media_type);
                                    Log.d("media_text",""+media_text);
                                    Log.d("media_url",""+media_url);
                                }
                                */

                        }




                    }
                    else
                    {
                        Log.d("responseMessage","responseMessage error");


                    }
                }
                else
                {
                    Log.d("somethingwentwrong","something went wrong");

                }


            }catch (Exception e){
                e.printStackTrace();
            }
               /* dialog.dismiss();
                dialog.cancel();*/

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
              /*  dialog.dismiss();
                dialog.cancel();*/
            Log.d("onFailure","JSONObject something went wrong");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.d("onFailure","responseString something went wrong");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("onFailure","JSONArray something went wrong");
        }
    };
    private void send_createNotification(String title, String imageUrl, String channedId) {
        Intent intent = new Intent(getApplicationContext(), Scan_Extra_Activity.class);
        Bundle b=new Bundle();
        b.putString("call_scanactivity", "notification");
        intent.putExtras(b);
        //intent.putExtra("call_scanactivity","notification");
        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
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


        /*notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(imageUrl));

        Notification notification = notificationBuilder.build();
        notificationManager.notify(Scan_Activity.NotificationID.getID(), notification);*/

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
                        notificationManager.notify(Scan_Activity.NotificationID.getID(), notification);
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

    private void Call_beaconlist(){
        try {
            //{"api_unique_key":"f06a8c12f45f80721e5e4777bd8c5df1"}
            JSONObject Beacon_Jobj=new JSONObject();

            Beacon_Jobj.put("api_unique_key",saml_token);

            Log.d("Beacon_Jason",Beacon_Jobj.toString());

            Get_Beacon_List(Beacon_Jobj);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void Get_Beacon_List(JSONObject jsonObject) throws UnsupportedEncodingException
    {

        RequestParams params = new RequestParams();
        params.put("",jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        final String url= ConfigQn.Main_URL_Beacon+"get-beacon-list";
        Log.d("url_service",url);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MyCustomSSLFactory socketFactory = new MyCustomSSLFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(socketFactory);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        client.setTimeout(20*1000);
        client.post(getApplicationContext(),url,entity,"application/json",new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res_service",response.toString());

                try{
                    if(response.length()>0)
                    {

                       /* dialog.dismiss();
                        dialog.cancel();*/
                        if(response.getInt("responseCode")==200)
                        {

                            //Toast.makeText(SplashActivityQN.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                            JSONArray BeaconArray=response.getJSONArray("responseData");
                            Log.d("BeaconArray",""+BeaconArray.toString());



                            if(BeaconArray.length()>0)
                            {

                                for (int i = 0; i < BeaconArray.length(); i++)
                                {
                                    JSONObject jsonObject=BeaconArray.getJSONObject(i);

                                    String name=jsonObject.getString("name");
                                    String uuid=jsonObject.getString("uuid");
                                    String mac=jsonObject.getString("mac");
                                    String location=jsonObject.getString("location");


                                    Log.d("name",""+name);
                                    Log.d("uuid",""+uuid);
                                    Log.d("mac",""+mac);
                                    Log.d("location",""+location);

                                    beaconManager.startMonitoringBeaconsInRegion(new Region("region"+i, Identifier.parse(uuid), Identifier.parse("1"), Identifier.parse("1")));
                                }




                            }
                        }
                        else
                        {
                            Log.d("responseMessage","responseMessage error");


                        }
                    }
                    else
                    {
                        Log.d("something went wrong","something went wrong");

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
               /* dialog.dismiss();
                dialog.cancel();*/

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
              /*  dialog.dismiss();
                dialog.cancel();*/
                Log.d("onFailure","JSONObject something went wrong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("onFailure","responseString something went wrong");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("onFailure","JSONArray something went wrong");
            }
        });

    }

}
