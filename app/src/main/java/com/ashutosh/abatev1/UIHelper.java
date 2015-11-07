package com.ashutosh.abatev1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Vostro-Daily on 10/14/2015.
 * Description : A Background asynchronous task that fetches data from the database, creates UI for fragment and inflates the UI with the data.
 */
public class UIHelper extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

    private Context ctx;
    private View rootView;
    private ArrayList<String> mContentList;
    private ArrayList<String> mCategoryList;
    public static String LOG_TAG;
    private ArrayList<Bitmap> mBitmaps;
    private RecyclerView.Adapter adapter;

    public UIHelper(Context ctx, View rootView) {
        this.ctx = ctx;
        this.rootView = rootView;
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(Void... voids) {
        // Note: Remember a toast can't be launched from background thread.
        // fetch images from server, store images in database, fetch images from database, show images to user

        DBHelper dbHelper = new DBHelper(ctx);
        // The database is created here when a call to getWritableDatabase() is made for the first time using the UserPostContract.
        // the call returns a read-write database
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        // database should be cleared for the first time the application is installed.
        int rows = writableDatabase.delete(UserPostContract.Post.TABLE_NAME, null, null);

        NetworkHelper.DBWriter dbWriter = new NetworkHelper.DBWriter(writableDatabase);
        dbWriter.executeProcess();

        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.fetchData(ctx);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();       // returns a read only database

        readDataFromDB(readableDatabase);

        readDataFromExtStorage();

        dbHelper.close();                               // close the databse after use

        ArrayList<Bitmap> drawables = new ArrayList<>();
        //drawables = getDrawables();       // commented out
        return drawables;    // return drawables
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        super.onPostExecute(bitmaps);
        //((CardViewFragment)((Activity) ctx).getFragmentManager().findFragmentByTag("CardViewFragment")).showView(bitmaps);
        Toast.makeText(ctx, "bitmap : " + mBitmaps.toString(), Toast.LENGTH_SHORT).show();
        showView(bitmaps);              // doesn't work for now
    }

    private void readDataFromDB(SQLiteDatabase readableDB){
        // reads data from the SQLite database using the Cursor returned from the query
        String [] projection = { UserPostContract.Post._ID, UserPostContract.Post.COLUMN_NAME_POST_CONTENT,
                UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI};

        Cursor c = readableDB.query(UserPostContract.Post.TABLE_NAME,
                projection,     // set of attributes/columns we require from the SQL query to the DB
                null,       // where clause
                null,       // selection args
                null,       // group by
                null,       // having
                null);      // order by

        c.moveToFirst();

        mContentList = new ArrayList<>();
        mCategoryList = new ArrayList<>();
        int x = c.getCount();   int y = 0;

        // read data from the table rows using the cursor
        for(int i=0; i<c.getCount(); i++){
            long id = c.getLong(c.getColumnIndex(UserPostContract.Post._ID));
            if(id>=0){
                y++;
                String content = c.getString(c.getColumnIndex(UserPostContract.Post.COLUMN_NAME_POST_CONTENT));
                mContentList.add(content);
                String uri = c.getString(c.getColumnIndex(UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI));
                mCategoryList.add(uri);
            }
            c.moveToNext();         // move to next row
        }
        c.close();                  // close the cursor when data is read completely
    }


    private void readDataFromExtStorage() {             // reads bitmaps from external storage
        LOG_TAG = UIHelper.class.toString();
        String dummyFile = "file";
        File fileX = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), dummyFile);
        //        /storage/emulated/0/Android/data/com.ashutosh.abatev1/files/Pictures
        String path = fileX.getParentFile().toString();      // returns the path of ext storage where images are stored
        //        Log.i("Ext_path", path);
        boolean readable = NetworkHelper.isExternalStorageReadable();
        if(!readable){
            Log.e(LOG_TAG, "External storage isn't readable");
            return; // do nothing
        }
        File extDir = new File(path);                   // returns the External storage directory as specified by the path
        Log.i(LOG_TAG, extDir.toString());
        File files[] = extDir.listFiles();              // returns the list of files in the external directory
        Log.i(LOG_TAG, files[1].toString());
        readBitmaps(files);
    }

    private void readBitmaps(File[] files) {
        // reads bitmaps from the list of files specified

//        try {
//            String path = files[0].getPath();
            /*InputStream is = new FileInputStream(path);
            BufferedInputStream bis = new BufferedInputStream(is);      // chain a buffer to the inputStream
            byte [] data = {};
            bis.read(data);             // read data to the data bytes
            bis.close();

            ByteArrayInputStream byteStream = new ByteArrayInputStream(data);   // convert databytes to datastream to read content smoothly
            Bitmap bmp1 =  BitmapFactory.decodeStream(byteStream);*/
        mBitmaps = new ArrayList<>();
        BitmapFactory.Options options;
        Bitmap bmp;                     String path;

        for(int i=0;i<files.length;i++) {
            boolean isDirectory = files[i].isDirectory();
            if(isDirectory){
                //move to next file, if this file is a directory
                continue;
            }
            path = files[i].getPath();              // get the file path

            mBitmaps.add(NetworkHelper.getResizedBitmap(path));
        }
/*
        }catch (IOException e){
            Log.e(LOG_TAG, "File Not Readable-" + e.toString());
        }*/
    }

    public void showView(ArrayList<Bitmap> bitmaps){

        //ArrayList<String> strings = new ArrayList<>(Arrays.asList("Pollution", "Corruption", "Poverty", "Crime", "Poor literacy",
        //       "x", "y", "z", "9", "10"));

//        ArrayList<Bitmap> drawables = bitmaps;
        final ArrayList<Bitmap> drawables = mBitmaps;
        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView_cardViewFragment);
//        LinearLayoutManager llm = new LinearLayoutManager(ctx);
//        recyclerView.setLayoutManager(llm);       // layoutManager is already set in the CardViewFragment
        adapter = new MyRecyclerAdapter(mContentList, mCategoryList, mBitmaps);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());        // use the default animations

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int visibleItemCount, totalItemCount, pastVisiblesItems;
            boolean loading = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                visibleItemCount = llm.getChildCount();
                totalItemCount = llm.getItemCount();
                pastVisiblesItems = llm.findFirstVisibleItemPosition();

                if (!loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        // if scrolling reaches end of the list
                        loading = true;
                        Log.v("...", "Last Item Wow !");
                        Toast.makeText(ctx, "Reached End : Load More Items", Toast.LENGTH_SHORT).show();

                        // launch an async task to load more images into the adapter
                        LoadMoreBitmaps moreBitmaps = new LoadMoreBitmaps();
                        moreBitmaps.execute();
                    }
                } else {
                    loading = false;
                }
            }
        });

//        ((ViewGroup) rootView).addView(recyclerView);
    }

    public class LoadMoreBitmaps extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            int w = SuperHelper.getImageWidth(ctx);
            int h = SuperHelper.getImageHeight(ctx);

            try {
                mBitmaps.add(Picasso.with(ctx).load("http://i.imgur.com/DvpvklR.png").resize(w, h).get());
                mBitmaps.add(Picasso.with(ctx).load("http://i.imgur.com/dM5hfCg.jpg").resize(w, h).get());
                mBitmaps.add(Picasso.with(ctx).load("http://i.imgur.com/dM5hfCg.jpg").resize(w, h).get());
                mBitmaps.add(Picasso.with(ctx).load("http://i.imgur.com/Uj6PxCA.jpg").resize(w, h).get());
            } catch (IOException e) {
                Log.e(UIHelper.LOG_TAG, "Error loading images with picasso");
            }
            mCategoryList.add("xxxxx");              mCategoryList.add("yyyyy");
            mCategoryList.add("mnopq");              mCategoryList.add("abcde");
            mContentList.add("contentx");        mContentList.add("contentz");
            mContentList.add("mmmmm");           mContentList.add("zzzzz");
            adapter.notifyItemInserted(mBitmaps.size());
            return null;
        }
    }
}
