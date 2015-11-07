package com.ashutosh.abatev1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        private ArrayList<String> contentList;
        private ArrayList<String> categoryList;
        private int noOfPostItems;

        public DBWriter(SQLiteDatabase writableDatabase) {
            this.db = writableDatabase;
        }

        // writes network output to the database
        public void executeProcess() {
            categoryList = new ArrayList<>();
            contentList = new ArrayList<>();

            getDataFromServer();        // retrieves the contentList and the categoryList from the server
//            int noOfItems = 7;          // should be atleast 10 for the start

            if(noOfPostItems < 1){
                // Dummy Data if there is no data in the server
                contentList = new ArrayList<>(Arrays.asList("Pollution", "Corruption", "Poverty", "Crime", "Poor literacy",
                        "Child Labour", "Child Marriage", "Improper Org. Working", "Poor Infrastructure", "Dirty Roads"));
                categoryList = new ArrayList<>(Arrays.asList("abc", "xyz", "123", "uvw", "mno", "987", "pqr", "ijk", "999", "ghi"));

                for (int i = 0; i < 10; i++) {
                    storeData(contentList.get(i), categoryList.get(i));
                }
            } else {
                for (int i = 0; i < noOfPostItems; i++) {
                    storeData(contentList.get(i), categoryList.get(i));
                }
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

        private void getDataFromServer() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonArrayString = "";

            try {
                // constructing the url for openweathermap query
                final String GET_POSTS_BASE_URL =
                        "http://abate-csmadhav.rhcloud.com/getposts?";
                final String QUERY_PARAM_REQNO = "reqno";

                Uri builtUri = Uri.parse(GET_POSTS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM_REQNO, "1")
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                jsonArrayString = buffer.toString();

            }catch (IOException e){
                e.printStackTrace();
            }

            try{
                parsePostJsonArray(jsonArrayString);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        private void parsePostJsonArray(String jsonArrayString) throws JSONException{
            JSONArray jsonArray = new JSONArray(jsonArrayString);
            //noOfPostItems = jsonArray.length();
            noOfPostItems = 5;
            for(int i = 0; i< noOfPostItems; i++){
                JSONObject object = jsonArray.getJSONObject(i);
                categoryList.add(object.getString("category"));
                contentList.add(object.getString("description"));
            }
        }
    }

    // note : this is a time consuming task/method and should therefore be done only in background.
    private ArrayList<Bitmap> getBitmaps(){

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        String imgs[] = { "IMG1446884399464.jpg", "IMG1446883898721.jpg", "IMG1446883093305.jpg", "IMG1446877588263.jpg",
                            "IMG1446876724687.jpg"};
        ArrayList<String> imagesList = new ArrayList<>(Arrays.asList(imgs));
        String baseUrl = "http://abate-csmadhav.rhcloud.com/images/";           // base url for fetching an image
        /*int ids[] = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
                R.drawable.pic4, R.drawable.pic5, R.drawable.pic6,
                R.drawable.pic7, R.drawable.pic8, R.drawable.ash, R.drawable.pic10};

        Resources res = ctx.getResources();

        BitmapFactory factory = new BitmapFactory();
        for(int i = 0;i<ids.length; i++){
            // decode img, scale it down, load in m/m
            BitmapFactory.Options options = new BitmapFactory.Options();    // creating a new options ref for every loop count/image
            options.inJustDecodeBounds = true;
            factory.decodeResource(res, ids[i], options);

            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            options.inJustDecodeBounds = false;
            bitmaps.add(factory.decodeResource(res, ids[i], options));
        }*/
        int w = SuperHelper.getImageWidth(ctx);
        int h = SuperHelper.getImageHeight(ctx);

        for( String image : imagesList){
            String url = baseUrl + image;
            try {
                bitmaps.add(Picasso.with(ctx).load(url).resize(w, h).get());     // this loads the image from the url and adds to bitmaps
            }catch (IOException e){
                e.printStackTrace();
            }
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

    public static Bitmap getResizedBitmap(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();    // creating a new options ref for every loop count/image
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = NetworkHelper.calculateInSampleSize(options, 100, 100);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

}
