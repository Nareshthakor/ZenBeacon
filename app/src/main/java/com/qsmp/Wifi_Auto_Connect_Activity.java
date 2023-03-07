package com.qsmp;

import static android.provider.Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED;
import static android.provider.Settings.ADD_WIFI_RESULT_ALREADY_EXISTS;
import static android.provider.Settings.ADD_WIFI_RESULT_SUCCESS;
import static android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Wifi_Auto_Connect_Activity extends AppCompatActivity {

    Bitmap bitmap;
    ProgressDialog progressDialog;
    Handler handler_multi_stub;
    WifiManager wifiManager;
    Button btn_connect,btn_save_confrigration,btn_done;
    TextView txt_ssid_text;
    EditText edt_ssid_name,edt_password,edt_crm_number;
    TextView txt_labal_name;
    public Runnable runnable;
    public Handler handler = new Handler();
    public Handler handler_ssid = new Handler();
    public boolean mStopHandler = false;
    String SSIDName="";
    String CRMNumber="";
    String SSID_Password="";
    private final String USER_AGENT = "Mozilla/5.0";
    ImageView img_wifigif,img_wifi,img_about_actionbar_back,img_about_actionbar_setting;
    private static AsyncHttpClient client ;
    String formattedMAC="";
    String formattedIP="";
    SharedPreferences sp_userdetail;
    boolean flag_Intent_N_Detail=false;
    TextView txt_conn_avai_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_auto_connect);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handler_multi_stub=new Handler();
        client= new AsyncHttpClient();

        sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {// if build version is less than Q try the old traditional method
            wifiManager.setWifiEnabled(true);
        }

        btn_connect=findViewById(R.id.btn_connect);
        txt_conn_avai_name=findViewById(R.id.txt_conn_avai_name);


        txt_labal_name=findViewById(R.id.txt_labal_name);
        img_wifigif=findViewById(R.id.img_wifigif);
        img_wifi=findViewById(R.id.img_wifi);
        img_about_actionbar_back=findViewById(R.id.img_about_actionbar_back);
        img_about_actionbar_setting=findViewById(R.id.img_about_actionbar_setting);

        img_wifi.setVisibility(View.VISIBLE);
        img_wifigif.setVisibility(View.GONE);
        txt_labal_name.setVisibility(View.GONE);
        img_wifi.setColorFilter(getResources().getColor(R.color.grey_400));

        if(sp_userdetail.contains("SSID_Name"))
        {
            SSIDName = sp_userdetail.getString("SSID_Name", null);
            txt_conn_avai_name.setText(SSIDName+" connection is available");
        }
        else
        {
            txt_conn_avai_name.setText("Taj VIP connection is available");
        }


        img_about_actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_about_actionbar_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetMenu();
            }
        });


        btn_connect.setOnClickListener(v->
        {
            if(sp_userdetail.contains("SSID_Name"))
            {
                Log.d("SSID_Name","Contain");
                SSIDName = sp_userdetail.getString("SSID_Name", null);
                SSID_Password = sp_userdetail.getString("SSID_password", null);
                CRMNumber = sp_userdetail.getString("CRM_Number", null);


                if(getCurrentSsid(Wifi_Auto_Connect_Activity.this)!=null)
                {
                    if(unornamatedSsid(getCurrentSsid(Wifi_Auto_Connect_Activity.this)).equals(SSIDName)){
                        //ProgressBarplash.setVisibility(View.INVISIBLE);
                        txt_labal_name.setVisibility(View.VISIBLE);
                        txt_labal_name.setText("You are already connected "+SSIDName);

                        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        formattedIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                        //Log.d("formattedIP",""+formattedIP);
                        call_html_Data();
                    }
                    else
                    {
                        img_wifi.setVisibility(View.GONE);
                        img_wifigif.setVisibility(View.VISIBLE);

                        Glide.with(Wifi_Auto_Connect_Activity.this)
                                .load(R.drawable.wifi_signal)
                                //.asGif()
                                //.placeholder(R.drawable.img)
                                //.crossFade()
                                .into(img_wifigif);


                        txt_labal_name.setVisibility(View.VISIBLE);
                        txt_labal_name.setText("Attempting to connect to the network "+SSIDName+" . . .");


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            connectUsingNetworkSuggestion(SSIDName,SSID_Password);
                        }
                        else
                        {
                            ConnectSSID(SSIDName,SSID_Password);
                        }

                        Ssid_not_Connect();


                    }
                }
                else
                {

                    img_wifi.setVisibility(View.GONE);
                    img_wifigif.setVisibility(View.VISIBLE);

                    Glide.with(Wifi_Auto_Connect_Activity.this)
                            .load(R.drawable.wifi_signal)
                            //.asGif()
                            //.placeholder(R.drawable.img)
                            //.crossFade()
                            .into(img_wifigif);

                    Log.d("Else","ssid null");
                    txt_labal_name.setVisibility(View.VISIBLE);
                    txt_labal_name.setText("Attempting to connect to the network "+SSIDName+" . . .");


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    {
                        connectUsingNetworkSuggestion(SSIDName,SSID_Password);
                    }
                    else
                    {
                        ConnectSSID(SSIDName,SSID_Password);
                    }
                }


            }
            else
            {
                Log.d("SSID_Name","Not Contain");
                Toast.makeText(Wifi_Auto_Connect_Activity.this,"Please Set SSID Configuration Data" ,Toast.LENGTH_SHORT).show();
                BottomSheetMenu();
            }







        });



    }


    private void BottomSheetMenu()
    {
        // TextView user_title,txt_user_manager_certificate,txt_user_delete,txt_user_edit;
        // CircleImageView txt_user_edit;
        View view1 = getLayoutInflater().inflate(R.layout.bottom_layout_set_confrigration, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(Wifi_Auto_Connect_Activity.this, R.style.BottomSheetDialogTheme);

        dialog.setContentView(view1);
        dialog.setCancelable(true);
        dialog.show();

        edt_ssid_name=dialog.findViewById(R.id.edt_ssid_name);
        edt_password=dialog.findViewById(R.id.edt_password);
        edt_crm_number=dialog.findViewById(R.id.edt_crm_number);
        btn_save_confrigration=dialog.findViewById(R.id.btn_save_confrigration);


        // View divider_edit=view1.findViewById(R.id.divider_edit);


        if(sp_userdetail.contains("SSID_Name")) {
            Log.d("SSID_Name", "Contain");
            SSIDName = sp_userdetail.getString("SSID_Name", null);
            SSID_Password = sp_userdetail.getString("SSID_password", null);
            CRMNumber = sp_userdetail.getString("CRM_Number", null);


            edt_ssid_name.setText(SSIDName);
            edt_password.setText(SSID_Password);
            edt_crm_number.setText(CRMNumber);

        }


        // FavourableDeviceBean bean = favourableDeviceBeanList.get(position);

        btn_save_confrigration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SSIDName=edt_ssid_name.getText().toString().trim();
                CRMNumber=edt_crm_number.getText().toString().trim();

                SSID_Password=edt_password.getText().toString().trim();

                if(SSIDName.length()>0)
                {
                    if(CRMNumber.length()>0)
                    {
                        SharedPreferences.Editor editor_data= sp_userdetail.edit();

                        editor_data.putString("SSID_Name",SSIDName);
                        editor_data.putString("SSID_password",SSID_Password);
                        editor_data.putString("CRM_Number",CRMNumber);
                        editor_data.apply();

                        dialog.dismiss();
                        btn_connect.performClick();
                    }
                    else
                    {
                        Toast.makeText(Wifi_Auto_Connect_Activity.this,"please enter CRM Number" ,Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Wifi_Auto_Connect_Activity.this,"please enter ssid name" ,Toast.LENGTH_SHORT).show();
                }



            }
        });




        // BottomSheetMenuPolicy(position,favourableDeviceBeanList);


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectUsingNetworkSuggestion(String ssid, String password)
    {
        final WifiNetworkSuggestion suggestion1 = new WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                .build();


        final List<WifiNetworkSuggestion> suggestionsList = new ArrayList<WifiNetworkSuggestion>();
        suggestionsList.add(suggestion1);

        final WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int status = wifiManager.addNetworkSuggestions(suggestionsList);



        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // Error handling
            Log.d("Status_tag","success "+status);
            wifiManager.disconnect();
            wifiManager.reconnect();
            status=wifiManager.removeNetworkSuggestions(suggestionsList);
            status = wifiManager.addNetworkSuggestions(suggestionsList);

        }
        else if(status==WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE)
        {
            Log.i("TAG", "PSK network added: STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE "+status);
            //Toast.makeText(this, "Configuration existed (as-is) on device, nothing changed", Toast.LENGTH_LONG).show();
            status=wifiManager.removeNetworkSuggestions(suggestionsList);
            status = wifiManager.addNetworkSuggestions(suggestionsList);
        }
        else if(status==WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED)
        {
            Log.i("TAG", "PSK network added: STATUS_NETWORK_SUGGESTIONS_ERROR_APP_DISALLOWED "+status);
            //Toast.makeText(this, "Configuration existed (as-is) on device, nothing changed", Toast.LENGTH_LONG).show();
            status=wifiManager.removeNetworkSuggestions(suggestionsList);
            status = wifiManager.addNetworkSuggestions(suggestionsList);
        }
        else
        {
            Log.d("Status_tag","fail "+status);
        }

        final IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    Log.d("Status_tag","postconn ");
                    return;
                }
                // Post connection
                Log.d("Status_tag","postconn not ");
            }
        };
        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);


        Intent intent = new Intent(Settings.ACTION_WIFI_ADD_NETWORKS);
        intent.putParcelableArrayListExtra(Settings.EXTRA_WIFI_NETWORK_LIST, (ArrayList<? extends Parcelable>) suggestionsList);
        //startActivity(intent);
        startActivityForResult(intent, 555);


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void Connect_Above_10device(String AP_SSID, String AP_PASSWORD)
    {
        Log.d("ConnectSSID_above10","CAll above 10 connect ssid");
        Log.d("ConnectSSID_above10","SSIDName "+AP_SSID);
        Log.d("ConnectSSID_above10","password "+AP_PASSWORD);



        final WifiNetworkSuggestion wifiSuggestionBuilder =
                new WifiNetworkSuggestion.Builder()
                        .setSsid(AP_SSID)
                        .setWpa2Passphrase(AP_PASSWORD)
                        //.setIsAppInteractionRequired(true) // Optional (Needs location permission)
                        //.setWpa2EnterpriseConfig(wifiEnterpriseConfig)
                        .build();
        final List<WifiNetworkSuggestion> suggestionsList =
                new ArrayList<>(Collections.singleton(wifiSuggestionBuilder));

        Intent intent = new Intent(Settings.ACTION_WIFI_ADD_NETWORKS);
        intent.putParcelableArrayListExtra(Settings.EXTRA_WIFI_NETWORK_LIST, (ArrayList<? extends Parcelable>) suggestionsList);
        //startActivity(intent);
        startActivityForResult(intent, 555);

        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int status = wifiManager.addNetworkSuggestions(suggestionsList);


        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS)
        {
            //Toast.makeText(this,"PSK network added",Toast.LENGTH_LONG).show();
            Log.i("TAG", "PSK network added: "+status);

          /*  Intent intent=new Intent(Qam_Set_Certificate_Activity.this,Wifi_Connect_Device_Instruction_Activity.class);
            intent.putExtra("Identity",Main_Identity);
            intent.putExtra("SSIDName",SSIDName);
            startActivity(intent);*/
        }
        else if(status==WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE)
        {
            Log.i("TAG", "PSK network added: STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE "+status);
            //Toast.makeText(this, "Configuration existed (as-is) on device, nothing changed", Toast.LENGTH_LONG).show();
            status=wifiManager.removeNetworkSuggestions(suggestionsList);
            status = wifiManager.addNetworkSuggestions(suggestionsList);
        }
        else {
            Toast.makeText(this,"PSK network not added",Toast.LENGTH_LONG).show();
            Log.i("TAG", "PSK network not added: "+status);
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // User agreed to save configurations: still need to check individual results
            Log.d("requestCode",""+requestCode);
            Log.d("resultCode",""+resultCode);
            if (data != null && data.hasExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                for (int code : data.getIntegerArrayListExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                    Log.d("code",""+code);
                    switch (code) {
                        case ADD_WIFI_RESULT_SUCCESS:
                            Toast.makeText(this, "Configuration saved or modified", Toast.LENGTH_LONG).show();

                            ConnectionHandlerSSID();


                            break;
                        case ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED:
                            Toast.makeText(this, "Something went wrong - invalid configuration", Toast.LENGTH_LONG).show();
                            break;
                        case ADD_WIFI_RESULT_ALREADY_EXISTS:
                            Toast.makeText(this, "Configuration existed (as-is) on device, nothing changed", Toast.LENGTH_LONG).show();
                            ConnectionHandlerSSID();
                            break;
                        default:
                            Toast.makeText(this, "Other errors code: " + code, Toast.LENGTH_LONG)
                                    .show();
                            break;
                    }
                }
            }

            if (requestCode == 201)
            {
                //get_Cert_Url_Data();
            }

        } else {
            Toast.makeText(this, "User refused to save configurations", Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void call_html_Data()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    sendGet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }

    private void call_NetworkDetail_page(String crm_id,String device_id,String device_ip)
    {

        //https://rudder.dev.qntmnet.com/wsmp/api/smart_auth
        try {


            JSONObject Rider_reg_info_Jobj=new JSONObject();

            Rider_reg_info_Jobj.put("crm_id",crm_id);
            Rider_reg_info_Jobj.put("device_id",device_id);
            Rider_reg_info_Jobj.put("device_ip",device_ip);
            Rider_reg_info_Jobj.put("flag","create");


            Log.d("Set_smart_auth Jason",Rider_reg_info_Jobj.toString());

            Set_smart_auth(Rider_reg_info_Jobj);

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


    private void Set_smart_auth(JSONObject jsonObject) throws UnsupportedEncodingException
    {
       /* final Dialog dialog;


        dialog = new Dialog(SplashActivityQN.this);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        // Set dialog title
        // dialog.setTitle("Custom Dialog");
        dialog.show();*/

        RequestParams params = new RequestParams();
        params.put("",jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        //final String url= ConfigQn.Main_URL+"verify_qam_user";
        final String url= "https://rudder.dev.qntmnet.com/wsmp/api/smart_auth";
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
        client.post(Wifi_Auto_Connect_Activity.this,url,entity,"application/json",new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res",response.toString());

                try{
                    if(response.length()>0)
                    {

                        if(response.getString("responseCode").equalsIgnoreCase("200") || response.getString("responseCode").equalsIgnoreCase("209"))
                        {

                            //Toast.makeText(Wifi_Auto_Connect_Activity.this, response.getString("responseMsg"), Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        flag_Intent_N_Detail=true;
                                        //startActivity(new Intent(Wifi_Auto_Connect_Activity.this,Network_Detail_Activity.class));
                                        Intent intent = new Intent(Wifi_Auto_Connect_Activity.this, Network_Detail_Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 200);
                        }
                        else
                        {
                            Toast.makeText(Wifi_Auto_Connect_Activity.this, response.getString("responseMsg"), Toast.LENGTH_LONG).show();

                        }



                    }
                    else
                    {
                        Toast.makeText(Wifi_Auto_Connect_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
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
                try {
                    Log.d("throwable_JO",""+throwable.getMessage().toString());
                    Log.d("throwable_JO_err",""+errorResponse.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(Wifi_Auto_Connect_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Wifi_Auto_Connect_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
                try {
                    Log.d("throwable_TH",""+statusCode);
                    Log.d("throwable_TH",""+responseString);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Wifi_Auto_Connect_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }



    private void sendGet() throws Exception {

        //String url = "https://developer.yupwifi.com/raajbagh/loginuser.php?link-login=&link-orig=&error=&chap-id=&mac=&identity=&chap-challenge=&link-login-only=loginuser.php?link-login=http://192.168.88.1/login?dst=http%3A%2F%2Fwww.peplink.com%2F&link-orig=http://www.peplink.com/&error=&chap-id=&mac=74:D4:35:C7:6B:2E&identity=raajbagh&chap-challenge=&link-login-only=http://192.168.88.1/login&un=802309&up=802309";
        //String url="https://developer.yupwifi.com/AutoConnectApp/connect.php";
        String url="http://peplink.com";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent",USER_AGENT);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine+"\n");
        }
        //String str=new Jsoup().parse(response.toString()).text()
        //System.out.println("html to text : "+Html.fromHtml(response.toString()).toString());

        in.close();

        try {

            //print result
            System.out.println(response.toString());
            System.out.println();
            Document doc = Jsoup.parse(response.toString());
      /*  System.out.println(doc.select("meta[http-equiv=refresh]").attr("content"));
        System.out.println("URL===>"+doc.select("meta[content=0]").attr("URL"));*/
            //  Log.d("url",extractAllText(response.toString()));
            // Document doc = Jsoup.parse(html);
            Element eMETA = doc.select("meta").first();
            String content = eMETA.attr("content");
            String urlRedirect = content.split(";")[1];
            String urlres = urlRedirect.split("=")[1].replace("'","");

            Log.d("url==>",urlRedirect);


            Uri uri = Uri.parse(urlRedirect);
            Log.d("UI==>",uri.getQueryParameter("UI"));
            Log.d("UIP==>",uri.getQueryParameter("UIP"));
            formattedIP=uri.getQueryParameter("SIP");
            formattedMAC = uri.getQueryParameter("MA").replaceAll("(.{2})", "$1"+":").substring(0,17);
            Log.d("MA==>",formattedMAC);
            Log.d("SIP==>",uri.getQueryParameter("SIP"));
            Log.d("OS==>",uri.getQueryParameter("OS"));

        }catch (Exception e)
        {
            e.printStackTrace();

        }



        if(formattedMAC.trim().length()>0)
        {
            Log.d("formattedMAC",""+formattedMAC);
            call_NetworkDetail_page(CRMNumber,formattedMAC,formattedIP);
        }
        else
        {
            Log.d("formattedMAC","null");

            Log.d("formattedMAC",""+formattedMAC);
            call_NetworkDetail_page(CRMNumber,formattedMAC,formattedIP);
        }


/*

        //Element element=dc.select("input[name*=link-orig").

        org.jsoup.nodes.Element element2=dc.select("input[name*="+TOKEN2).first();
        strinput2=element2.attr("value");
        System.out.println("string input 2"+strinput2);

        org.jsoup.nodes.Element element1=dc.select("input[name*="+TOKEN1).first();
        strinput1=element1.attr("value");
        System.out.println("string input 1"+strinput1);

        org.jsoup.nodes.Element element3=dc.select("input[name*="+TOKEN3).first();
        strinput3=element3.attr("value");
        System.out.println("string input 3"+strinput3);

        org.jsoup.nodes.Element element4=dc.select("input[name*="+TOKEN4).first();
        strinput4=element4.attr("value");
        System.out.println("string input 4"+strinput4);

        org.jsoup.nodes.Element element5=dc.select("input[name*="+TOKEN5).first();
        strinput5=element5.attr("value");
        System.out.println("string input 5" + strinput5);


        org.jsoup.nodes.Element element6=dc.select("input[name*="+TOKEN6).first();
        strinput6=element6.attr("value");
        System.out.println("string input 6"+strinput6);
        preferences.setIdentity(strinput6);

        org.jsoup.nodes.Element element7=dc.select("input[name*="+TOKEN7).first();
        if(element7!=null)
            strinput7=element7.attr("value");
        System.out.println("string input 7"+strinput7);

        org.jsoup.nodes.Element element8=dc.select("input[name*="+TOKEN8).first();
        strinput8=element8.attr("value");
        System.out.println("string input 8"+strinput8);

        org.jsoup.nodes.Element element9=dc.select("input[name*="+TOKEN9).first();
        strinput9=element9.attr("value");
        System.out.println("string input 9"+strinput9);

        org.jsoup.nodes.Element element10=dc.select("input[name*="+TOKEN10).first();
        strinput10=element10.attr("value");
        System.out.println("string input 10"+strinput10);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectionManager(strinput10,username,password);
            }
        }, 1000);*/
    }


    private void ConnectSSID(String SSIDName,String password)
    {
        Log.d("ConnectSSID_below10","CAll below 10 connect ssid");
        Log.d("ConnectSSID_below10","SSIDName "+SSIDName);
        Log.d("ConnectSSID_below10","password "+password);



        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\""+SSIDName+"\"";
        wc.preSharedKey = "\""+password+"\"";
       /* wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        wc.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);
        wc.status = WifiConfiguration.Status.ENABLED;*/
        try{

            Log.d("ConnectSSID_below10","SSIDName last"+SSIDName);

            WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);

            int netId = wifiManager.addNetwork(wc);

            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();


        }catch (Exception e){
            e.printStackTrace();
            Log.d("ConnectSSID_below10","Exception"+e.getMessage());
        }
        ConnectionHandlerSSID();
    }


    public void Ssid_not_Connect()
    {
        handler_ssid.postDelayed(new Runnable() {
                @Override
                public void run() {
                    BottomSheetMenu_SSID_Not_Connect();
                }
            }, 15000);

    }

    private void BottomSheetMenu_SSID_Not_Connect()
    {
        // TextView user_title,txt_user_manager_certificate,txt_user_delete,txt_user_edit;
        // CircleImageView txt_user_edit;
        View view1 = getLayoutInflater().inflate(R.layout.bottom_layout_wifi_instruction, null);

        final BottomSheetDialog dialog_ssid = new BottomSheetDialog(Wifi_Auto_Connect_Activity.this, R.style.BottomSheetDialogTheme);

        dialog_ssid.setContentView(view1);
        dialog_ssid.setCancelable(true);
        dialog_ssid.show();

        btn_done=dialog_ssid.findViewById(R.id.btn_done);
        txt_ssid_text=dialog_ssid.findViewById(R.id.txt_ssid_text);

        txt_ssid_text.setText("Please Select \""+SSIDName+"\"  network manually from the wifi list");



        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog_ssid.dismiss();

            }
        });


        // BottomSheetMenuPolicy(position,favourableDeviceBeanList);


    }


    private void ConnectionHandlerSSID(){

        runnable = new Runnable() {
            @Override
            public void run() {
                // do your stuff - don't create a new runnable here!
                System.out.println("ssid "+getCurrentSsid(Wifi_Auto_Connect_Activity.this));
                //Log.d("ssid",getCurrentSsid(ConfigurationActivity.this));
                if (!mStopHandler) {
                    if(getCurrentSsid(Wifi_Auto_Connect_Activity.this)!=null){
                        if(unornamatedSsid(getCurrentSsid(Wifi_Auto_Connect_Activity.this)).equals(SSIDName)){
                            txt_labal_name.setText("Now you are connected "+SSIDName);
                            mStopHandler=true;

                            img_wifi.setVisibility(View.VISIBLE);
                            img_wifigif.setVisibility(View.GONE);
                            img_wifi.setColorFilter(getResources().getColor(R.color.primary));



                            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            formattedIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                            //Log.d("formattedIP",""+formattedIP);
                            call_html_Data();
                            /*Intent intent=new Intent(Qam_Set_Certificate_Activity.this,Qam_Dashboard_Activity.class);
                            startActivity(intent);*/
                        }
                    }

                    handler.postDelayed(this, 2000);
                }
            }
        };

// start it with:
        handler.post(runnable);
    }

    private boolean check_ssid_exits(String str_ssid)
    {
        Log.d("check_ssid_exits",""+str_ssid);
        boolean ssid_flag=false;
        List<ScanResult> wifiScanList = wifiManager.getScanResults();
        //wifis = new String[wifiScanList.size()];

        for(int i = 0; i < wifiScanList.size(); i++){
            //wifis[i] = ((wifiScanList.get(i)).SSID);

            if(wifiScanList.get(i).SSID.equals(str_ssid)) {

               /* WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", wifis[i]);
                wifiConfig.preSharedKey = String.format("\"%s\"", "password");

                WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
                //remember id
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();*/

                ssid_flag=true;
            }
        }
        return ssid_flag;
    }
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID().toString();
            }
        }
        return ssid;
    }

    private String unornamatedSsid(String ssid)
    {
        ssid = ssid.replaceFirst("^\"", "");
        return ssid.replaceFirst("\"$", "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        handler_multi_stub.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(isLocationEnabled(Wifi_Auto_Connect_Activity.this)){
                    //perfomlogin();
                    if(!wifiManager.isWifiEnabled())
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        {
                            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                            startActivityForResult(panelIntent, 201);
                        }
                    }else{
                        //get_Cert_Url_Data();
                    }

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Wifi_Auto_Connect_Activity.this);
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
        }, 2000);


        if(!flag_Intent_N_Detail)
        {
            ConnectionHandlerSSID();
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
    protected void onStop() {
        super.onStop();
        mStopHandler=false;
        handler.removeCallbacksAndMessages(null);
        handler_ssid.removeCallbacksAndMessages(null);
        handler_multi_stub.removeCallbacksAndMessages(null);

    }

}