package com.qsmp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;
import com.qsmp.Cardviewpager_extra.Scan_Adapter;
import com.qsmp.Cardviewpager_extra.GalleryTransformer;
import com.qsmp.Cardviewpager_extra.WormDotsIndicator;
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
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Scan_Extra_Activity extends AppCompatActivity implements MonitorNotifier {

    private ViewPager mViewPager;
    WormDotsIndicator dot3;
    BeaconManager beaconManager;
    private BackgroundPowerSaver backgroundPowerSaver;
    public static boolean insideRegion = false;
    FrameLayout frm_layout;
    String name,imageUrl;
    LinearLayout ll_scan_back;
    ArrayList<Double> distancevalue=new ArrayList<>();
    ArrayList<String> nameiddistancevalue=new ArrayList<>();
    String BeaconIDdata="notfound";
    String Mainbeaconid="";
    Identifier namespaceId;
    double distancefinal;
    String StrNamespaceId;
    Beacon closest = null;
    double edistonedistance;
    RelativeLayout rl_viewpager;
    TextView txt_actionbar_name;
    String call_scan_activity_Data="";
    ArrayList<String> beacon_list;
    SharedPreferences sp_userdetail;
    String saml_token;
    private static AsyncHttpClient client ;
    connectionDector dector;
    int threetimesfalse=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_extra);

        sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);
        client= new AsyncHttpClient();
        dector=new connectionDector(Scan_Extra_Activity.this);

        saml_token=sp_userdetail.getString("saml_token",null);





        mViewPager= (ViewPager) findViewById(R.id.vp_main_pager);
        dot3=findViewById(R.id.dot3);
        frm_layout=findViewById(R.id.frm_layout);
        rl_viewpager=findViewById(R.id.rl_viewpager);
        ll_scan_back=findViewById(R.id.ll_scan_back);
        txt_actionbar_name=findViewById(R.id.txt_actionbar_name);

        rl_viewpager.setVisibility(View.GONE);
        frm_layout.setVisibility(View.VISIBLE);
        txt_actionbar_name.setText("Finding Deals");

        final RippleBackground rippleBackground= findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        //mInstance = this;
        BeaconManager.getInstanceForApplication(this).setMonitorNotifier(this);
        beaconManager = BeaconManager.getInstanceForApplication(this);

        try {


            Bundle b = getIntent().getExtras();
            call_scan_activity_Data = b.getString("call_scanactivity");
            //call_scan_activity_Data=getIntent().getStringExtra("call_scanactivity");
            Log.d("call_scan_activity_Data",""+call_scan_activity_Data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d("call_scan_activity_Data","Exception "+call_scan_activity_Data);
        }

        Call_beaconlist();



        ll_scan_back.setOnClickListener(v -> {
            if(call_scan_activity_Data.equalsIgnoreCase("notification"))
            {
                Intent i=new Intent(Scan_Extra_Activity.this,Qsmp_Dashboard_Activity.class);
                startActivity(i);
            }
            else
            {
                onBackPressed();
            }

        });

    }

    @Override
    public void onBackPressed() {
        if(call_scan_activity_Data.equalsIgnoreCase("notification"))
        {
            Intent i=new Intent(Scan_Extra_Activity.this,Qsmp_Dashboard_Activity.class);
            startActivity(i);
        }
        else
        {
            super.onBackPressed();

        }
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d("Scan_Act", "didEnterRegion");
        insideRegion = true;
        // Send a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
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
        Log.d("Scan_Act", "didExitRegion");
        insideRegion = false;

       /* try {
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
        }*/


    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
       /* Log.d("Office_bm_add", "didDetermineStateForRegion");
        Log.d("Office_bm_add"," state : "+state);*/





       /* if(state==1)
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

    @Override
    protected void onResume() {
        super.onResume();
        setBluetooth(true);
        if(isLocationEnabled(Scan_Extra_Activity.this)){

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
                            }
                            else
                            {
                                set_view_pager_data_beacon(BeaconIDdata);
                            }

                        }
                    }
                    else
                    {
                        BeaconIDdata = Mainbeaconid;
                        Log.d("uuid_BeaconIDdata"," notfound "+BeaconIDdata);
                        set_view_pager_data_beacon(Mainbeaconid);
                    }



                }
                else
                {
                    Log.d("getscan_allBeacons","beqcon size 0");
                    if(BeaconIDdata.equalsIgnoreCase("notfound"))
                    {
                        rl_viewpager.setVisibility(View.GONE);
                        frm_layout.setVisibility(View.VISIBLE);
                        txt_actionbar_name.setText("Finding Deals");
                    }

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



    public void set_view_pager_data_beacon(String uuid)
    {
        try {
            //{"api_unique_key":"f06a8c12f45f80721e5e4777bd8c5df1","uuid":"2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"}

            JSONObject Beacon_Jobj=new JSONObject();


            Beacon_Jobj.put("api_unique_key",saml_token);
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
        final String url= ConfigQn.Main_URL_Beacon+"get-campaign-detail";
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
        client.post(Scan_Extra_Activity.this,url,entity,"application/json",new JsonHttpResponseHandler()
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

                            rl_viewpager.setVisibility(View.VISIBLE);
                            frm_layout.setVisibility(View.GONE);

                            //Toast.makeText(SplashActivityQN.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                            String data = response.getString("responseData");
                            Object json = new JSONTokener(data).nextValue();


                            JSONObject responseData=response.getJSONObject("responseData");

                            if(responseData.length()>0)
                            {

                                String Title_Name=responseData.getString("name");

                                JSONArray BeaconArray=responseData.getJSONArray("Advertise_Detail");
                                Log.d("BeaconArray",""+BeaconArray.toString());

                                if(BeaconArray.length()>0)
                                {

                                    Scan_Adapter adapter = new Scan_Adapter(Scan_Extra_Activity.this);
                                    for (int i = 0; i < BeaconArray.length(); i++)
                                    {
                                        Scan_Beacon_Bean itemData=null;
                                        JSONObject jsonObject=BeaconArray.getJSONObject(i);

                                        String media_type=jsonObject.getString("media_type");
                                        String media_text=jsonObject.getString("media_text");
                                        String media_url=jsonObject.getString("media_url");
                                        String media_redirection_url=jsonObject.getString("media_redirection_url");
                                        itemData = new Scan_Beacon_Bean(media_text,media_text,media_url,media_redirection_url);
                                        adapter.addCardItem(itemData);

                                        Log.d("namedia_typeme",""+media_type);
                                        Log.d("media_text",""+media_text);
                                        Log.d("media_url",""+media_url);
                                    }

                                    txt_actionbar_name.setText(Title_Name);
                                    set_adapter_Data(adapter,BeaconArray.length());




                                }


                            }
                            else
                            {
                                rl_viewpager.setVisibility(View.GONE);
                                frm_layout.setVisibility(View.VISIBLE);
                                txt_actionbar_name.setText("Finding Deals");

                                //Toast.makeText(Scan_Extra_Activity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();
                            }



                        }
                        else
                        {

                            rl_viewpager.setVisibility(View.GONE);
                            frm_layout.setVisibility(View.VISIBLE);
                            txt_actionbar_name.setText("Finding Deals");

                            Toast.makeText(Scan_Extra_Activity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();


                        }
                    }
                    else
                    {
                        Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Scan_Extra_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

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
        client.post(Scan_Extra_Activity.this,url,entity,"application/json",responseHandler);

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


    public void set_adapter_Data(Scan_Adapter adapter,int array_length)
    {


        //adapter.setElevation(0.6f);
        //viewPager.setAdapter(adapter);

        mViewPager.setAdapter(adapter);
        if(array_length>1)
        {
            dot3.setVisibility(View.VISIBLE);
            mViewPager.setOffscreenPageLimit(array_length);
        }
        else
        {
            dot3.setVisibility(View.GONE);
        }

        mViewPager.setPageTransformer(true,new GalleryTransformer());


        dot3.setViewPager(mViewPager);
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


    private void createNotification(String title, String imageUrl, String channedId) {
        Intent intent = new Intent(Scan_Extra_Activity.this, Scan_Extra_Activity.class);
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

                                    //BeaconManager.getInstanceForApplication(Scan_Extra_Activity.this).

                                    BeaconManager.getInstanceForApplication(Scan_Extra_Activity.this).startMonitoringBeaconsInRegion(new Region("region"+i, Identifier.parse(uuid), Identifier.parse("1"), Identifier.parse("1")));
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