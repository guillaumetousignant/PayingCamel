package com.guillaumetousignant.payingcamel.ui.helpers

import android.graphics.Shader
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import kotlin.math.min
import java.security.MessageDigest

class CircleTransform : BitmapTransformation() {

    private val idBytes = javaClass.name.toByteArray()

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return circleCrop(pool, toTransform)
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {

        return source?.let{
            val size = min(it.width, it.height)
            val x = (it.width - size) / 2
            val y = (it.height - size) / 2

            // CHECK this could be acquired from the pool too
            val squared = Bitmap.createBitmap(it, x, y, size, size)

            val result: Bitmap = pool.get(size, size, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(result)
            val paint = Paint()
            paint.shader = BitmapShader(
                squared,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )

            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)

            result
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
    }
}