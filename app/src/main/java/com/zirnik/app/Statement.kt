package com.zirnik.app

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Kredi kartı ekstresinden okunan satır.
 * income = iade (borcu azaltır). payment = kart borcu ödemesi: gider/gelir değil, zaten alışverişte
 * gider yazıldı; önizlemede işaretsiz gelir ve varsayılan olarak atlanır.
 */
data class StatementRow(val date: Long, val desc: String, val kurus: Long, val income: Boolean, val payment: Boolean = false)

private val TR = Locale("tr", "TR")

private val LINE_DATE = Regex("""^(\d{2})[./](\d{2})[./](\d{4})\s+(.*)$""")
// Yalnızca Türkçe biçim (1.234,56): saat ve tarih parçalarıyla karışmasın. Eksi başta ya da sonda olabilir.
private val AMOUNT = Regex("""(?<![\d.,])([-+]?)(\d{1,3}(?:\.\d{3})+|\d+),(\d{2})(-?)(?!\d)""")
private val LEADING_TIME = Regex("""^\d{1,2}:\d{2}(:\d{2})?\s*""")
private val CURRENCY = Regex("""\b(TL|TRY)\b|₺""")

/**
 * Satır başında tarih olan satırları okur; tutar olarak satırdaki İLK tutarı alır
 * (bonus/puan sütunları tutardan sonra gelir). Kart ekstresinde harcama artıdır; eksi ya da "İADE"
 * iadedir, eksi + "ÖDEME" kart borcu ödemesidir. shortcut: alt satıra taşan açıklamalar ve
 * bankaya özel düzenler yok; yanlış okunan banka için örnek satırla bu işlev ayarlanır.
 */
fun parseStatement(text: String, now: Long = System.currentTimeMillis()): List<StatementRow> =
    text.lines().mapNotNull { raw ->
        val m = LINE_DATE.matchEntire(raw.trim()) ?: return@mapNotNull null
        val date = statementDate(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt(), now)
            ?: return@mapNotNull null
        val rest = m.groupValues[4]
        val amount = AMOUNT.find(rest) ?: return@mapNotNull null
        val whole = amount.groupValues[2].filter { it.isDigit() }
        if (whole.length > 9) return@mapNotNull null
        val kurus = whole.toLong() * 100 + amount.groupValues[3].toInt()
        if (kurus <= 0) return@mapNotNull null
        val desc = rest.replace(AMOUNT, " ").replace(CURRENCY, " ").replace(LEADING_TIME, "")
            .replace(Regex("""\s+"""), " ").trim().take(60).let { if (it.isBlank()) "Ekstre satırı" else titleCaseTr(it) }
        val f = fold(desc)
        val negative = amount.groupValues[1] == "-" || amount.groupValues[4] == "-"
        val credit = negative || "IADE" in f
        val payment = credit && ("ODEME" in f || "TAHSILAT" in f)
        StatementRow(date, desc, kurus, income = credit && !payment, payment = payment)
    }

private fun statementDate(d: Int, mo: Int, y: Int, now: Long): Long? = try {
    val t = Calendar.getInstance().apply { isLenient = false; clear(); set(y, mo - 1, d, 12, 0) }.timeInMillis
    if (y >= 2000 && t <= now + 86_400_000L) t else null
} catch (e: IllegalArgumentException) {
    null
}

/** Aynı gün + aynı tutar + aynı yön = aynı hareket sayılır (not farklı olsa da, elle girilmiş olabilir). */
fun dupKey(date: Long, kurus: Long, income: Boolean): String =
    SimpleDateFormat("yyyyMMdd", TR).format(date) + ":" + kurus + ":" + income
