package com.emp.auction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenActivity extends AppCompatActivity {
    PhotoView imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        imageview =(PhotoView) findViewById(R.id.photoView);
        Glide.with(this)
                .load(Common.getInstance().getImageurl())
                .into(imageview);
    }
}
