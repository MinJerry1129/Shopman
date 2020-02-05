package com.emp.auction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BasketAdapter extends ArrayAdapter<Basket> {

    private Context mContext;
    private List<Basket> allProductList = new ArrayList<>();

    public BasketAdapter(Context context, ArrayList<Basket> list) {
        super(context, 0 , list);
        mContext = context;
        allProductList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_basket ,parent,false);

        Basket currentMovie = allProductList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.productimage);
        Log.d("imageurl:",currentMovie.getmImage() );
        Glide.with(getContext())
                .load(currentMovie.getmImage())
                .into(image);
//        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.productname);
        name.setText(currentMovie.getmName());
        TextView price = (TextView) listItem.findViewById(R.id.productprice);
        price.setText(currentMovie.getmPrice()+"€");
        TextView count = (TextView) listItem.findViewById(R.id.productcount);
        count.setText(currentMovie.getmCount());
        return listItem;
    }
}
