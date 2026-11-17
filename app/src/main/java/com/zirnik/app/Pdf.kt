package com.zirnik.app

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

/**
 * PDF'in metnini çıkarır. Şifreliyse ve parola yanlışsa InvalidPasswordException fırlatır.
 * Ağır iş: arka plan iş parçacığında çağır. shortcut: taranmış (metinsiz) PDF boş döner, OCR yedeği yok.
 */
fun pdfText(ctx: Context, uri: Uri, password: String?): String {
    PDFBoxResourceLoader.init(ctx.applicationContext)
    val stream = ctx.contentResolver.openInputStream(uri) ?: error("PDF açılamadı")
    return stream.use { s ->
        PDDocument.load(s, password ?: "").use { doc ->
            PDFTextStripper().apply { sortByPosition = true }.getText(doc)
        }
    }
}
