package com.qsmp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class SplashActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    SharedPreferences sp_userdetail;
    private static AsyncHttpClient client ;
    connectionDector dector;
    String Str_Userid;
    String Str_UserName;
    String Str_Password;
    String Str_Rudder_Id;
    String Str_cloud_id;
    String saml_token;
    String Str_two_fa_token;
    String Str_twofa_status;
    String Str_debugmode;

    String Str_StunQn_Ip;
    String Str_StunQn_Port;


    double version, play_version;
    String MainStr_Version;
    public AlertDialog d;
    private boolean mNeedsFlexibleUpdate;
    public static final int REQUEST_CODE = 1234;
    private static final int LOCK_REQUEST_CODE = 221;
    MyApplicationApollo mApplication;
    String Str_Stun_Server_list_data="";
    String Str_Device_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        client= new AsyncHttpClient();
        dector=new connectionDector(SplashActivity.this);
        sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);




    }

    @Override
    protected void onResume() {
        super.onResume();

            if(dector.checkNetwork())
            {

                getlogindata();

            }
            else
            {
                //Toast.makeText(SplashActivityQN.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashActivity.this, ErrorPage_Qn_Activtiy.class);
                startActivity(i);
            }


    }


    public void getlogindata()
    {
        Log.d("call","logindata_urlmain");
        if(sp_userdetail.contains("user_name") && sp_userdetail.contains("password"))
        {


            if(sp_userdetail.contains("Str_Base_url_qn_stage_beta"))
            {
                ConfigQn.Main_URL=sp_userdetail.getString("Str_Base_url_qn_stage_beta",null);
                ConfigQn.Main_URL_Beacon=sp_userdetail.getString("Str_stage_beta_Base_URL_Beacon",null);
            }

            Log.d("call","sharepreferences_data");

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    try {
                        Str_UserName=sp_userdetail.getString("user_name",null);
                        Str_Password=sp_userdetail.getString("password",null);
                        Str_Rudder_Id=sp_userdetail.getString("rudder_id",null);

                        JSONObject Rider_reg_info_Jobj=new JSONObject();

                        try {
                            Str_Device_uuid = Settings.Secure.getString(SplashActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            Log.d("device_uuid",Str_Device_uuid);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }



                               /* Rider_reg_info_Jobj.put("username",edt_username.getText().toString());
                                Rider_reg_info_Jobj.put("password",edt_password.getText().toString());
                                Rider_reg_info_Jobj.put("rudder_id",Str_Rudder_Id);*/
                        Rider_reg_info_Jobj.put("username",Str_UserName);
                        Rider_reg_info_Jobj.put("password",Str_Password);
                        Rider_reg_info_Jobj.put("rudder_id",Str_Rudder_Id);
                        Rider_reg_info_Jobj.put("peer_identity",Str_Device_uuid);
                        Rider_reg_info_Jobj.put("is_qim","1");


                        Log.d("Qnlogin Jason",Rider_reg_info_Jobj.toString());



                        Set_Rider_Register_data(Rider_reg_info_Jobj);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }, 500);

        }
        else
        {
            Log.d("call","Login");
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(SplashActivity.this, Rudder_IdName_Activity.class);
                    startActivity(i);
                }
            }, 1000);



        }



    }



    private void Set_Rider_Register_data(JSONObject jsonObject) throws UnsupportedEncodingException
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
        final String url= ConfigQn.Main_URL+"verify_qam_user";
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
        client.post(SplashActivity.this,url,entity,"application/json",new JsonHttpResponseHandler()
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

                            //Toast.makeText(SplashActivityQN.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                            JSONArray datarray=response.getJSONArray("data");

                            saml_token=response.getString("saml_token");

                            if(datarray.length()>0)
                            {
                                JSONObject jsonObject=datarray.getJSONObject(0);

                                Str_Userid=jsonObject.getString("user_id");
                                Str_cloud_id=jsonObject.getString("cloud_id");




                                if(Str_Userid.length()>0)
                                {



                                    sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);

                                    SharedPreferences.Editor editor_data= sp_userdetail.edit();
                                    editor_data.putString("user_id",Str_Userid);
                                    editor_data.putString("user_name",Str_UserName);
                                    editor_data.putString("password",Str_Password);
                                    editor_data.putString("rudder_id",Str_Rudder_Id);
                                    editor_data.putString("cloud_id",Str_cloud_id);
                                    editor_data.putString("saml_token",saml_token);

                                 /*   editor_data.putString("privateKey",privateKey);
                                    editor_data.putString("publickey",publickey);*/
                                    editor_data.commit();


                                    //startActivity(new Intent(SplashActivityQN.this,MainDashboard_Activity_QnUnGrid.class));
                                    startActivity(new Intent(SplashActivity.this, Qsmp_Dashboard_Activity.class));

                                }



                            }
                        }
                        else
                        {
                            Toast.makeText(SplashActivity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                            SharedPreferences sp_detail=getSharedPreferences("userdetail.txt", MODE_PRIVATE);
                            SharedPreferences.Editor editor=sp_detail.edit();
                            editor.clear();
                            editor.commit();


                            finish();
                            Intent intent=new Intent(SplashActivity.this, Rudder_IdName_Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Toast.makeText(SplashActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
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
                Toast.makeText(SplashActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(SplashActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(SplashActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }


    private boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
      /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
       {
           int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
           int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
          // return Environment.isExternalStorageManager() && result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
       } else {
           int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
           int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
           int result3= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
           int result4 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
           return result1 == PackageManager.PERMISSION_GRANTED && result2== PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
       }*/
    }

    private void requestPermission() {
        // int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // int READPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int locationPermission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int locationPermission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
      /*  if (READPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if (locationPermission1 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (locationPermission2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //ActivityCompat.requestPermissions(Splash_Screen.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);

    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public boolean CheckingPermissionIsEnabledOrNot() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void RequestMultiplePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,}, 7);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            if (grantResults.length > 0) {
                boolean BluetoothConnectPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean BluetoothScanPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (BluetoothConnectPermission && BluetoothScanPermission) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    // Constants.showToast(this, "Permission Denied");
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



}