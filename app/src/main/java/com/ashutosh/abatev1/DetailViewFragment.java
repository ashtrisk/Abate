package com.ashutosh.abatev1;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailViewFragment extends Fragment {

    private static final String POST_SHARE_HASHTAG = " #ABATE";
    private String mDescription = "";
    private String mCategory = "";
    private Bitmap mBitmap ;

    public DetailViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_view, container, false);

        Intent intent = getActivity().getIntent();
        if(intent == null){
            return rootView;
        }

        mDescription = intent.getStringExtra(Intent.EXTRA_TEXT);
        mCategory = intent.getStringExtra(Intent.CATEGORY_INFO);
        byte[] byteArray = intent.getByteArrayExtra("IMAGE");

        mBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);     // bytearray decoded to bitmap

        TextView categ_textView = (TextView)rootView.findViewById(R.id.textView_post_categ);
        categ_textView.setText(mCategory);

        TextView desc_textView = (TextView)rootView.findViewById(R.id.textView_post_desc);
        desc_textView.setText(mDescription);

        ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView_detail);
        imageView.setImageBitmap(mBitmap);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This inflates or adds items to the action bar if it is present
        inflater.inflate(R.menu.detailframent, menu);
        // get the share menu item
        MenuItem shareItem = menu.findItem(R.id.action_share);
        // get the shareActionProvider for the share menu item
        ShareActionProvider shareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(shareItem, shareActionProvider);

        // Attach an intent to the share action provider
        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(createShareForecastIntent());
        }else{
            Log.e(DetailActivity.class.toString(), "share Action Provider is null");
        }
    }

    private Intent createShareForecastIntent(){
        // creates and returns the intent whenever this method is called
        // An implicit intent that calls an activity which can perform the action send.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // this flag is set to prevent the sharing activity to being pushed onto the activity stack,
        // so that when user clicks the back button the user returns to the sunshine app rather than some other(sharing activity) activity.
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // we are going to share plain text in from our sunshine app.
        shareIntent.setType("text/plain");
        // share the weather forecast string + the hash tag #SunshineApp
        shareIntent.putExtra(Intent.EXTRA_TEXT, mCategory+"\n"+mDescription + POST_SHARE_HASHTAG);

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();

        return shareIntent;
    }


}
