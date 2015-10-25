package com.ashutosh.abatev1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Vostro-Daily on 10/14/2015.
 * Description : A Background asynchronous task that fetches data from the database, creates UI for fragment and inflates the UI with the data.
 */
public class UIHelper extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

    private Context ctx;
    private View rootView;
    private ArrayList<String> contentList;
    private ArrayList<String> uriList;
    public static String LOG_TAG;
    private ArrayList<Bitmap> mBitmaps;

    public UIHelper(Context ctx, View rootView) {
        this.ctx = ctx;
        this.rootView = rootView;
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(Void... voids) {
        // fetch images from server, store images in database, fetch images from database, show images to user

        DBHelper dbHelper = new DBHelper(ctx);
        // The database is created here when a call to getWritableDatabase() is made for the first time using the UserPostContract.
        // the call returns a read-write database
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        NetworkHelper.DBWriter dbWriter = new NetworkHelper.DBWriter(writableDatabase);
        dbWriter.executeProcess();

        NetworkHelper networkHelper = new NetworkHelper();
        networkHelper.fetchData(ctx);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();       // returns a read only database

        readDataFromDB(readableDatabase);

        readDataFromExtStorage();

        ArrayList<Bitmap> drawables = new ArrayList<>();
        //drawables = getDrawables();       // commented out
        return drawables;    // return drawables
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        super.onPostExecute(bitmaps);
        //((CardViewFragment)((Activity) ctx).getFragmentManager().findFragmentByTag("CardViewFragment")).showView(bitmaps);
        Toast.makeText(ctx, "bitmap : " + mBitmaps.toString(), Toast.LENGTH_SHORT).show();
        showView(bitmaps);
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

        contentList = new ArrayList<>();
        uriList = new ArrayList<>();

        for(int i=0; i<c.getCount(); i++){
            long id = c.getLong(c.getColumnIndex(UserPostContract.Post._ID));
            if(id>=0){
                String content = c.getString(c.getColumnIndex(UserPostContract.Post.COLUMN_NAME_POST_CONTENT));
                contentList.add(content);
                String uri = c.getString(c.getColumnIndex(UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI));
                uriList.add(uri);
            }
            c.moveToNext();         // move to next row
        }
        c.close();                  // close the cursor when data is read completely
    }


    private void readDataFromExtStorage() {
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

            options = new BitmapFactory.Options();    // creating a new options ref for every loop count/image
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            options.inSampleSize = NetworkHelper.calculateInSampleSize(options, 100, 100);

            options.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(path, options);

            mBitmaps.add(bmp);
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
        ArrayList<Bitmap> drawables = mBitmaps;
        RecyclerView recyclerView = new RecyclerView(ctx);
        LinearLayoutManager llm = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(llm);
        RecyclerView.Adapter adapter = new MyRecyclerAdapter(contentList, uriList, drawables);
        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(null);
        ((ViewGroup) rootView).addView(recyclerView);
    }
}
