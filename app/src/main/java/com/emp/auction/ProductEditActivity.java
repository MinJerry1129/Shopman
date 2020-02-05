package com.emp.auction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProductEditActivity extends AppCompatActivity {
    private EditText _mEditName;
    private TextView _mEditDescription;
    private EditText _mEditPrice;
    private Button mButton;
    private  Button mDelete;

    private SliderLayout sliderLayout;
    private ImageView imageView;
    private VideoView videoView;
    private List<String> imageUrls;
    private String videoPath=null;

    private MediaController mc;

    private ImageView image_video_toggle;
    private boolean isVideo;

    private boolean imageDone;
    private boolean priceDone;
    private boolean descriptionDone;
    private boolean nameDone;

    String product_name ;
    String product_price;
    String product_des;
    String product_fav ;
    String product_image ;
    String shop_name;
    String shop_mobile;
    String shop_location;
    String shop_image;
    String sel_iamge = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        _mEditName = (EditText) findViewById(R.id.editProductName);
        _mEditDescription = (EditText) findViewById(R.id.txtDescription);
        _mEditPrice = (EditText) findViewById(R.id.editPrice);

        imageView = (ImageView) findViewById(R.id.image_View);
        mc = new MediaController(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //------------------
        mButton=(Button)findViewById(R.id.btnPost);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPost()){
                    newPost();
                }
            }
        });

        mDelete = (Button)findViewById(R.id.btnDelete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPost()){
                    onDelete();
                }
            }
        });

        imageUrls=new ArrayList<>();
        videoPath="";
        imageDone=false;
        setImageEventListner();

//slider
        sliderLayout = findViewById(R.id.imageSlider);

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(5000);
        //setSliderViews();
        setready();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    public void initview(){
        _mEditName.setText(product_name);
        _mEditDescription.setText(product_des);
        _mEditPrice.setText(product_price);
    }


    public void setready(){
        JsonObject json = new JsonObject();
        json.addProperty("userid",Common.getInstance().getUserId());
        json.addProperty("productid",Common.getInstance().getProductid());
        json.addProperty("shopid",Common.getInstance().getShopid());
        try {
            Ion.with(getBaseContext())
                    .load(Common.getInstance().getBaseURL()+"api/buyeroneproduct")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    String status_product = result.get("status_product").getAsString();
                                    if(status_product.equals("ok")){
                                        product_name = result.get("productname").getAsString();
                                        product_price = result.get("productvalue").getAsString();
                                        product_des = result.get("productdes").getAsString();
                                        product_fav = result.get("fav").getAsString();
                                        product_image = result.get("productimg").getAsString();
                                        shop_name = result.get("shopname").getAsString();
                                        shop_mobile = result.get("shopmobile").getAsString();
                                        shop_location = result.get("shoplocation").getAsString();
                                        shop_image = result.get("shopimg").getAsString();
                                        initview();
                                        setSliderViews_main();
                                    }
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


    private void setImageEventListner(){
        _mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckName=(ImageView) findViewById(R.id.imageCheckName);
                if(s.toString().trim().isEmpty()){
                    nameDone=false;
                    imageCheckName.setVisibility(View.GONE);
                }
                else{
                    nameDone=true;
                    imageCheckName.setImageResource(R.drawable.ic_done);
                    imageCheckName.setVisibility(View.VISIBLE);
                }
            }
        });
        _mEditDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckDescription=(ImageView) findViewById(R.id.imageCheckDescription);
                if(s.toString().trim().isEmpty()){
                    descriptionDone=false;
                    imageCheckDescription.setVisibility(View.GONE);
                }
                else{
                    descriptionDone=true;
                    imageCheckDescription.setImageResource(R.drawable.ic_done);
                    imageCheckDescription.setVisibility(View.VISIBLE);
                }
            }
        });
        _mEditPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageView imageCheckPrice=(ImageView) findViewById(R.id.imageCheckPrice);
                if(s.toString().isEmpty() || !s.toString().matches("\\d+(?:\\.\\d+)?") || Integer.valueOf(s.toString())<=0){
                    priceDone=false;
                    imageCheckPrice.setVisibility(View.GONE);
                }
                else{
                    priceDone=true;
                    imageCheckPrice.setImageResource(R.drawable.ic_done);
                    imageCheckPrice.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);

            for(Image theImage : images){
                String filePath=theImage.getPath();
                if( filePath.endsWith("jpg") || filePath.endsWith("JPG") || filePath.endsWith("png") || filePath.endsWith("bmp")) {
                    if(imageUrls.size()<5) {
                        imageUrls.add(filePath);
                    }
                }
                else{
                }
            }
            setSliderViews();
        }
        sel_iamge = "yes";
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setSliderViews() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                sliderLayout.removeAllSliders();
//        sliderLayout.removeAllViews();
                for (String theImageUrl: imageUrls) {
//            TextSliderView textSliderView = new TextSliderView(this);
//            textSliderView.image(theImageUrl).setScaleType(BaseSliderView.ScaleType.CenterCrop);
//            sliderLayout.addSlider(textSliderView);
                    File imgFile = new File(theImageUrl);
                    DefaultSliderView sliderView = new DefaultSliderView(getBaseContext());
                    sliderView.image(imgFile).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                    sliderLayout.addSlider(sliderView);
                    Log.d("Filepath:", theImageUrl);
                }
            }
        });

    }

    public void setSliderViews_main() {
        String[] imageUrl=product_image.split("_split_");
        for (int i=0;i<imageUrl.length;i++) {
            String theImageUrl=Common.getInstance().getBaseimageURL()+imageUrl[i];
//            imageUrls.add(theImageUrl);
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.image(theImageUrl).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            sliderLayout.addSlider(textSliderView);
        }
    }


    public void onPickImage(View view) {
        ImagePicker.create(this).limit(5).includeVideo(false).start();
//                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
//                .folderMode(true) // folder mode (false by default)
//                .toolbarFolderTitle("Folder") // folder selection title
//                .toolbarImageTitle("Tap to select") // image selection title
//                .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
//                .includeVideo(true) // Show video on image picker
//                .single() // single mode
//                .multi() // multi mode (default mode)
//                .limit(10) // max images can be selected (99 by default)
//                .showCamera(true) // show camera or not (true by default)
//                .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
//                .enableLog(false) // disabling log
//                .start(); // start image picker activity with request code
    }
    private boolean isValidPost(){
        ImageView imageCheckName=(ImageView) findViewById(R.id.imageCheckName);
        ImageView imageCheckImage=(ImageView) findViewById(R.id.imageCheckImage);
        ImageView imageCheckDescription=(ImageView) findViewById(R.id.imageCheckDescription);
        ImageView imageCheckPrice=(ImageView) findViewById(R.id.imageCheckPrice);
        boolean valid = true;

//        String productName = _mEditName.getText().toString();
//        String description = _mEditDescription.getText().toString();
//        String price = _mEditPrice.getText().toString();

        if(descriptionDone==false){
            valid=false;
            imageCheckDescription.setVisibility(View.VISIBLE);
            imageCheckDescription.setImageResource(R.drawable.ic_error);
        }
        if(priceDone==false){
            valid=false;
            imageCheckPrice.setVisibility(View.VISIBLE);
            imageCheckPrice.setImageResource(R.drawable.ic_error);
        }
        if(nameDone==false){
            valid=false;
            imageCheckName.setVisibility(View.VISIBLE);
            imageCheckName.setImageResource(R.drawable.ic_error);
        }

        return valid;
    }
    private void newPost(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        mButton.setEnabled(false);
        mButton.setBackgroundColor(Color.GRAY);

        JSONArray uploadImages = new JSONArray();
        for(String theImage:imageUrls){
            Bitmap bm = BitmapFactory.decodeFile(theImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            uploadImages.put(encodedImage);
        }
        JsonObject json = new JsonObject();
        json.addProperty("productid",Common.getInstance().getProductid());
        json.addProperty("productname",_mEditName.getText().toString());
        json.addProperty("productdes",  _mEditDescription.getText().toString());
        json.addProperty("productvalue", _mEditPrice.getText().toString());
        json.addProperty("productimg", uploadImages.toString());
        json.addProperty("selimage", sel_iamge);
        json.addProperty("imageurl", product_image);

        //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/sellereditproductandroid")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_success), Toast.LENGTH_LONG).show();
                                    mButton.setEnabled(false);
                                } else {
                                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_fail), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_checkinternet), Toast.LENGTH_LONG).show();
                                Log.d("err",e.toString());
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private  void onDelete(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Really delete this product?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int which) {
                        arg0.dismiss();
                        Delete();
                    }
                });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void Delete(){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Posting...");
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();

        JsonObject json = new JsonObject();
        json.addProperty("productid",Common.getInstance().getProductid());

        //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/sellerdelproduct")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                String status = result.get("status").getAsString();
                                if (status.equals("ok")) {
                                    Toast.makeText(getBaseContext(), "Successfully Deleted.", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getBaseContext(), "Deleted Fail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getBaseContext(), "Sorry. connect fail.", Toast.LENGTH_LONG).show();
                                Log.d("err",e.toString());
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ShopMainActivity.class);
        startActivity(intent);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    moveToHome();
                    return true;
                case R.id.navigation_favorite:
                    moveToFavorite();
                    return true;
                case R.id.navigation_addproduct:
                    moveToBasket();
                    return true;
                case R.id.navigation_logout:
                    moveToLogout();
                    return true;
            }
            return false;
        }
    };
    private void moveToHome(){
        Intent intent=new Intent(this, ShopMainActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToFavorite(){
        Intent intent=new Intent(this, ShopFavoriteActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToBasket(){
        Intent intent=new Intent(this, PostActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToLogout(){
        JsonObject json = new JsonObject();
        json.addProperty("username", Common.getInstance().getUsername());
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/logout")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Log.d("test:::", "NOOOOOOOOOOOOOO");
                            if (result != null) {
                                Common.getInstance().setUsername("");
                                Common.getInstance().setPassword("");
                                Intent intent=new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d("test:::", "NOOOOOOOOOOOOOO");
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
