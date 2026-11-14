package com.zirnik.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File

private const val MAX_SIDE = 1600

private fun receiptsDir(ctx: Context) = File(ctx.filesDir, "receipts").apply { mkdirs() }

fun receiptFile(ctx: Context, name: String) = File(receiptsDir(ctx), name)

/** Kameranın yazacağı geçici dosya ve uri'si. */
fun cameraTarget(ctx: Context): Pair<File, Uri> {
    val f = File(File(ctx.cacheDir, "camera").apply { mkdirs() }, "fis.jpg")
    return f to FileProvider.getUriForFile(ctx, "${ctx.packageName}.files", f)
}

/**
 * Seçilen/çekilen fotoğrafı küçültüp döndürerek filesDir/receipts/<id>.jpg olarak saklar.
 * Başarısızsa null (örn. dosya okunamadı).
 */
fun saveReceipt(ctx: Context, src: Uri, id: Long): String? = try {
    val open = { ctx.contentResolver.openInputStream(src) ?: error("açılamadı") }
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    open().use { BitmapFactory.decodeStream(it, null, bounds) }
    var sample = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE * 2) sample *= 2
    val raw = open().use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample }) }
        ?: error("çözülemedi")
    val rotation = when (open().use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1) }) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    val scale = minOf(1f, MAX_SIDE.toFloat() / maxOf(raw.width, raw.height))
    val m = Matrix().apply { postRotate(rotation); postScale(scale, scale) }
    val out = Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, m, true)
    val name = "$id.jpg"
    receiptFile(ctx, name).outputStream().use { out.compress(Bitmap.CompressFormat.JPEG, 85, it) }
    name
} catch (e: Exception) {
    null
}

fun deleteReceipt(ctx: Context, name: String) {
    if (name.isNotEmpty()) receiptFile(ctx, name).delete()
}
