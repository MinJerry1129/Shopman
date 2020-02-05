package com.emp.auction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    String androidDeviceId = "";
    private int SPLASH_TIME = 1000;
    private boolean firstFlag;
    private boolean secondFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.activity_splash_screen);


//get categories===============
        JsonObject json = new JsonObject();
        json.addProperty("foo", "bar");

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    moveToLogin();

            }
        },3000);
//        Thread timer = new Thread() {
//            public void run() {
//                try {
//                    sleep(SPLASH_TIME);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
////                    if (SharedPref.getPrefForLoginStatus(SplashActivity.this)) {
////
////                        ProjectUtils.genericIntent(SplashActivity.this, HomeActivity.class, null, true);
////                    } else {
////                        ProjectUtils.genericIntent(SplashActivity.this, LoginActivity.class, null, true);
////                    }
//                    Log.i("Splash","Screen");
//
//                }
//            }
//        };
//        timer.start();
    }
    private void moveToLogin(){
        androidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Common.getInstance().setMydeviceid(androidDeviceId);
        Log.d("deviceid", androidDeviceId);
        JsonObject json = new JsonObject();
        json.addProperty("deviceid", androidDeviceId);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/remember")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                Log.d("result:", result.toString());
                                String status = result.get("status").getAsString();
                                String username = result.get("username").getAsString();
                                String password = result.get("password").getAsString();
                                String lang = result.get("lang").getAsString();
                                Common.getInstance().setUsername(username);
                                Common.getInstance().setPassword(password);
                                Common.getInstance().setLang(lang);
                                if(lang.equals("")){
                                    final Configuration configuration = getResources().getConfiguration();
                                    LocaleHelper.setLocale(getBaseContext(), "fr");
                                    configuration.setLayoutDirection(new Locale("fr"));
                                    Common.getInstance().setLang("fr");
                                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                                }else{
                                    final Configuration configuration = getResources().getConfiguration();
                                    LocaleHelper.setLocale(getBaseContext(), lang);
                                    configuration.setLayoutDirection(new Locale(lang));
                                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                                }
                                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);//LoginActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_checkinternet), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }




    }
}
