package com.ashutosh.abatev1;

import android.provider.BaseColumns;

/**
 * Created by Vostro-Daily on 10/17/2015.
 *
 * Description : A contract(agreement) between the model and the view on how data is to be stored and retrieved from the database.
 * In simple set of all Tables and Columns in each table in the database.
 * Here, each inner class represents a new table in the database
 **/
public class UserPostContract {

    public static final class Post implements BaseColumns {
        // unique id for the table is provided by BaseColumns interface '_ID', which is required for use with Content Provider
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_POST_CONTENT = "post_content";
        public static final String COLUMN_NAME_POST_IMAGE_URI = "post_image_uri";
    }

    public static final class User implements BaseColumns {
        // defines the columns for the 'user' table
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_USER_EMAIL = "user_email";
        public static final String COLUMN_NAME_USER_PIC_HTTP_URL = "user_pic_url";
    }
}
