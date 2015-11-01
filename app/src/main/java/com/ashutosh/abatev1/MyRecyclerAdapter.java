package com.ashutosh.abatev1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vostro-Daily on 8/24/2015.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

    ArrayList<String> mContentList;
    ArrayList<String> mUriList;
    ArrayList<Bitmap> mBitmaps;

    MyRecyclerAdapter(ArrayList<String> contentList, ArrayList<String> uriList, ArrayList<Bitmap> drawables){
        mContentList = contentList;
        mUriList = uriList;
        mBitmaps = drawables;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context ctx = parent.getContext();
        CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CardView cardView = new CardView(ctx);
        cardView.setLayoutParams(params);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ctx, DetailActivity.class);
//                ctx.startActivity(intent);        // listener when a cardView is clicked
                Intent intentx = new Intent(ctx, NavDrawerActivity.class);
                ctx.startActivity(intentx);

            }
        });

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View rootView = inflater.inflate(R.layout.news_item_layout, null, false);

        cardView.addView(rootView);

        // set edges round for the cardView
        int radius = 14;
        final float scale = ctx.getResources().getDisplayMetrics().density;
        int pixels = (int) (radius * scale + 0.5f);
        cardView.setRadius(pixels);

        MyViewHolder holder = new MyViewHolder(cardView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Context ctx = holder.itemView.getContext();
        //if(position < mBitmaps.size()) {        // only call getView if there are more items to load

        //holder.viewGroup.removeAllViews();              //overlapping of items solved here
        getViewForCard(ctx, position, holder.viewGroup);      // getView for the card

        //}
    }

    @Override
    public int getItemCount() {
        return getLeastSize();
    }

    public void getViewForCard(Context context, int pos, ViewGroup vg){
//        View view;
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
        Context ctx = context;
        LinearLayout ll = (LinearLayout) vg.findViewById(R.id.linearLayout_news_root);     //new LinearLayout(ctx);
//        ll.setLayoutParams(params);
//        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = (TextView)vg.findViewById(R.id.textView_news_date);  //new TextView(ctx);
        tv.setText(mUriList.get(pos));
        TextView tv1 = (TextView)vg.findViewById(R.id.textView_news_title);  //new TextView(ctx);
        tv1.setText(mContentList.get(pos));
//        ImageView imageView = new ImageView(ctx);
        //imageView.setMinimumHeight(50);
//        imageView.setMaxHeight(100);
//        imageView.setScaleType(ImageView.ScaleType.CENTER);
//        imageView.setAdjustViewBounds(true);
//        imageView.setImageResource(mBitmaps.get(pos));
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setImageBitmap(mBitmaps.get(pos));

        // create an image view and add bitmap to it.

//        DynamicImageView dImageView = new DynamicImageView(ctx);
//        dImageView.setLayoutParams(params);
//        dImageView.requestLayout();
//        dImageView.getLayoutParams().height = 500;
//        dImageView.getLayoutParams().width = 500;
//        if(pos<mBitmaps.size()){
//            Bitmap bmp = mBitmaps.get(pos);
//            dImageView.setImageBitmap(bmp);
//        }
//        dImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageView imageView = (ImageView) vg.findViewById(R.id.imageView_newsItem);
        if(imageView.getDrawable() != null){
            imageView.setImageDrawable(null);   // remove any image from imageview
        }

        imageView.setImageBitmap(mBitmaps.get(pos));

//        ll.addView(dImageView);       // commented out
//
//        ll.addView(tv);
//        ll.addView(tv1);

    }

    private int getLeastSize() {
        int s1 = mBitmaps.size();
        int s2 = mContentList.size();
        int s3 = mUriList.size();
        if(s1>=s2 && s1>=s3)
            return s1;
        else if(s2>=s1 && s2>=s3)
            return s2;
        else
            return s3;
    }


}
