package com.emp.auction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ShopmainAdapter extends ArrayAdapter<Shopmain> {

    private Context mContext;
    private List<Shopmain> shopmainList = new ArrayList<>();

    public ShopmainAdapter(Context context, ArrayList<Shopmain> list) {
        super(context, 0 , list);
        mContext = context;
        shopmainList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_shop_main ,parent,false);

        Shopmain currentMovie = shopmainList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.shopimage);
        Glide.with(getContext())
                .load(currentMovie.getmImage())
                .into(image);
//        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.shopname);
        name.setText(currentMovie.getmName());
        return listItem;
    }
}
