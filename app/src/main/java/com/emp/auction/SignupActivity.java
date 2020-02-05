package com.emp.auction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    EditText _email;
    EditText _usernameText;
    EditText _mobileText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;


    EditText _selleremail;
    EditText _sellerusernameText;
    EditText _sellermobileText;
    EditText _sellerpasswordText;
    EditText _sellerreEnterPasswordText;
    EditText _shopname;
    EditText _shopaddress;
    TextView _shoplocation;
    ImageView _shopImgae;
    ImageView _Btnshopaddress;



    Button _sellersignupButton;
    TextView _sellerloginLink;

    Button _sel_buyer;
    Button _sel_seller;
    LinearLayout _buyer_layout;
    LinearLayout _seller_layout;
    Image image;
    String filePath;
    String sel_image;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_signup);
//        getSupportActionBar().hide(); //hide the title bar
        Common.getInstance().setShopaddress("");
        Common.getInstance().setShoplocation("");
        _email=(EditText) findViewById(R.id.input_email);
        _usernameText = (EditText) findViewById(R.id.input_username);
        _mobileText = (EditText) findViewById(R.id.input_mobile);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _sellerusernameText=(EditText) findViewById(R.id.input_seller_name);
        _selleremail = (EditText) findViewById(R.id.input_seller_email);
        _sellermobileText = (EditText) findViewById(R.id.input_seller_mobile);
        _sellerpasswordText = (EditText) findViewById(R.id.input_seller_password);
        _sellerreEnterPasswordText = (EditText) findViewById(R.id.input_seller_reEnterPassword);
        _shopname = (EditText) findViewById(R.id.input_shopname);
        _shopaddress = (EditText) findViewById(R.id.input_shopaddress);
        _shoplocation = (TextView) findViewById(R.id.input_shoplocation);
        _Btnshopaddress = (ImageView) findViewById(R.id.Btnshopaddress);

        _sellersignupButton = (Button) findViewById(R.id.btn_seller_signup);
        _sellerloginLink = (TextView) findViewById(R.id.link_seller_login);
        _shopImgae = (ImageView) findViewById(R.id.shop_img);
        _sel_buyer = (Button) findViewById(R.id.btn_buyer_sel);
        _sel_seller = (Button) findViewById(R.id.btn_seller_sel);
        _buyer_layout = (LinearLayout) findViewById(R.id.buyer_layout);
        _seller_layout = (LinearLayout) findViewById(R.id.seller_layout);

        _sel_buyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyer_sel();
            }
        });

        _sel_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seller_sel();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _Btnshopaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupMapActivity.class);
                startActivity(intent);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Common.getInstance().setShoplocation("");
                Common.getInstance().setShopaddress("");
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _sellersignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellersignup();
            }
        });

        _sellerloginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA ,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    public LatLng getLocationFromAddress(){
        LatLng p1 = null;
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        String strAddress = _shopaddress.getText().toString();
        try{
            address = coder.getFromLocationName(strAddress, 5);
            Log.d("adadfad:", "mayyou");
            if (address == null) {
                Log.d("adadfad:", "mayyou");
//                Toast.makeText(getBaseContext(),getResources().getString(R.string.toast_sign_shopaddress), Toast.LENGTH_LONG).show();
                onSignupFailed(getResources().getString(R.string.toast_sign_shopaddress));
                return null;
            }
            if (address.toString().equals("[]")){//
//                Toast.makeText(getBaseContext(),"Input Correct shop address", Toast.LENGTH_LONG).show();
                onSignupFailed(getResources().getString(R.string.toast_sign_shopaddress));
                return null;
            }
            Log.d("adadfadf:", address.toString());
            Address location = address.get(0);

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            _shoplocation.setText(p1.latitude + "," + p1.longitude);
            Log.d("adf:",p1.toString());
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return p1;
    }

    protected void onStart() {

        super.onStart();
        setready();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // or get a single image only
            image = ImagePicker.getFirstImageOrNull(data);

            if(image!=null) {
                //imageView.setImageBitmap();
                filePath=image.getPath();
                sel_image = "yes";
                _shopImgae.setImageURI(Uri.parse(filePath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setready(){
        _shoplocation.setText(Common.getInstance().getShoplocation());
        _shopaddress.setText(Common.getInstance().getShopaddress());

    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(getResources().getString(R.string.toast_sign_correctinfo));
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String email = _email.getText().toString().toLowerCase ();
        String username = _usernameText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        //String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobile",mobile);
        json.addProperty("username", username);
        json.addProperty("password",password);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/signupbuyer")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    onSignupSuccess();
                                } else if (status.equals("existuser")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_existuser));
                                } else if (status.equals("existemail")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_existemail));
                                } else if (status.equals("fail")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_fail));
                                }
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void sellersignup() {
        getLocationFromAddress();
        Log.d(TAG, "Signup");
        if (sel_image != "yes"){
            onSignupFailed(getResources().getString(R.string.toast_sign_selshopimage));
            return;
        }

        if (!validate_shop()) {
            onSignupFailed(getResources().getString(R.string.toast_sign_correctinfo));
            return;
        }



        _signupButton.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Bitmap bm = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        String email = _selleremail.getText().toString().toLowerCase ();
        String username = _sellerusernameText.getText().toString();
        String mobile = _sellermobileText.getText().toString();
        String password = _sellerpasswordText.getText().toString();
        String shopname = _shopname.getText().toString();
        String shopaddress  = _shopaddress.getText().toString();
        String shoplocation  = _shoplocation.getText().toString();
        String shopimage = encodedImage;

        //String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        JsonObject json = new JsonObject();
        json.addProperty("email",email);
        json.addProperty("mobile",mobile);
        json.addProperty("username", username);
        json.addProperty("password",password);
        json.addProperty("shopname",shopname);
        json.addProperty("shopaddress",shopaddress);
        json.addProperty("shoplocation",shoplocation);
        json.addProperty("shopimage",shopimage);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/signupseller")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    onSignupSuccess();
                                } else if (status.equals("existuser")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_existuser));
                                } else if (status.equals("existemail")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_existemail));
                                } else if (status.equals("fail")) {
                                    onSignupFailed(getResources().getString(R.string.toast_sign_fail));
                                }
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onPickerImage( View view) {
        ImagePicker.create(this).single().toolbarFolderTitle("Folder").start();


    }

    public void buyer_sel() {
        _seller_layout.setVisibility(LinearLayout.GONE);
        _buyer_layout.setVisibility(LinearLayout.VISIBLE);
        _sel_buyer.setTextColor(0xffe8732d);
        _sel_buyer.setBackgroundColor(0xffffffff);
        _sel_seller.setTextColor(0xffffffff);
        _sel_seller.setBackgroundColor(0xffe8732d);
    }

    public void seller_sel() {
        _seller_layout.setVisibility(LinearLayout.VISIBLE);
        _buyer_layout.setVisibility(LinearLayout.GONE);
        _sel_buyer.setTextColor(0xffffffff);
        _sel_buyer.setBackgroundColor(0xffe8732d);
        _sel_seller.setTextColor(0xffe8732d);
        _sel_seller.setBackgroundColor(0xffffffff);
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Common.getInstance().setUser(_usernameText.getText().toString());
        finish();
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String username = _usernameText.getText().toString();
        String email = _email.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError(getResources().getString(R.string.valid_email));
            valid = false;
        } else {
            _email.setError(null);
        }

        if (username.isEmpty() || username.length()<4){
            _usernameText.setError(getResources().getString(R.string.valid_usercharacter));
            valid = false;
        }
        else {
            _usernameText.setError(null);
        }

        if (mobile.isEmpty() || !mobile.matches("\\d+(?:\\.\\d+)?") || mobile.length()<8|| mobile.length()>14) {
            _mobileText.setError(getResources().getString(R.string.valid_mobile));
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError(getResources().getString(R.string.valid_mobilecharacter));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError(getResources().getString(R.string.valid_passwordmatch));
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
    public boolean validate_shop() {
        boolean valid = true;
        String username = _sellerusernameText.getText().toString();
        String email = _selleremail.getText().toString();
        String mobile = _sellermobileText.getText().toString();
        String password = _sellerpasswordText.getText().toString();
        String reEnterPassword = _sellerreEnterPasswordText.getText().toString();
        String shopname = _shopname.getText().toString();
        String shopaddress = _shopaddress.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError(getResources().getString(R.string.valid_email));
            valid = false;
        } else {
            _email.setError(null);
        }

        if (username.isEmpty() || username.length()<4){
            _usernameText.setError(getResources().getString(R.string.valid_usercharacter));
            valid = false;
        }
        else {
            _usernameText.setError(null);
        }

        if (mobile.isEmpty() || !mobile.matches("\\d+(?:\\.\\d+)?") || mobile.length()<8 || mobile.length()>14) {
            _mobileText.setError(getResources().getString(R.string.valid_mobile));
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError(getResources().getString(R.string.valid_mobilecharacter));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError(getResources().getString(R.string.valid_passwordmatch));
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (shopname.isEmpty()){
            _shopname.setError(getResources().getString(R.string.valid_shopname));
            valid = false;
        }
        else {
            _shopname.setError(null);
        }
        if (shopaddress.isEmpty()){
            _shopaddress.setError(getResources().getString(R.string.valid_shopaddress));
            valid = false;
        }
        else {
            _shopaddress.setError(null);
        }
        if (_shoplocation.equals("")){
            _shopaddress.setError(getResources().getString(R.string.valid_shopaddresscorrect));
            valid = false;
        }
        else {
            _shopaddress.setError(null);
        }
        return valid;
    }
}