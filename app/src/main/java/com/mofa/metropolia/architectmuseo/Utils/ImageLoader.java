package com.mofa.metropolia.architectmuseo.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by wenhao on 9.9.2017.
 */

public class ImageLoader {

    public static void loadImageMax(Context context, String url, ImageView imageView,
                                    int maxWidth, int maxHeight){

        int size = (int) Math.ceil(Math.sqrt(maxWidth * maxHeight));

        Picasso.with(context)
                .load(url)
                .transform(new BitmapTransform(maxWidth, maxHeight))
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .resize(size, size)
                .centerInside()
                .into(imageView);
    }

    public static void loadImage(Context context, String url, ImageView imageView){

        Picasso.with(context)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .into(imageView);
    }

}
