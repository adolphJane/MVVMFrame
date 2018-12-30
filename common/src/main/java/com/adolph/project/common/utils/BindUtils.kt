package com.adolph.project.common.utils

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.adolph.project.basewidgets.image.GlideUtils

object BindUtils {
    @BindingAdapter(value =["url", "placeholderRes"], requireAll = false)
    fun setImageUri(imageView: ImageView, url: String, placeholderRes: Int) {
        if (!TextUtils.isEmpty(url)) {
            //使用Glide框架加载图片
//            GlideUtils.with(imageView.context)
//                .load(url)
//                .apply(RequestOptions().placeholder(placeholderRes))
//                .into(imageView)
        }
    }
}