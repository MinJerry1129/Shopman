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

public class AllShopAdapter extends ArrayAdapter<AllShop> {

    private Context mContext;
    private List<AllShop> allShopList = new ArrayList<>();

    public AllShopAdapter(Context context, ArrayList<AllShop> list) {
        super(context, 0 , list);
        mContext = context;
        allShopList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_all_shop ,parent,false);

        AllShop currentMovie = allShopList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.shopimage);
        Glide.with(getContext())
                .load(currentMovie.getmImage())
                .into(image);
//        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        ImageView fav_image = (ImageView) listItem.findViewById(R.id.shopfavicon);
        if(currentMovie.getmFav().equals("yes"))
        {
            fav_image.setImageResource(R.drawable.fav_red);
        }else{
            fav_image.setImageResource(R.drawable.fav_black);
        }

        TextView name = (TextView) listItem.findViewById(R.id.shopname);
        name.setText(currentMovie.getmName());
        return listItem;
    }
}
