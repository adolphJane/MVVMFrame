package com.adolph.project.basewidgets.image;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 *
 */
public final class GlideUtils {

    public static void setCircleImage(Context context, String url, ImageView view) {
        RequestOptions requestOptions = new RequestOptions()
//                .placeholder(R.drawable.def_img_round_holder)
//                .error(R.drawable.def_img_round_error)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop().dontAnimate();
        Glide.with(context).load(url).apply(requestOptions).into(view);
    }

    public static void setImage(Context context, String url, ImageView view) {
        if (url.endsWith(".svg") || url.endsWith(".SVG")) {
            setSvgImage(context, url, view);
            return;
        }

        RequestOptions requestOptions = new RequestOptions()
//                .placeholder(R.drawable.def_img)
//                .error(R.drawable.def_img)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).dontAnimate();
        Glide.with(context).load(url).apply(requestOptions).into(view);
    }

    private static void setSvgImage(Context context, String url, ImageView view) {
        GlideApp.with(context)
                .as(PictureDrawable.class)
//                .error(R.drawable.def_img)
                .load(url).into(view);
    }
}
