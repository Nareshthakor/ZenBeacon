package com.qsmp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Rudder_IdName_Activity extends AppCompatActivity {


    EditText edt_rudderid;
    Button btn_rudder_id;
    connectionDector dector;

    private boolean backPressedToExitOnce = false;
    String Str_stage_beta_type = "Stage";
    String Str_stage_beta_Base_URL = "";
    String Str_stage_beta_Base_URL_Beacon = "";
    String Str_stage_beta_key_data = "";
    ArrayList<String> Base_Url_stage_beta_title_list = new ArrayList<>();
    SharedPreferences sp_userdetail;
    String Str_RudderID = "";
    String Str_Url_flag = "";

    private static final int MY_FINE_LOCATION_REQUEST = 99;
    private static final int MY_BACKGROUND_LOCATION_REQUEST = 100;
    private static AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rudder_id_name);


        edt_rudderid = findViewById(R.id.edt_rudderid);
        btn_rudder_id = findViewById(R.id.btn_rudder_id);


        sp_userdetail = getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);
        client = new AsyncHttpClient();
        dector = new connectionDector(Rudder_IdName_Activity.this);




        btn_rudder_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dector.checkNetwork()) {
                    Log.d("Str_stage_beta_type", Str_stage_beta_type);
                    if (edt_rudderid.getText().toString().trim().length() > 0) {
                        String edt_rudder_id_value = edt_rudderid.getText().toString().trim();


                        String[] RudderID_Array = edt_rudder_id_value.split("://");


                        if (RudderID_Array.length > 0) {
                            if (RudderID_Array.length > 1) {
                                Log.d("RudderID_Array_0", RudderID_Array[0]);
                                Log.d("RudderID_Array_1", RudderID_Array[1]);

                                Str_RudderID = RudderID_Array[1];
                                Str_Url_flag = RudderID_Array[0];

                                Str_Url_flag = Str_Url_flag.toLowerCase(Locale.ROOT);
                                Log.d("Str_Url_flag", "lowercase " + Str_Url_flag);
                                if (Str_Url_flag.equalsIgnoreCase("rudders") || Str_Url_flag.equalsIgnoreCase("Rudders") || Str_Url_flag.equalsIgnoreCase("rudderb") || Str_Url_flag.equalsIgnoreCase("Rudderb") || Str_Url_flag.equalsIgnoreCase("dev") || Str_Url_flag.equalsIgnoreCase("Dev") || Str_Url_flag.equalsIgnoreCase("rudder") || Str_Url_flag.equalsIgnoreCase("Rudder")) {
                                    Log.d("valid Prefix", Str_Url_flag);

                                    if (Str_Url_flag.contains("rudders") || Str_Url_flag.contains("Rudders")) {
                                        Str_stage_beta_key_data = "rudders";
                                        check_rudderid_url(Str_RudderID, "rudders");
                                    } else if (Str_Url_flag.contains("rudderb") || Str_Url_flag.contains("Rudderb")) {
                                        Str_stage_beta_key_data = "rudderb";
                                        check_rudderid_url(Str_RudderID, "rudderb");
                                    } else if (Str_Url_flag.contains("dev") || Str_Url_flag.contains("Dev")) {
                                        Str_stage_beta_key_data = "dev";
                                        check_rudderid_url(Str_RudderID, "dev");
                                    } else if (Str_Url_flag.contains("rudder") || Str_Url_flag.contains("Rudder")) {
                                        Str_stage_beta_key_data = "rudder";
                                        check_rudderid_url(Str_RudderID, "rudder");
                                    } else {
                                        Str_stage_beta_key_data = "rudder";
                                        check_rudderid_url(Str_RudderID, "rudder");
                                    }

                                } else {
                                    Toast.makeText(Rudder_IdName_Activity.this, "Please valid Prefix...", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Str_RudderID = RudderID_Array[0];
                                check_rudderid_url(Str_RudderID, "rudder");

                            }
                        } else {
                            Toast.makeText(Rudder_IdName_Activity.this, "Please Enter RudderId", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(Rudder_IdName_Activity.this, "Please Enter Rudder ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Rudder_IdName_Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        LocationPermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (CheckingPermissionIsEnabledOrNot())
            {
                // carry on the normal flow, as the case of  permissions  granted.
            } else {
                RequestMultiplePermission();
            }
        }
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
    public void check_rudderid_url(String Rudder_id, String url_str_data) {
        //{"rudder_id":"zenexim1"}
        try {


            if (url_str_data.equals("rudders")) {
                Str_stage_beta_Base_URL = "https://rudders.qntmnet.com/api/v1/";
                ConfigQn.Main_URL = "https://rudders.qntmnet.com/api/v1/";
                Str_stage_beta_Base_URL_Beacon = "https://rudders.qntmnet.com/wsmp/beacon-api/";
                ConfigQn.Main_URL_Beacon = "https://rudders.qntmnet.com/wsmp/beacon-api/";

            } else if (url_str_data.equals("rudderb")) {
                Str_stage_beta_Base_URL = "https://rudderb.qntmnet.com/api/v1/";
                ConfigQn.Main_URL = "https://rudderb.qntmnet.com/api/v1/";
                Str_stage_beta_Base_URL_Beacon = "https://rudderb.qntmnet.com/wsmp/beacon-api/";
                ConfigQn.Main_URL_Beacon = "https://rudderb.qntmnet.com/wsmp/beacon-api/";

            } else if (url_str_data.equals("dev")) {
                Str_stage_beta_Base_URL = "https://rudder.dev.qntmnet.com/api/v1/";
                ConfigQn.Main_URL = "https://rudder.dev.qntmnet.com/api/v1/";
                Str_stage_beta_Base_URL_Beacon = "https://rudder.dev.qntmnet.com/wsmp/beacon-api/";
                ConfigQn.Main_URL_Beacon = "https://rudder.dev.qntmnet.com/wsmp/beacon-api/";

            } else if (url_str_data.equals("rudder")) {
                Str_stage_beta_Base_URL = "https://rudder.qntmnet.com/api/v1/";
                ConfigQn.Main_URL = "https://rudder.qntmnet.com/api/v1/";
                Str_stage_beta_Base_URL_Beacon = "https://rudder.qntmnet.com/wsmp/beacon-api/";
                ConfigQn.Main_URL_Beacon = "https://rudder.qntmnet.com/wsmp/beacon-api/";

            } else {

            }

            JSONObject Rider_reg_info_Jobj = new JSONObject();

            Rider_reg_info_Jobj.put("rudder_id", Rudder_id);

            Log.d("Qn_rudderid_Jason", Rider_reg_info_Jobj.toString());

            Set_check_rudderid(Rider_reg_info_Jobj);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void Set_check_rudderid(JSONObject jsonObject) throws UnsupportedEncodingException {
        final Dialog dialog;


        dialog = new Dialog(Rudder_IdName_Activity.this);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        // Set dialog title
        // dialog.setTitle("Custom Dialog");
        dialog.show();

        RequestParams params = new RequestParams();
        params.put("", jsonObject);
        // StringEntity entity = new StringEntity(params.toString());
        final ByteArrayEntity entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        //  params.put("Password",jsonpassword);
        final String url = ConfigQn.Main_URL + "verify_qam_user";
        Log.d("url", url);
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MyCustomSSLFactory socketFactory = new MyCustomSSLFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client.setSSLSocketFactory(socketFactory);

        } catch (Exception e) {
            e.printStackTrace();
        }
        client.setTimeout(20 * 1000);
        client.post(Rudder_IdName_Activity.this, url, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res", response.toString());

                try {

                    if (response.length() > 0) {

                        dialog.dismiss();
                        dialog.cancel();

                        if (response.getInt("responseCode") == 201) {

                            ConfigQn.Main_URL = Str_stage_beta_Base_URL;

                            SharedPreferences.Editor editor_data = sp_userdetail.edit();
                            editor_data.putString("Str_Base_url_qn_stage_beta", Str_stage_beta_Base_URL);
                            editor_data.putString("Str_stage_beta_Base_URL_Beacon", Str_stage_beta_Base_URL_Beacon);
                            editor_data.commit();




                            Intent intent = new Intent(Rudder_IdName_Activity.this, LoginActivity.class);
                            //intent.putExtra("Rudder_id",edt_rudderid.getText().toString());
                            intent.putExtra("Rudder_id", Str_RudderID);
                            startActivity(intent);


                            //Toast.makeText(Services_WebView_Activity.this, "Successfully Disable Two Factor Authentication", Toast.LENGTH_LONG).show();


                        } else {

                            Str_stage_beta_Base_URL = "";
                            Str_stage_beta_key_data = "";
                            Str_RudderID = "";
                            Str_Url_flag = "";
                            edt_rudderid.setText("");
                            //Toast.makeText(Rudder_IdName_Activity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();
                            Toast.makeText(Rudder_IdName_Activity.this, "Invalid RudderId", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        dialog.dismiss();
                        dialog.cancel();
                        Toast.makeText(Rudder_IdName_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(Rudder_IdName_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(Rudder_IdName_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(Rudder_IdName_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            //super.onBackPressed();
            moveTaskToBack(false);
              /*  this.finish();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);*/

            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else {
            this.backPressedToExitOnce = true;
            Toast.makeText(Rudder_IdName_Activity.this, "Press again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;


                }
            }, 3000);
        }
        //super.onBackPressed();

    }


    private void LocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(Rudder_IdName_Activity.this);
                    builder.setTitle("Location Permission");
                    builder.setMessage("Please select \"Allow all the time\" option in location permission menu ");


                    builder.setNegativeButton("Grant Permission", (dialog, which) -> {
                        requestBackgroundLocationPermission();

                    });
                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();

                }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    // starServiceFunc()
                }
            }else{
                // starServiceFunc()
            }

        }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Rudder_IdName_Activity.this);
                builder.setTitle("Access Location Permission");
                builder.setMessage("Location permission required");


                builder.setNegativeButton("OK", (dialog, which) -> {
                    requestFineLocationPermission();

                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
            } else {
                requestFineLocationPermission();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MY_BACKGROUND_LOCATION_REQUEST);

        //requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MY_BACKGROUND_LOCATION_REQUEST);
    }

    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_FINE_LOCATION_REQUEST);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_FINE_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    requestFineLocationPermission();
                    // Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_BACKGROUND_LOCATION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}
