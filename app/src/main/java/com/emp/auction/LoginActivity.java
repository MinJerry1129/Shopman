package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    Button _loginButton;
    TextView _signupLink;
    TextView _forgetlink;
    EditText _passwordText;
    EditText _usernameText;
    Spinner _selLang;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide(); //hide the title bar

        _usernameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.login_input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _forgetlink=(TextView) findViewById(R.id.link_forgot);
        _selLang = (Spinner) findViewById(R.id.sel_language);

        _usernameText.setText(Common.getInstance().getUsername());
        _passwordText.setText(Common.getInstance().getPassword());

        if (Common.getInstance().getUsername().equals("")){

        }else{
            login();
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _forgetlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotActivity.class);
                startActivity(intent);
            }
        });

        setSpinner();

    }
    public void setSpinner(){
        List<String> countryList = new ArrayList<>();
        countryList.add(getResources().getString(R.string.string_selectlanguage));
        countryList.add("En");
        countryList.add("Fr");
        countryList.add("Zn");
        final Configuration configuration = getResources().getConfiguration();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_spinner_item,countryList);
        adapter.setDropDownViewResource(R.layout.spinner_text_color);
        _selLang.setAdapter(adapter);

        _selLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Log.d("select:", "0 selected");
                    //Common.getInstance().getmUser().setmLanguage("English");
                    LocaleHelper.setLocale(getBaseContext(), "en");
                    configuration.setLayoutDirection(new Locale("en"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                    Common.getInstance().setLang("en");

                    Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if(position ==2){
                    LocaleHelper.setLocale(getBaseContext(), "fr");
                    configuration.setLayoutDirection(new Locale("fr"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                    Common.getInstance().setLang("fr");
                    Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if(position == 3){
                    Log.d("select:", "2 selected");
                    LocaleHelper.setLocale(getBaseContext(), "zh");
                    configuration.setLayoutDirection(new Locale("zh"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                    Common.getInstance().setLang("zh");
                    Intent intent=new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(getResources().getString(R.string.toast_login_valide));
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String username = _usernameText.getText().toString().toLowerCase ();
        String password = _passwordText.getText().toString();
        Log.d("username:", username);
        Log.d("password:", password);
        Log.d("device:", Common.getInstance().getMydeviceid());
        Log.d("lang:", Common.getInstance().getLang());
        Log.d("username:", Common.getInstance().getBaseURL()+"verifyuser.php");
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password",password);
        json.addProperty("deviceid",Common.getInstance().getMydeviceid());
        json.addProperty("lang",Common.getInstance().getLang());
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/verifyuser")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String role=result.get("role").getAsString();
                                    String userid = result.get("id").getAsString();
                                    String useremail = result.get("email").getAsString();
                                    Common.getInstance().setUserId(userid);
                                    Common.getInstance().setUseremail(useremail);
                                    onLoginSuccess(_usernameText.getText().toString().toLowerCase (),role);
                                    //categories loading
                                }else if (status.equals("wrongpassword")) {
                                    onLoginFailed(getResources().getString(R.string.toast_login_wrongpassword));
                                }else{
                                    onLoginFailed(getResources().getString(R.string.toast_login_nouser));
                                }
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        // TODO: Implement your own authentication logic here.
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String muser,String role) {
        _loginButton.setEnabled(true);
        Common.getInstance().setUser(muser);
        Common.getInstance().setUserRole(role);
        if(role.equals("buyer")){
            Intent intent = new Intent(this, HomeActivity.class).putExtra("role",role);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, ShopMainActivity.class).putExtra("role",role);
            startActivity(intent);
        }

        finish();
    }

    public void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError(getResources().getString(R.string.valid_username));
            valid = false;
        } else {
            _usernameText.setError(null);
        }


        if (password.isEmpty()) {
            _passwordText.setError(getResources().getString(R.string.valid_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
