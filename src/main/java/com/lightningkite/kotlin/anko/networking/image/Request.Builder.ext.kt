package com.lightningkite.kotlin.anko.networking.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.lightningkite.kotlin.anko.image.ImageUtils
import com.lightningkite.kotlin.anko.image.getBitmapFromUri
import com.lightningkite.kotlin.networking.lambda
import com.lightningkite.kotlin.stream.writeToFile
import okhttp3.Request
import java.io.File

fun Request.Builder.lambdaBitmap(options: BitmapFactory.Options = BitmapFactory.Options()) = lambda<Bitmap> {
    BitmapFactory.decodeStream(it.body()?.byteStream(), null, options)
}

fun Request.Builder.lambdaBitmap(minBytes: Long) = lambda<Bitmap> {
    val options = BitmapFactory.Options().apply {
        inSampleSize = ImageUtils.calculateInSampleSize(it.body()?.contentLength() ?: 0, minBytes)
    }
    BitmapFactory.decodeStream(it.body()?.byteStream(), null, options)
}

fun Request.Builder.lambdaBitmapExif(context: Context, minBytes: Long) = lambda<Bitmap> {
    val tempFile = File.createTempFile("image", "jpg", context.cacheDir)
    it.body()?.byteStream()?.writeToFile(tempFile)
    context.getBitmapFromUri(Uri.fromFile(tempFile), minBytes)!!
}

fun Request.Builder.lambdaBitmapExif(context: Context, maxWidth: Int = 2048, maxHeight: Int = 2048) = lambda<Bitmap> {
    val tempFile = File.createTempFile("image", "jpg", context.cacheDir)
    it.body()?.byteStream()?.writeToFile(tempFile)
    context.getBitmapFromUri(Uri.fromFile(tempFile), maxWidth, maxHeight)!!
}