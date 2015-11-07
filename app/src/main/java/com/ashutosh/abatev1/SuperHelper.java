package com.ashutosh.abatev1;

import android.content.Context;

/**
 * Created by Vostro-Daily on 11/7/2015.
 */
public class SuperHelper {
    private static int image_width;
    private static int image_height;

    public static int getImageWidth(Context ctx){
        image_width = (int)ctx.getResources().getDimension(R.dimen.image_width);
        return image_width;
    }
    public static int getImageHeight(Context ctx){
        image_height = (int)ctx.getResources().getDimension(R.dimen.image_height);
        return image_height;
    }
}
