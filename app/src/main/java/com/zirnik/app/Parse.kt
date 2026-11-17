package com.zirnik.app

import java.util.Calendar
import java.util.Locale

/** Fiş metninden ayıklanan taslak; bulunamayan alan null. */
data class ReceiptInfo(val kurus: Long?, val date: Long?, val store: String?)

private val TR = Locale("tr", "TR")

/** Anahtar kelime eşleşmesi için büyük harf + Türkçe harfleri sadeleştir. */
internal fun fold(s: String) = s.uppercase(TR)
    .replace('İ', 'I').replace('Ş', 'S').replace('Ğ', 'G').replace('Ü', 'U').replace('Ö', 'O').replace('Ç', 'C')

private val AMOUNT = Regex("""(\d{1,3}(?:[.,]\d{3})+|\d+)[.,](\d{2})(?!\d)""")
private val DATE = Regex("""(?<!\d)(\d{1,2})[./-](\d{1,2})[./-](\d{4}|\d{2})(?!\d)""")

private fun amountsIn(line: String): List<Long> = AMOUNT.findAll(line).mapNotNull { m ->
    val whole = m.groupValues[1].filter { it.isDigit() }
    if (whole.length > 9) null else whole.toLong() * 100 + m.groupValues[2].toInt()
}.toList()

private fun findTotal(lines: List<String>): Long? {
    val f = lines.map(::fold)
    // Tutar bazen anahtar kelimeden sonraki satıra kayar.
    fun amountAt(i: Int) = amountsIn(lines[i]).lastOrNull() ?: lines.getOrNull(i + 1)?.let { amountsIn(it).lastOrNull() }
    val tiers = listOf<(String) -> Boolean>(
        { "GENEL TOPLAM" in it || "ODENECEK" in it },
        { "TOPLAM" in it && "ARA TOPLAM" !in it && "KDV" !in it },
        { "TUTAR" in it && "KDV" !in it },
    )
    for (tier in tiers) {
        // Toplam fişin altında olur: son eşleşen satırı al.
        val i = f.indices.lastOrNull { tier(f[it]) && amountAt(it) != null }
        if (i != null) return amountAt(i)
    }
    // shortcut: anahtar kelime yoksa en büyük tutar; para üstü varsa yanlış olabilir, kullanıcı formda düzeltir.
    return lines.flatMap(::amountsIn).maxOrNull()
}

private fun findDate(text: String, now: Long): Long? = DATE.findAll(text).firstNotNullOfOrNull { m ->
    val d = m.groupValues[1].toInt()
    val mo = m.groupValues[2].toInt()
    var y = m.groupValues[3].toInt()
    if (y < 100) y += 2000
    try {
        val t = Calendar.getInstance().apply {
            isLenient = false
            clear(); set(y, mo - 1, d, 12, 0)
        }.timeInMillis
        if (y >= 2000 && t <= now + 86_400_000L) t else null // gelecekteki/uydurma tarihi alma
    } catch (e: IllegalArgumentException) {
        null // 31.02 gibi geçersiz tarih
    }
}

private val NOT_STORE = listOf("FIS", "TARIH", "TEL", "VKN", "VERGI", "ADRES", "SAAT", "NO:")

private fun findStore(lines: List<String>): String? = lines.take(6).firstOrNull { l ->
    val body = l.filterNot { it.isWhitespace() }
    body.count { it.isLetter() } >= 3 && body.count { it.isLetter() } >= body.length * 0.6 &&
        NOT_STORE.none { it in fold(l) }
}?.let(::titleCaseTr)?.take(40)

/** "MİGROS TİCARET" -> "Migros Ticaret" */
internal fun titleCaseTr(s: String): String = s.replace('İ', 'i').replace('I', 'i') // I -> ı olmasın: NETFLIX -> Netflix
    .lowercase(TR).split(' ').filter { it.isNotBlank() }
    .joinToString(" ") { w -> w.replaceFirstChar { it.titlecase(TR) } }

fun parseReceipt(text: String, now: Long = System.currentTimeMillis()): ReceiptInfo {
    val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
    return ReceiptInfo(findTotal(lines), findDate(text, now), findStore(lines))
}
