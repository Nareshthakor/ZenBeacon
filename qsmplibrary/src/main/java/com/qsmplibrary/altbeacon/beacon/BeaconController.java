package com.qsmplibrary.altbeacon.beacon;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.qsmplibrary.R;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class BeaconController implements Printer, MonitorNotifier {

    private  Printer printer;
    BeaconManager beaconManager;
    ArrayList<Double> distancevalue=new ArrayList<>();
    ArrayList<String> nameiddistancevalue=new ArrayList<>();
    String BeaconIDdata="notfound";
    String Mainbeaconid="";
    Identifier namespaceId;
    double distancefinal;
    String StrNamespaceId;
    Beacon closest = null;
    double edistonedistance;
    int threetimesfalse=0;
    boolean url_response=false;
    private static AsyncHttpClient client=new AsyncHttpClient();
    Context m_context;
    String Str_main_key_Token="";
    Class<?> m_class;
    public static final Region wildcardRegion = new Region("all-beacons", null, null, null);
    /*public PrinterController(Printer printer) {
        this.printer = printer;
    }*/


    public BeaconController(Printer printer, Context context,String key,Class<?> classs_data) {
        this.printer = printer;
        this.m_context = context;
        this.Str_main_key_Token = key;
        this.m_class = classs_data;

        getBeacon_Validation(Str_main_key_Token);


    }
    @Override
    public String print(String message) {
        printer.print(message);
        Log.d("Delegate_message",""+message);
        return message;
    }


    public void getBeacon_Validation(String saml_token)
    {

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

        url_response=false;
        RequestParams params = new RequestParams();
        params.put("",jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        //final String url= ConfigQn.Main_URL_Beacon+"get-beacon-list";
        final String url= "https://rudder.dev.qntmnet.com/wsmp/beacon-api/get-beacon-list";
        Log.d("url",url);
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
        client.post(m_context,url,entity,"application/json",new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res",response.toString());

                try{
                    if(response.length()>0)
                    {

                       /* dialog.dismiss();
                        dialog.cancel();*/
                        if(response.getInt("responseCode")==200)
                        {
                            url_response=true;
                            print("key security Success");
                            getscan_allBeacons();



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

                                    //BeaconManager.getInstanceForApplication(Scan_Extra_Activity.this).

                                    BeaconManager.getInstanceForApplication(m_context).startMonitoringBeaconsInRegion(new Region("region"+i, Identifier.parse(uuid), Identifier.parse("1"), Identifier.parse("1")));
                                }




                            }


                        }
                        else
                        {
                            print("key security Success");
                            url_response=false;
                            //Toast.makeText(m_context, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                        }
                    }
                    else
                    {
                        //Toast.makeText(m_context, "something went wrong", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(m_context, "something went wrong", Toast.LENGTH_LONG).show();
                url_response=false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(m_context, "something went wrong", Toast.LENGTH_LONG).show();
                url_response=false;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(m_context, "something went wrong", Toast.LENGTH_LONG).show();
                url_response=false;
            }
        });



    }



    public void getscan_allBeacons()
    {
        BeaconManager.getInstanceForApplication(m_context).setMonitorNotifier(this);
        beaconManager = BeaconManager.getInstanceForApplication(m_context);
        beaconManager.removeAllRangeNotifiers();

        Log.d("Call_scan_method","getscan_allBeacons");
        beaconManager.getBeaconParsers().clear();

        // The example shows how to find iBeacon.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));


        RangeNotifier rangeNotifier = new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
            {
                double distance = 100.0;
                if (beacons.size() > 0)
                {
                    Log.d("getscan_allBeacons","beqcon size "+beacons.size());
                    //print("beqcon size "+beacons.size());

                    distancevalue.clear();
                    nameiddistancevalue.clear();

                    for (final Beacon beacon : beacons)
                    {


                        //Log.d("uuid_beaconssize", "" + beacons.size());

                        namespaceId = beacon.getId1();
                        StrNamespaceId = String.valueOf(namespaceId);
                        distancefinal=beacon.getDistance();


                        //String Str_Beacon_bt_id_main = beacons.iterator().next().getBluetoothAddress();
                        String Str_Beacon_bt_id_main = beacon.getBluetoothAddress();
                        Log.d("beacon_mac", "" + Str_Beacon_bt_id_main);


                        Log.d("uid", "" + beacon.getId2());



                        Identifier instanceId = beacon.getId2();

                        if (distance > beacon.getDistance()) {
                            closest = beacon;
                            distance = beacon.getDistance();
                            //Log.d("closest", "" +closest);
                            //Log.d("closest", "" +closest.getId1());
                            //Log.d("closest", "" +closest.getDistance());
                        }

                        //Log.d("uuid_", "" + beacon.getId1().toString());
                        distancevalue.add(beacon.getDistance());
                        nameiddistancevalue.add(beacon.getId1().toString());



                        Log.d("namaspaceid", "" + beacon.getId1());
                        Log.d("distance", "" + beacon.getDistance());


                        //Log.d("RangingActivity", "I see a beacon transmitting namespace id: " + namespaceId +" and instance id: " + instanceId +" approximately " + beacon.getDistance() + " meters away.");

                        edistonedistance = beacon.getDistance();
                        // Do we have telemetry data?
                        if (beacon.getExtraDataFields().size() > 0) {
                            long telemetryVersion = beacon.getExtraDataFields().get(0);
                            long batteryMilliVolts = beacon.getExtraDataFields().get(1);
                            long pduCount = beacon.getExtraDataFields().get(3);
                            long uptime = beacon.getExtraDataFields().get(4);

                            //Log.d("RangingActivity", "The above beacon is sending telemetry version " + telemetryVersion + ", has been up for : " + uptime + " seconds" + ", has a battery level of " + batteryMilliVolts + " mV" + ", and has transmitted " + pduCount + " advertisements.");

                        }



                    }




                    Log.d("uuid_", "closestvaludedistance" + Collections.min(distancevalue));
                    Log.d("uuid_nameid", "" + nameiddistancevalue.toString());
                    Log.d("uuid_namedistnce", "" + distancevalue.toString());
                    int minIndex = distancevalue.indexOf(Collections.min(distancevalue));
                    Log.d("uuid_closminindex", "" + minIndex);

                    Log.d("uuid_closestidname",nameiddistancevalue.get(minIndex).toString());

                    Mainbeaconid=nameiddistancevalue.get(minIndex).toString();
                    Log.d("uuid_Mainbeaconid",""+Mainbeaconid);


                    if(!BeaconIDdata.equalsIgnoreCase("notfound"))
                    {


                        Log.d("uuid_BeaconIDdata",""+BeaconIDdata);
                        if (Mainbeaconid.equalsIgnoreCase(BeaconIDdata))
                        {
                            threetimesfalse=0;
                            Log.d("uuid_BeaconIDdata","Mainbeaconid equals" );
                            BeaconIDdata = Mainbeaconid;

                        }
                        else
                        {
                            Log.d("threetimesfalse","before"+threetimesfalse);
                            threetimesfalse=threetimesfalse+1;
                            Log.d("threetimesfalse","update"+threetimesfalse);
                            if(threetimesfalse==4)
                            {
                                threetimesfalse=0;
                                Log.d("uuid_BeaconIDdata","Mainbeaconid not equals" );
                                BeaconIDdata = Mainbeaconid;
                                set_view_pager_data_beacon(Mainbeaconid);
                                print(Mainbeaconid);
                            }
                            else
                            {
                                set_view_pager_data_beacon(BeaconIDdata);
                                print(Mainbeaconid);
                            }

                        }
                    }
                    else
                    {
                        BeaconIDdata = Mainbeaconid;
                        Log.d("uuid_BeaconIDdata"," notfound "+BeaconIDdata);
                        set_view_pager_data_beacon(Mainbeaconid);
                        //print(Mainbeaconid);
                    }



                }
                else
                {
                    Log.d("getscan_allBeacons","beqcon size 0");
                    //print("beqcon size "+beacons.size());
                    if(BeaconIDdata.equalsIgnoreCase("notfound"))
                    {
                        /*rl_viewpager.setVisibility(View.GONE);
                        frm_layout.setVisibility(View.VISIBLE);
                        txt_actionbar_name.setText("Finding Deals");*/
                    }

                }
            }

        };

        beaconManager.startMonitoring(wildcardRegion);
        beaconManager.startRangingBeacons(wildcardRegion);
        beaconManager.addRangeNotifier(rangeNotifier);

        //beaconManager.startRangingBeacons(BeaconReferenceApplication.wildcardRegion);
    }


    public void set_view_pager_data_beacon(String uuid)
    {
        try {
            //{"api_unique_key":"f06a8c12f45f80721e5e4777bd8c5df1","uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"}

            JSONObject Beacon_Jobj=new JSONObject();


            Beacon_Jobj.put("api_unique_key",Str_main_key_Token);
            Beacon_Jobj.put("uuid",uuid);

            Log.d("Beacon_Jason",Beacon_Jobj.toString());

            Get_Beacon_Detail(Beacon_Jobj);
        }catch (Exception e)
        {
            e.printStackTrace();
        }




    }

    private void Get_Beacon_Detail(JSONObject jsonObject) throws UnsupportedEncodingException
    {
        RequestParams params = new RequestParams();
        params.put("",jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        final String url= "https://rudder.dev.qntmnet.com/wsmp/beacon-api/get-campaign-detail";
        Log.d("url",url);
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
        client.post(m_context,url,entity,"application/json",new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res",response.toString());

                try{
                    if(response.length()>0)
                    {
                        if(response.getInt("responseCode")==200)
                        {
                            print(response.toString());
                        }
                        else
                        {
                            print("response nulll");
                        }
                    }
                    else
                    {
                        //Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void didEnterRegion(Region region) {
        Log.d("PrinterController","CAll_didEnterRegion");
        print("didEnterRegion");
        try {
            ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(myProcess);
            if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            {
                Log.d("Office_bm_add","Your application is in background state");
                if(region.getId1().toString()!=null)
                {
                    Log.d("Office_bm_add", "Sending notification.");

                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());

                    // sendNotification("Enter beacon",region.getId1().toString());
                    //ParseJsonData(region.getId1().toString(),true);

                    ParseJsonData(region.getId1().toString(),true);
                }
            }else{
                Log.d("Office_bm_add","Application is in forground state");
                if(region.getId1().toString()!=null)
                {
                    Log.d("Office_bm_add", "Sending notification.");

                    Log.d("Office_bm_add"," id : "+region.getUniqueId());
                    Log.d("Office_bm_add"," id : "+region.getBluetoothAddress());
                    Log.d("Office_bm_add"," id : "+region.getId1());
                    Log.d("Office_bm_add"," id : "+region.getId2());
                    Log.d("Office_bm_add"," id : "+region.getId3());

                    ParseJsonData(region.getId1().toString(),true);
                    // sendNotification("Enter beacon",region.getId1().toString());
                    //ParseJsonData(region.getId1().toString(),false);
                }

            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void didExitRegion(Region region) {
        Log.d("PrinterController","didExitRegion");
        print("didExitRegion");
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.d("PrinterController","didDetermineStateForRegion");
        print("didDetermineStateForRegion");
    }


    private void  ParseJsonData(String uuid,boolean isbackground){
        try {
            Log.d("uuid_pars",uuid);
            if(isbackground)
            {
                try {
                    //{"api_unique_key":"f06a8c12f45f80721e5e4777bd8c5df1","uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"}
                    JSONObject Beacon_Jobj=new JSONObject();

                    Beacon_Jobj.put("api_unique_key",Str_main_key_Token);
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
        //final String url= ConfigQn.Main_URL_Beacon+"get-campaign-detail";
        final String url="https://rudder.dev.qntmnet.com/wsmp/beacon-api/get-campaign-detail";
        Log.d("url",url);
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
        client.post(m_context,url,entity,"application/json",responseHandler);

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

                            createNotification(media_text,media_url,"test");


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



    private void createNotification(String title, String imageUrl, String channedId)
    {
        Intent intent = new Intent(m_context, m_class);
        Bundle b=new Bundle();
        b.putString("call_scanactivity", "notification");
        intent.putExtras(b);
        //intent.putExtra("call_scanactivity","notification");
        PendingIntent pendingIntent=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
            pendingIntent = PendingIntent.getActivity(m_context.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity(m_context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(m_context.getApplicationContext(), channedId)
                        .setSmallIcon(R.drawable.bssid)
                        .setAutoCancel(true)
                        .setLights(Color.BLUE, 500, 500)
                        .setVibrate(new long[]{500, 500, 500})
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(title)
                        .setContentIntent(pendingIntent)
                        //setContentText(content)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        // Since android Oreo notification channel is needed.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(m_context);

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

        Glide.with(m_context.getApplicationContext())
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


