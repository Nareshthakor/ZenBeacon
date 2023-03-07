package com.qsmp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Network_Detail_Activity extends AppCompatActivity {

    boolean mStopHandler = false;
    Handler handler = new Handler();
    WifiManager wifiManager;
    TextView txt_error_notes;
    private TextView txt_tool_strength_name, txt_tool_network_name, txt_tool_bssid_name,txt_tool_ip_name;
    private TextView ll_devices_title;
    LinearLayout ll_devices;


    private static AsyncHttpClient client;
    private boolean backPressedToExitOnce = false;
    ImageView img_about_actionbar_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_detail);

        ll_devices=findViewById(R.id.ll_devices);
        ll_devices_title=findViewById(R.id.ll_devices_title);




        client= new AsyncHttpClient();





        txt_tool_ip_name= findViewById(R.id.txt_tool_ip_name);
        txt_error_notes = findViewById(R.id.txt_error_notes);

        txt_tool_strength_name = findViewById(R.id.txt_tool_strength_name);
        txt_tool_network_name = findViewById(R.id.txt_tool_network_name);
        txt_tool_bssid_name = findViewById(R.id.txt_tool_bssid_name);

        img_about_actionbar_back = findViewById(R.id.img_about_actionbar_back);


        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("linkspeed", "" + wifiInfo.getLinkSpeed()+" "+WifiInfo.LINK_SPEED_UNITS);
        Log.d("linkspeed", "" + wifiInfo.getFrequency());



        img_about_actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // do your stuff - don't create a new runnable here!
                if (!mStopHandler) {
                    // WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager.isWifiEnabled()) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                        if (wifiInfo != null) {
                            int dbm = wifiInfo.getRssi();
                            Log.d("dbm", "" + dbm);

                            txt_tool_strength_name.setText("" + dbm);

                            txt_tool_network_name.setText(wifiInfo.getSSID());
                            txt_tool_bssid_name.setText(wifiInfo.getBSSID());
                            String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                            txt_tool_ip_name.setText(ip);

                            if (dbm >= -55) {
                                txt_tool_strength_name.setTextColor(getResources().getColor(R.color.wifigreen));
                                txt_tool_strength_name.setText(dbm +" dbm");
                                // img_singnal.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_48dp);
                                //  img_singnal.setColorFilter(ContextCompat.getColor(ToolsActivity.this, R.color.wifigreen), android.graphics.PorterDuff.Mode.SRC_IN);
                            } else if (dbm < -55 && dbm >= -60) {
                                txt_tool_strength_name.setTextColor(getResources().getColor(R.color.wifigreen));
                                txt_tool_strength_name.setText(dbm +" dbm");
                                //  img_singnal.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_48dp);
                                // img_singnal.setColorFilter(ContextCompat.getColor(ToolsActivity.this, R.color.wifigreen), android.graphics.PorterDuff.Mode.SRC_IN);
                            } else if (dbm < -60 && dbm >= -70) {
                                txt_tool_strength_name.setTextColor(getResources().getColor(R.color.wifiyellow));
                                txt_tool_strength_name.setText(dbm +" dbm");
                                //  img_singnal.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_48dp);
                                //  img_singnal.setColorFilter(ContextCompat.getColor(ToolsActivity.this, R.color.wifiyellow), android.graphics.PorterDuff.Mode.SRC_IN);
                            } else if (dbm < -70 && dbm >= -80) {
                                txt_tool_strength_name.setTextColor(getResources().getColor(R.color.wifiyellow));
                                txt_tool_strength_name.setText(dbm +" dbm");
                                // img_singnal.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_48dp);
                                // img_singnal.setColorFilter(ContextCompat.getColor(ToolsActivity.this, R.color.wifiyellow), android.graphics.PorterDuff.Mode.SRC_IN);
                            } else {
                                txt_tool_strength_name.setTextColor(getResources().getColor(R.color.wifired));
                                txt_tool_strength_name.setText("" + dbm);
                                //  img_singnal.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_48dp);
                                // img_singnal.setColorFilter(ContextCompat.getColor(ToolsActivity.this, R.color.wifired), android.graphics.PorterDuff.Mode.SRC_IN);
                            }
                        }
                    }
                    handler.postDelayed(this, 2000);
                }
            }
        };

// start it with:
        handler.post(runnable);
    }




    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(Wifichangereceiver);
        mStopHandler=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStopHandler=false;


        registerReceiver(Wifichangereceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        if(isLocationEnabled(Network_Detail_Activity.this))
        {
            if (wifiManager.isWifiEnabled()) {
                ll_devices.setVisibility(View.VISIBLE);
                ll_devices_title.setVisibility(View.VISIBLE);
                //  ll_netinfo.setVisibility(View.VISIBLE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    txt_tool_network_name.setText(wifiInfo.getSSID());
                    txt_tool_bssid_name.setText(wifiInfo.getBSSID());
                    String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                    txt_tool_ip_name.setText(ip);
                }
            } else {
                Log.i("Wi-Fi network state", "off");
                ll_devices.setVisibility(View.GONE);
                ll_devices_title.setVisibility(View.GONE);
                txt_error_notes.setVisibility(View.VISIBLE);
                //   ll_netinfo.setVisibility(View.INVISIBLE);
            }
        }
        else {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);
            builder.setTitle("Enable Loaction Service");
            builder.setMessage("Enable Location To Find Available Wi-Fi");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                }
            });
            builder.setNegativeButton("CLOSE",(dialog, which) -> {
                ll_devices.setVisibility(View.GONE);
                ll_devices_title.setVisibility(View.GONE);
                txt_error_notes.setVisibility(View.VISIBLE);
                //  ll_netinfo.setVisibility(View.INVISIBLE);
            });
            builder.setCancelable(false);
            builder.show();

        }


    }


















    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    if (wifiManager.isWifiEnabled()) {
                        ll_devices.setVisibility(View.VISIBLE);
                        ll_devices_title.setVisibility(View.VISIBLE);
                        //   ll_netinfo.setVisibility(View.VISIBLE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            txt_tool_network_name.setText(wifiInfo.getSSID());
                            txt_tool_bssid_name.setText(wifiInfo.getBSSID());
                            String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                            txt_tool_ip_name.setText(ip);
                        }
                    } else {
                        Log.i("Wi-Fi network state", "off");
                        ll_devices.setVisibility(View.GONE);
                        ll_devices_title.setVisibility(View.GONE);
                        txt_error_notes.setVisibility(View.VISIBLE);
                        //  ll_netinfo.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }



    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.i("Wi-Fi network state", info.getDetailedState().toString());
        }
    };




    public BroadcastReceiver Wifichangereceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            Log.d("extraWifiState",""+extraWifiState);

            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    // txt_message.setVisibility(View.VISIBLE);
                    // ll_netinfo.setVisibility(View.GONE);
                    txt_error_notes.setVisibility(View.VISIBLE);
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    //txt_message.setVisibility(View.VISIBLE);
                    // ll_netinfo.setVisibility(View.GONE);

                    txt_error_notes.setVisibility(View.VISIBLE);
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //Toast.makeText(context,"Wifi enabled",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            Log.d("wifiInfo", wifiInfo.toString());
                            Log.d("SSID", wifiInfo.getSSID());

                            if (wifiInfo.getSSID() != null) {
                                String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                                txt_tool_ip_name.setText(ip);
                                txt_tool_network_name.setText(wifiInfo.getSSID());
                                txt_tool_bssid_name.setText(wifiInfo.getBSSID());
                                txt_error_notes.setVisibility(View.GONE);
                                //   ll_netinfo.setVisibility(View.VISIBLE);
                                if(!isLocationEnabled(Network_Detail_Activity.this))
                                {
                                    txt_error_notes.setVisibility(View.VISIBLE);
                                    // ll_netinfo.setVisibility(View.GONE);
                                }

                            }
                        }
                    }, 1200);

                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    //Toast.makeText(context,"Wifi enabled", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            Log.d("wifiInfo", wifiInfo.toString());
                            Log.d("SSID", wifiInfo.getSSID());

                            if (wifiInfo.getSSID() != null) {
                                String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                                txt_tool_ip_name.setText(ip);
                                txt_tool_network_name.setText(wifiInfo.getSSID());
                                txt_tool_bssid_name.setText(wifiInfo.getBSSID());
                                //  txt_message.setVisibility(View.GONE);
                                //  ll_netinfo.setVisibility(View.VISIBLE);
                                if(!isLocationEnabled(Network_Detail_Activity.this))
                                {
                                    txt_error_notes.setVisibility(View.VISIBLE);
                                    //  ll_netinfo.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, 1200);

                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    //  WifiState.setText("WIFI STATE UNKNOWN");
                    break;
            }
        }
    };
    String frequencyToChannel(String frequency) {
        switch(frequency) {
            case "2412" :
                return "2.4GHz";
            case "2417" :
                return "2.4GHz";
            case "2422" :
                return "2.4GHz";
            case "2427" :
                return "2.4GHz";
            case "2432" :
                return "2.4GHz";
            case "2437" :
                return "2.4GHz";
            case "2442" :
                return "2.4GHz";
            case "2447" :
                return "2.4GHz";
            case "2452" :
                return "2.4GHz";
            case "2457" :
                return "2.4GHz";
            case "2462" :
                return "2.4GHz";
            case "2467" :
                return "2.4GHz";
            case "2472" :
                return "2.4GHz";
            case "2484" :
                return "2.4GHz";
            case "5035" :
                return "5GHz";
            case "5040" :
                return "5GHz";
            case "5045" :
                return "5GHz";
            case "5055" :
                return "5GHz";
            case "5060" :
                return "5GHz";
            case "5080" :
                return "5GHz";
            case "5170" :
                return "5GHz";
            case "5180" :
                return "5GHz";
            case "5190" :
                return "5GHz";
            case "5200" :
                return "5GHz";
            case "5210" :
                return "5GHz";
            case "5220" :
                return "5GHz";
            case "5230" :
                return "5GHz";
            case "5240" :
                return "5GHz";
            case "5260" :
                return "5GHz";
            case "5280" :
                return "5GHz";
            case "5300" :
                return "5GHz";
            case "5320" :
                return "5GHz";
            case "5500" :
                return "5GHz";
            case "5520" :
                return "5GHz";
            case "5540" :
                return "5GHz";
            case "5560" :
                return "5GHz";
            case "5580" :
                return "5GHz";
            case "5600" :
                return "5GHz";
            case "5620" :
                return "5GHz";
            case "5640" :
                return "5GHz";
            case "5660" :
                return "5GHz";
            case "5680" :
                return "5GHz";
            case "5700" :
                return "5GHz";
            case "5720" :
                return "5GHz";
            case "5745" :
                return "5GHz";
            case "5765" :
                return "5GHz";
            case "5785" :
                return "5GHz";
            case "5805" :
                return "5GHz";
            case "5825" :
                return "5GHz";
            case "4915" :
                return "5GHz";
            case "4920" :
                return "5GHz";
            case "4925" :
                return "5GHz";
            case "4935" :
                return "5GHz";
            case "4940" :
                return "5GHz";
            case "4945" :
                return "5GHz";
            case "4960" :
                return "5GHz";
            case "4980" :
                return "5GHz";
            default:
                return "No channel";
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    /* @Override
    public void onBackPressed() {
        //mStopHandler=true;
        if (backPressedToExitOnce) {
            //super.onBackPressed();
            moveTaskToBack(false);
            mStopHandler=true;
              *//*  this.finish();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);*//*

            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else
        {
            this.backPressedToExitOnce = true;
            Toast.makeText(Network_Detail_Activity.this, "Press again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;


                }
            }, 3000);
        }
        //super.onBackPressed();

    }*/
}