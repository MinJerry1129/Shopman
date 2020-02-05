package com.emp.auction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ShopEditActivity extends AppCompatActivity {
    ImageView _shopimg;
    EditText _shopname;
    EditText _shopmobile;
    EditText _shopaddress;
    TextView _shoplocation;
    Image image;
    Button _update;
    String filePath;
    String sel_image="no";
    String sel_address="yes";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_edit);
        _shopimg = (ImageView) findViewById(R.id.shop_img);
        _shopname = (EditText) findViewById(R.id.input_shopname);
        _shopmobile = (EditText)findViewById(R.id.input_seller_mobile);
        _shopaddress = (EditText)findViewById(R.id.input_shopaddress);
        _shoplocation = (TextView)findViewById(R.id.input_shoplocation);
        _update = (Button) findViewById(R.id.btn_seller_update);
        _update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedata();
            }
        });
        getProductData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }
    private void getProductData(){
        JsonObject json = new JsonObject();
        json.addProperty("useremail",Common.getInstance().getUseremail());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/getsellermain")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_product = result.get("status_product").getAsString();
                                    String shop_id = result.get("shopid").getAsString();
                                    String shop_name = result.get("shopname").getAsString();
                                    String shop_mobile = result.get("shopmobile").getAsString();
                                    String shop_image = result.get("shopimage").getAsString();
                                    String shop_address = result.get("shopaddress").getAsString();
                                    String shop_location = result.get("shoplocation").getAsString();
                                    _shopname.setText(shop_name);
                                    _shopmobile.setText(shop_mobile);
                                    _shopaddress.setText(shop_address);
                                    _shoplocation.setText(shop_location);
                                    Glide.with(getBaseContext())
                                            .load(Common.getInstance().getBaseimageURL() + shop_image)
                                            .into(_shopimg);
                                    Log.d("Shop_id:", shop_id);

                                } else if (status.equals("fail")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(),getResources().getString(R.string.toast_checkinternet),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

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
                sel_address = "no";
                return null;
            }
            if (address.toString().equals("[]")){
                sel_address = "no";
                return null;
            }
            Log.d("adadfadf:", address.toString());
            Address location = address.get(0);

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            _shoplocation.setText(p1.latitude + "," + p1.longitude);
            sel_address = "yes";
            Log.d("adf:",p1.toString());
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return p1;
    }
    public void onPickerImage( View view) {
        ImagePicker.create(this).single().toolbarFolderTitle("Folder").start();
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
                _shopimg.setImageURI(Uri.parse(filePath));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void updatedata(){
        getLocationFromAddress();
        if (!validate_shop()) {
            onSignupFailed(getResources().getString(R.string.toast_sign_correctinfo));
            return;
        }
        String encodedImage = "";
        if(sel_image.equals("yes")){
            Bitmap bm = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        String mobile = _shopmobile.getText().toString();
        String shopname = _shopname.getText().toString();
        String shopaddress  = _shopaddress.getText().toString();
        String shoplocation  = _shoplocation.getText().toString();
        String shopimage = encodedImage;
        JsonObject json = new JsonObject();
        json.addProperty("email",Common.getInstance().getUseremail());
        json.addProperty("mobile",mobile);
        json.addProperty("shopname",shopname);
        json.addProperty("shopaddress",shopaddress);
        json.addProperty("shoplocation",shoplocation);
        json.addProperty("shopimage",shopimage);
        json.addProperty("selimage",sel_image);

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/updateshop")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                Log.d("adfadfadsfads:", result.get("url").getAsString());
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), "Update Success", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getBaseContext(), "Update Fail", Toast.LENGTH_LONG).show();
                                }
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
    public boolean validate_shop() {
        boolean valid = true;
        String mobile = _shopmobile.getText().toString();
        String shopname = _shopname.getText().toString();
        String shopaddress = _shopaddress.getText().toString();

        if (mobile.isEmpty() || !mobile.matches("\\d+(?:\\.\\d+)?") || mobile.length()<8) {
            _shopmobile.setError("Enter valid mobile number");
            valid = false;
        } else {
            _shopmobile.setError(null);
        }

        if (shopname.isEmpty()){
            _shopname.setError("Input shopname");
            valid = false;
        }
        else {
            _shopname.setError(null);
        }
        if (shopaddress.isEmpty()){
            _shopaddress.setError("Input shopaddress");
            valid = false;
        }
        else {
            _shopaddress.setError(null);
        }
        if (sel_address.equals("no")){
            _shopaddress.setError("Input correct shopaddress");
            valid = false;
        }
        else {
            _shopaddress.setError(null);
        }
        return valid;
    }
}
