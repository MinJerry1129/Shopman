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

public class ShopFavoriteAdapter extends ArrayAdapter<ShopFavorite> {

    private Context mContext;
    private List<ShopFavorite> allShopList = new ArrayList<>();

    public ShopFavoriteAdapter(Context context, ArrayList<ShopFavorite> list) {
        super(context, 0 , list);
        mContext = context;
        allShopList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_shop ,parent,false);

        ShopFavorite currentMovie = allShopList.get(position);

        ImageView image = (ImageView) listItem.findViewById(R.id.shopimage);
        Glide.with(getContext())
                .load(currentMovie.getmImage())
                .into(image);
//        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.shopname);
        name.setText(currentMovie.getmName());
        TextView email = (TextView) listItem.findViewById(R.id.shopemail);
        email.setText(currentMovie.getmEmail());
        TextView mobile = (TextView) listItem.findViewById(R.id.shopmobile);
        mobile.setText(currentMovie.getmMobile());
        TextView address = (TextView) listItem.findViewById(R.id.shopaddress);
        address.setText(currentMovie.getmAddress());
        return listItem;
    }
}
