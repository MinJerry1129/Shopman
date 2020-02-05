package com.emp.auction;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//import com.bumptech.glide.Glide;

public class
ProductmainAdapter extends RecyclerView.Adapter<ProductmainAdapter.ViewHolder> {
    private ArrayList<Productmain> mProduct;
    private Context mContext;

    public ProductmainAdapter(Context context, ArrayList<Productmain> data) {
        mContext=context;
        mProduct = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_product_main, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

//        Glide.with(mContext)
//                .asBitmap()
//                .load(mProduct.get(position).getmImage())
//                .into(holder.image);
        Glide.with(mContext)
                .load(mProduct.get(position).getmImage())
                .into(holder.image);
//        Ion.with(mContext).load(mProduct.get(position).getmImage()).intoImageView(holder.image);
//        Picasso.get().load(mProduct.get(position).getmImage()).into(holder.image);

        holder.name.setText(mProduct.get(position).getmName());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shop_id=mProduct.get(position).getmShopId();
                String product_id=mProduct.get(position).getmId();
                Common.getInstance().setShopid(shop_id);
                Common.getInstance().setProductid(product_id);

                Intent intent=new Intent(mContext, OneProductActivity.class).putExtra("ProductId",mProduct.get(position).getmId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mProduct.size()>10){
            return 10;
        }else{
            return mProduct.size();
        }

    }
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name);
        }
    }
}
