package com.ashutosh.abatev1;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Vostro-Daily on 10/27/2015.
 */
public class XRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View rootView = inflater.inflate(R.layout.news_item_layout, null, false);

        CardView cardView = new CardView(ctx);
        cardView.addView(rootView);

//                TextView tv = new TextView(ctx);
//        tv.setText("If you are the smartest person in the room than you are in the wrong room.");
//        cardView.addView(tv);

        MyViewHolder holder = new MyViewHolder(cardView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
