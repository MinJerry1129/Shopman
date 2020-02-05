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

public class ProductFavoriteAdapter extends ArrayAdapter<ProductFavorite> {

    private Context mContext;
    private List<ProductFavorite> allProductList = new ArrayList<>();

    public ProductFavoriteAdapter(Context context, ArrayList<ProductFavorite> list) {
        super(context, 0 , list);
        mContext = context;
        allProductList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_product,parent,false);

        ProductFavorite currentMovie = allProductList.get(position);

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
        return listItem;
    }
}
