package com.qsmp;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
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


public class LoginActivity extends AppCompatActivity {

    EditText edt_username,edt_password;
    LinearLayout layout_username, layout_mobilenumber;


    MaterialButton btn_login;
    private static AsyncHttpClient client ;

    connectionDector dector;
    String Str_UserName;
    String Str_Password;

    String Str_Userid;

    String Str_Rudder_Id;
    String Str_cloud_id;
    String saml_token;
    String Str_Stun_Server_list_data="";


    SharedPreferences sp_userdetail;
    ImageView img_login_actionbar_back;
    String Str_Device_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);

        layout_username = findViewById(R.id.layout_username);

        btn_login = findViewById(R.id.btn_login);
        client= new AsyncHttpClient();
        dector=new connectionDector(LoginActivity.this);


        sp_userdetail= getSharedPreferences("userdetail.txt", Context.MODE_PRIVATE);

        Str_Rudder_Id=getIntent().getStringExtra("Rudder_id");

        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (!isValidEmail(edt_username.getText().toString().trim())) {
                    // edt_username.setError("Enter a valid address");
                    layout_username.setBackgroundResource(R.drawable.edittext_outer_border_error);
                } else {
                    layout_username.setBackgroundResource(R.drawable.edittext_outer_border);
                }*/
            }
        });


        btn_login.setOnClickListener(v ->
        {
            if(dector.checkNetwork())
            {

                if(edt_username.getText().toString().trim().length()>0)
                {

                    if(edt_password.getText().toString().trim().length()>0)
                    {

                        try {



                            Str_UserName=edt_username.getText().toString();
                            Str_Password=edt_password.getText().toString();
                            try {
                                Str_Device_uuid = Settings.Secure.getString(LoginActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                Log.d("device_uuid",Str_Device_uuid);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                            //{"password": "12345678", "rudder_id": "zenexim1", "username": "qnmatrix_android1"}


                            JSONObject Rider_reg_info_Jobj=new JSONObject();



                            Rider_reg_info_Jobj.put("username",edt_username.getText().toString());
                            Rider_reg_info_Jobj.put("password",edt_password.getText().toString());
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
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Please Enter Password",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Please Enter Userid",Toast.LENGTH_SHORT).show();
                }







            }
            else
            {
                Toast.makeText(LoginActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void Set_Rider_Register_data(JSONObject jsonObject) throws UnsupportedEncodingException
    {
        final Dialog dialog;


        dialog = new Dialog(LoginActivity.this);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        // Set dialog title
        // dialog.setTitle("Custom Dialog");
        dialog.show();

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

        client.post(LoginActivity.this,url,entity,"application/json",new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("res_login",response.toString());

                try{

                    if(response.length()>0)
                    {

                        dialog.dismiss();
                        dialog.cancel();
                        if(response.getInt("responseCode")==200)
                        {

                            //Toast.makeText(LoginActivity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();

                            JSONArray datarray=response.getJSONArray("data");

                            saml_token=response.getString("saml_token");
                                if(datarray.length()>0)
                                {
                                    JSONObject jsonObject=datarray.getJSONObject(0);

                                    Str_Userid=jsonObject.getString("user_id");
                                    Str_cloud_id=jsonObject.getString("cloud_id");


                                    if(Str_Userid.length()>0)
                                    {

                                        SharedPreferences.Editor editor_data= sp_userdetail.edit();
                                        editor_data.putString("user_id",Str_Userid);
                                        editor_data.putString("user_name",Str_UserName);
                                        editor_data.putString("password",Str_Password);
                                        editor_data.putString("rudder_id",Str_Rudder_Id);
                                        editor_data.putString("cloud_id",Str_cloud_id);
                                        editor_data.putString("saml_token",saml_token);

                                        editor_data.commit();


                                        //startActivity(new Intent(LoginActivity.this,MainDashboard_Activity_QnUnGrid.class));
                                        startActivity(new Intent(LoginActivity.this, Qsmp_Dashboard_Activity.class));


                                    }


                                }








                        }  else
                        {
                            Toast.makeText(LoginActivity.this, response.getString("responseMessage"), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
                dialog.dismiss();
                dialog.cancel();

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
              /*  dialog.dismiss();
                dialog.cancel();*/
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dialog.dismiss();
                dialog.cancel();
                Toast.makeText(LoginActivity.this, "something went wrong", Toast.LENGTH_LONG).show();
            }

        });

    }



    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            //android Regex to check the email address Validation
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /* boolean isvalidMobileNumber(String mobilenumber,String countrycode){
        // String swissNumberStr = "044 668 18 00";
         boolean valid = true;
         PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
         try {
             Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(mobilenumber, countrycode);
 //  This will check if the phone number is real and it's length is valid.
             boolean isPossible = phoneUtil.isPossibleNumber(swissNumberProto);
             if(isPossible){
                 valid = true;
             }else{
                 valid = false;
             }
         } catch (NumberParseException e) {
             System.err.println("NumberParseException was thrown: " + e.toString());
         }
         return valid;
     }*/


}