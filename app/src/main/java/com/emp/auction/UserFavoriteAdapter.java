package com.emp.auction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteAdapter extends ArrayAdapter<UserFavorite> {

    private Context mContext;
    private List<UserFavorite> allUserList = new ArrayList<>();

    public UserFavoriteAdapter(Context context, ArrayList<UserFavorite> list) {
        super(context, 0 , list);
        mContext = context;
        allUserList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_user ,parent,false);

        UserFavorite currentMovie = allUserList.get(position);

//        ImageView image = (ImageView) listItem.findViewById(R.id.shopimage);
//        Ion.with(getContext()).load(currentMovie.getmImage()).intoImageView(image);

        TextView name = (TextView) listItem.findViewById(R.id.username);
        name.setText(currentMovie.getmName());
        TextView email = (TextView) listItem.findViewById(R.id.useremail);
        email.setText(currentMovie.getmEmail());
        TextView mobile = (TextView) listItem.findViewById(R.id.usermobile);
        mobile.setText(currentMovie.getmMobile());
        return listItem;
    }
}
