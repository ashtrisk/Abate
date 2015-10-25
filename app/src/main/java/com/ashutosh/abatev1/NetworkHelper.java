package com.ashutosh.abatev1;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Vostro-Daily on 10/18/2015.
 * Description : Helps in fetching data from the server and storing it to the database.
 */
public class NetworkHelper {

    String LOG_TAG ;

    private Context ctx;        // used temporarily for reading data from resources directory

    public void fetchData(Context ctx){       // this method fetches data from the network and stores the same to the DB
        this.ctx = ctx;
        ArrayList<Bitmap> bitmaps = getBitmaps();
        storeInExtStorage(bitmaps);
    }

    public static class DBWriter {
        private SQLiteDatabase db;

        public DBWriter(SQLiteDatabase writableDatabase) {
            this.db = writableDatabase;
        }

        // writes network output to the database
        public void executeProcess() {
            // Dummy Data
            ArrayList<String> contentList = new ArrayList<>(Arrays.asList("Pollution", "Corruption", "Poverty", "Crime", "Poor literacy",
                    "Child Labour", "Child Marriage", "Improper Org. Working", "Poor Infrastructure", "Dirty Roads"));
            ArrayList<String> uriList = new ArrayList<>(Arrays.asList("abc", "xyz", "123", "uvw", "mno", "987", "pqr", "ijk", "999", "ghi"));
            int noOfItems = 10;

            for (int i = 0; i < noOfItems; i++) {
                 storeData(contentList.get(i), uriList.get(i));
            }
            // loop starts for every item in network
            //      data = fetchData();
            //      storeData(data);
            // loop ends after the last item in network
        }

        private void storeData(String content, String uri) {
            ContentValues values = new ContentValues();
            values.put(UserPostContract.Post.COLUMN_NAME_POST_CONTENT, content);
            values.put(UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI, uri);

            long rowId;     // primary key id
            rowId = db.insert(UserPostContract.Post.TABLE_NAME, UserPostContract.Post.COLUMN_NAME_POST_IMAGE_URI, values);
        }
    }

    // note : this is a time consuming task/method and should therefore be done only in background.
    private ArrayList<Bitmap> getBitmaps(){

        int ids[] = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
                R.drawable.pic4, R.drawable.pic5, R.drawable.pic6,
                R.drawable.pic7, R.drawable.pic8, R.drawable.ash, R.drawable.pic10};

        Resources res = ctx.getResources();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();


        BitmapFactory factory = new BitmapFactory();
        for(int i = 0;i<ids.length; i++){
            // decode img, scale it down, load in m/m
            BitmapFactory.Options options = new BitmapFactory.Options();    // creating a new options ref for every loop count/image
            options.inJustDecodeBounds = true;
            factory.decodeResource(res, ids[i], options);

            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            options.inJustDecodeBounds = false;
            bitmaps.add(factory.decodeResource(res, ids[i], options));
        }
        return bitmaps;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            int halfHeight = height / 2;
            int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void storeInExtStorage(ArrayList<Bitmap> bitmaps) {
        // store data (bitmaps) in external storage device
        LOG_TAG = NetworkHelper.class.toString();

        boolean writable = isExternalStorageWritable();
        if(!writable){
            Log.e(LOG_TAG, "External storage isn't writable");
            Toast.makeText(ctx, "External storage isn't writable", Toast.LENGTH_SHORT).show();
            return;     // do nothing if external storage is not writable
        }

        File extPicDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String albumNameX = getAlbumName(0);
        File fileX = new File(extPicDir, albumNameX );

        // getParentFile() b'cos mkdirs() creates a dir taking the whole file as the directory name including the album name
        // if called via file.mkdirs()
        fileX.getParentFile().mkdirs();      // creates a dir if not already created and returns true otherwise a false
        boolean isDirCreated = fileX.getParentFile().isDirectory();      // check at the parent whether dir is created or not

        if(!isDirCreated){
            Log.e(LOG_TAG, "Directory not created");
            Toast.makeText(ctx, "Directory not created", Toast.LENGTH_SHORT).show();
        }

        // declaring identifiers we will need inside the loop
        String albumName;               byte[] data;
        File file;                      Bitmap bmp;
        OutputStream os;                ByteArrayOutputStream byteStream;

        for(int i = 0;i<bitmaps.size();i++) {
            // writing drawable byte to the ext. storage.
            try {
                //InputStream is = getResources().openRawResource(R.drawable.balloons);
                //String path = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                //"/storage/emulated/0/Android/data/com.ashutosh.abatev1/files/Pictures/image.jpg";

                albumName = getAlbumName(i);
                file = new File(extPicDir, albumName);
                bmp = bitmaps.get(i);

                os = new FileOutputStream(file);
                byteStream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
                data = byteStream.toByteArray();
                //is.read(data);
                os.write(data);
                //is.close();
                os.close();

            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.e(LOG_TAG, "Error writing " + fileX, e);
                Toast.makeText(ctx, " Error writing", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getAlbumName(Integer num) {
        // creates a new unique albumName for each image.
        String baseName = "image";
        String uniqueNo = num.toString();
        String extension = ".jpg";
        String albumName = baseName+uniqueNo+extension;
        return albumName;
    }

    public static boolean isExternalStorageWritable(){
        String currentState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(currentState)){
            return true;
        }
        return false;
    }
    public static boolean isExternalStorageReadable(){
        String currentState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(currentState)||Environment.MEDIA_MOUNTED_READ_ONLY.equals(currentState)){
            return true;
        }
        return false;
    }

}
