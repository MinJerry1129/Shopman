package com.emp.auction;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PostActivity extends AppCompatActivity {
    private EditText _mEditName;
    private TextView _mEditDescription;
    private EditText _mEditPrice;
    private Button mButton;

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
    List<Image> images;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if(Common.getInstance().getUser().equals("guest12345")) onBackPressed();

        _mEditName = (EditText) findViewById(R.id.editProductName);
        _mEditDescription = (EditText) findViewById(R.id.txtDescription);
        _mEditPrice = (EditText) findViewById(R.id.editPrice);

        imageView = (ImageView) findViewById(R.id.image_View);
        sliderLayout = (SliderLayout) findViewById(R.id.imageSlider);
        mc = new MediaController(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_addproduct);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        imageUrls=new ArrayList<>();
        videoPath="";
        imageDone=false;
        setImageEventListner();

//slider
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(5000);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA ,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        //setSliderViews();
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
            images = ImagePicker.getImages(data);

            for(Image theImage : images){

                String filePath=theImage.getPath();
                if( filePath.endsWith("jpg") || filePath.endsWith("JPG") || filePath.endsWith("png") || filePath.endsWith("bmp")) {
                    if(imageUrls.size()<6) {
                        imageUrls.add(filePath);
                    }
                }
                else{
                }
            }
            setSliderViews();

            ImageView imageCheckImage=(ImageView)findViewById(R.id.imageCheckImage);
            if(imageUrls.size()>0){
                imageDone=true;
                imageCheckImage.setImageResource(R.drawable.ic_done);
                imageCheckImage.setVisibility(View.VISIBLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void setSliderViews() {
//        sliderLayout.clearSliderViews();
        this.runOnUiThread(new Runnable() {
            public void run() {
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

    public void onPickImage(View view) {
        LinearLayout layAddImage=(LinearLayout) findViewById(R.id.imageSelectButton);
        layAddImage.setVisibility(View.GONE);

        ImagePicker.create(this).limit(5).start();
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

        if(imageDone==false){
            valid=false;
            imageCheckImage.setVisibility(View.VISIBLE);
            imageCheckImage.setImageResource(R.drawable.ic_error);
        }
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
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();
            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            uploadImages.put(encodedImage);
        }
        JsonObject json = new JsonObject();
        json.addProperty("shopid",Common.getInstance().getShopid());
        json.addProperty("productname",_mEditName.getText().toString());
        json.addProperty("productdes",  _mEditDescription.getText().toString());
        json.addProperty("productvalue", _mEditPrice.getText().toString());
        json.addProperty("productimg", uploadImages.toString());

        //final File fileToUpload = new File(imageUrls.get(0));   //temp 0
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/selleraddproductandroid")
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
    @Override
    public void onBackPressed() {
        // Disable going back to the UserMainActivity
//        moveTaskToBack(true);
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
