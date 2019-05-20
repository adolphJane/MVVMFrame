package com.ixiye.project.basewidgets.glide

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest


/**
 * @package com.ixiye.project.basewidgets.glide
 * @author Adolph
 * @date 2019/4/17 Wed
 * @description TODO
 */

class RotateTransformation : BitmapTransformation {

    private var rotateRotationAngle: Float = 0f

    constructor(rotateRotationAngle: Float) {
        this.rotateRotationAngle = rotateRotationAngle
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val matrix = Matrix()

        if (outHeight > outWidth) {
            matrix.postRotate(90f)
        } else {
            matrix.postRotate(-90f)
        }

        return Bitmap.createBitmap(
            toTransform,
            0,
            0,
            toTransform.width,
            toTransform.height,
            matrix,
            true
        )
    }
}