package com.ashutosh.abatev1;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePostFragment extends Fragment {

    public static int ITEM_INDEX_IN_DRAWER = 1;
    private static int RESULT_LOAD_IMG = 1;
    private Context ctx;
    private ImageView  imageViewNewPost;
    private EditText editText;

    public CreatePostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState==null) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_create_post, container, false);
            ctx = getActivity();

            imageViewNewPost = (ImageView) rootView.findViewById(R.id.imageView_createPost);
            editText = (EditText) rootView.findViewById(R.id.edit_text_content);

            FloatingActionButton addImageButton = (FloatingActionButton) rootView.findViewById(R.id.button_addImage);
            addImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    Toast.makeText(ctx, "Select image from gallery", Toast.LENGTH_SHORT).show();
//                imageViewNewPost.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ash));
                    loadImageFromGallery();
                }
            });

            Button submitButton = (Button) rootView.findViewById(R.id.button_createPost_submit);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send post to the server for evaluation and addition to the database.
                    Toast.makeText(ctx, "Post Created and submitted for evaluation", Toast.LENGTH_SHORT).show();
                    Drawable drawable = imageViewNewPost.getDrawable();
                    String content = editText.getText().toString();
                    if(drawable!=null && content!=null) {
                        sendPostToServer(content, drawable);
                    } else {
                        Toast.makeText(ctx, "Please write something about the post and Select an image", Toast.LENGTH_LONG).show();
                    }
                }
            });
            return rootView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((NavDrawerActivity)activity).onSectionAttached(ITEM_INDEX_IN_DRAWER + 1);
    }

    public void loadImageFromGallery(){
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && null != data) {
                // Get the Image from data

                Uri path = data.getData();     // get the image uri
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = ctx.getContentResolver().query(path,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                imageViewNewPost.setImageBitmap(NetworkHelper.getResizedBitmap(imgDecodableString));

                // Set the Image in ImageView after decoding the String
//                imageViewNewPost.setImageBitmap(BitmapFactory
//                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(ctx, "You haven't picked Image",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ctx, "Something went wrong. Please Try Again", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void sendPostToServer(String content, Drawable drawable) {
        // launch an async task to send data to the server
        // api used - http://abate-csmadhav.rhcloud.com/postadd
        // parameters - id, email, description, category

    }

    public class SendPostTask extends AsyncTask<Void, Void, Void>{
        private String description;
        private Drawable drawable;

        SendPostTask(String content, Drawable drawable){
            description = content;
            this.drawable = drawable;
        }

        @Override
        protected Void doInBackground(Void... params) {
//            if(!((SignInActivity)ctx).isNetworkAvailable()){            // may produce an error some day
//                return null;        // do nothing
//            }
            SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String userId = pref.getString(NetworkHelper2.USER_ID, "");     // 2nd param is default value
//            HashMap<String, String> params = new Has

            return null;
        }
    }
}
