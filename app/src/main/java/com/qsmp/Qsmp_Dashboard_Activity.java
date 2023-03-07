package com.qsmp;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class Qsmp_Dashboard_Activity extends AppCompatActivity {

    LinearLayout ll_beaconlayout,ll_connect,ll_way_finding;
    private boolean backPressedToExitOnce = false;
    SharedPreferences sp_userdetail;
    String saml_token;
    private static AsyncHttpClient client ;
    connectionDector dector;
    String Str_UserName;
    String Str_Rudder_Id;
    ImageView img_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsmp_dashboard);
        client= new AsyncHttpClient();
        dector=new connectionDector(Qsmp_Dashboard_Activity.this);

        try {
            sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);
            Str_UserName=sp_userdetail.getString("user_name",null);
            Str_Rudder_Id=sp_userdetail.getString("rudder_id",null);

        }catch (Exception e)
        {
            e.printStackTrace();
        }


        saml_token=sp_userdetail.getString("saml_token",null);

        Log.d("saml_token",""+saml_token);
        ll_beaconlayout = findViewById(R.id.ll_beaconlayout);
        ll_connect = findViewById(R.id.ll_connect);
        ll_way_finding = findViewById(R.id.ll_way_finding);
        img_settings = findViewById(R.id.img_settings);


        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPictureialog();
                showMenu(v);
            }
        });
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





        ll_beaconlayout.setOnClickListener(v -> {
            //Intent i = new Intent(Qsmp_Dashboard_Activity.this,Scan_Activity.class);
            Intent i = new Intent(Qsmp_Dashboard_Activity.this,Scan_Extra_Activity.class);
            i.putExtra("call_scanactivity","qsmp_dashboard");
            startActivity(i);
        });


        ll_connect.setOnClickListener(v -> {
            //Intent i = new Intent(Qsmp_Dashboard_Activity.this,Scan_Activity.class);
            Intent i = new Intent(Qsmp_Dashboard_Activity.this,Wifi_Auto_Connect_Activity.class);
            i.putExtra("call_scanactivity","qsmp_dashboard");
            startActivity(i);
        });

        ll_way_finding.setOnClickListener(v ->
        {
            Intent i = new Intent(Qsmp_Dashboard_Activity.this,Call_Interface_Scan_Method_Activity.class);
            startActivity(i);
        });



    }




    private void showPictureialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.selectpic_dialog, null);
        dialogBuilder.setView(dialogView);

       /* EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
        editText.setText("test label");*/
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.TOP);
// set the margin
        alertDialog.getWindow().getAttributes().verticalMargin = 0.07F;

        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isLocationEnabled(Qsmp_Dashboard_Activity.this)){
            //perfomlogin();
            setBluetooth(true);

        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Qsmp_Dashboard_Activity.this);
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



    public void showMenu(View v) {
        /*PopupMenu popup1 = new PopupMenu(this, v);
        popup1.setOnMenuItemClickListener(MainDashboard_Activity_QnUnGrid.this);
        popup1.inflate(R.menu.menu_example);

        popup1.show();*/



        PopupMenu popup = new PopupMenu(Qsmp_Dashboard_Activity.this, v);

        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.menu_qsmp, popup.getMenu());
        Menu menuOpts = popup.getMenu();

        menuOpts.findItem(R.id.menu_username).setTitle(Str_UserName);
        menuOpts.findItem(R.id.menu_rudderid).setTitle(Str_Rudder_Id);


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.logout:
                    {
                        setlogoutdata();
                        break;
                    }
                    case R.id.re_authtnticate:
                    {
                        //Re_authtnticate_data();
                        break;
                    }
                    case R.id.profile:
                    {

                        break;
                    }
                    case R.id.about:
                    {
                        startActivity(new Intent(Qsmp_Dashboard_Activity.this, About_Activity.class));
                        break;
                    }
                    case R.id.menu_username:
                    {

                        break;
                    }

                }
                return true;
            }
        });
        popup.show();

    }


    public void setlogoutdata()
    {


        SharedPreferences sp_detail=getSharedPreferences("userdetail.txt", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp_detail.edit();
        editor.clear();
        editor.commit();



        this.finish();
        Intent intent=new Intent(Qsmp_Dashboard_Activity.this, Rudder_IdName_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }


    public boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_CONNECT);
                int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_SCAN);
                return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.enable();
            }
        } else if (!enable && isEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_CONNECT);
                int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), BLUETOOTH_SCAN);
                return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && bluetoothAdapter.disable();
            }
        }
        return true;
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
            Toast.makeText(Qsmp_Dashboard_Activity.this, "Press again to exit", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;


                }
            }, 3000);
        }
        //super.onBackPressed();

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
        client.post(Qsmp_Dashboard_Activity.this,url,entity,"application/json",new JsonHttpResponseHandler()
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
                                }




                            }
                        }
                        else
                        {
                            Toast.makeText(Qsmp_Dashboard_Activity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();


                        }
                    }
                    else
                    {
                        Toast.makeText(Qsmp_Dashboard_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
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
                Toast.makeText(Qsmp_Dashboard_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(Qsmp_Dashboard_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(Qsmp_Dashboard_Activity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

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
}