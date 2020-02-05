package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ForgotActivity extends AppCompatActivity {
    private static final String TAG = "ForgotActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _username;
    EditText _emailText;
    Button _reset;
    TextView _login;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        _username = (EditText) findViewById(R.id.forgot_input_username);
        _emailText = (EditText) findViewById(R.id.forgot_input_email);
        _reset = (Button) findViewById(R.id.btn_reset);
        _login = (TextView) findViewById(R.id.link_login);
        _reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChanePassword();
            }
        });
        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void ChanePassword() {
        if (!validate()) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        String email = _emailText.getText().toString();
        String username = _username.getText().toString();
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("email",email);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/forgotpass")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                Log.d("result:", result.toString());
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.valid_forgotcheckemail),Toast.LENGTH_LONG).show();
                                }else if (status.equals("email")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.valid_forgotemail),Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.valid_forgotuser),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.valid_forgotuser),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String username = _username.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("Enter your user name");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (username.isEmpty()) {
            _username.setError("Enter your password");
            valid = false;
        } else {
            _username.setError(null);
        }
        return valid;
    }
}

