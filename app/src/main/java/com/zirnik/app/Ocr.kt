package com.zirnik.app

import android.content.Context
import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

/** Saklanmış fiş fotoğrafını telefonda (internetsiz) okur. Okunamazsa onResult(null); ana iş parçacığında döner. */
fun readReceipt(ctx: Context, fileName: String, onResult: (ReceiptInfo?) -> Unit) {
    val bmp = BitmapFactory.decodeFile(receiptFile(ctx, fileName).path) ?: return onResult(null)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    recognizer.process(InputImage.fromBitmap(bmp, 0))
        .addOnSuccessListener { onResult(parseReceipt(it.text)); recognizer.close() }
        .addOnFailureListener { onResult(null); recognizer.close() }
}
