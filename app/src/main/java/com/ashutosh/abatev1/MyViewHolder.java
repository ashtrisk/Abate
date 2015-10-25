package com.ashutosh.abatev1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vostro-Daily on 8/24/2015.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    ViewGroup viewGroup;

    MyViewHolder(View view){
        super(view);
        viewGroup = (ViewGroup)itemView;
    }
}
