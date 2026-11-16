package com.zirnik.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Tutar kuruş (Long) olarak tutulur; ondalık hata olmaz. */
data class Entry(
    val id: Long, val kurus: Long, val income: Boolean, val note: String, val date: Long,
    val receipt: String = "", // filesDir/receipts altındaki fiş dosyası adı, yoksa boş
)

private val TR = Locale("tr", "TR")

/** "12,50" / "12.50" -> 1250. Geçersiz veya <=0 ise null. shortcut: binlik ayraç ("1.250,50") desteklenmez. */
fun parseKurus(s: String): Long? {
    val v = s.trim().replace(',', '.').toBigDecimalOrNull() ?: return null
    if (v.signum() <= 0) return null
    return v.movePointRight(2).setScale(0, RoundingMode.HALF_UP).toLong()
}

fun formatTl(kurus: Long): String {
    val a = Math.abs(kurus)
    val body = String.format(TR, "%,d", a / 100) + "," + String.format(TR, "%02d", a % 100)
    return (if (kurus < 0) "-" else "") + body + " ₺"
}

/** Yıl*12+ay: aylar arası gezinme için tek sayı. */
fun monthKey(millis: Long): Int {
    val c = Calendar.getInstance().apply { timeInMillis = millis }
    return c.get(Calendar.YEAR) * 12 + c.get(Calendar.MONTH)
}

fun monthTitle(key: Int): String {
    val c = Calendar.getInstance().apply { clear(); set(key / 12, key % 12, 1) }
    return SimpleDateFormat("LLLL yyyy", TR).format(c.time).replaceFirstChar { it.titlecase(TR) }
}

fun dayText(millis: Long): String = SimpleDateFormat("d MMM", TR).format(millis)

fun fullDayText(millis: Long): String = SimpleDateFormat("d MMM yyyy", TR).format(millis)

/** 1250 -> "12,50" (forma geri yazmak için; parseKurus ile gidiş-dönüş). */
fun plainKurus(kurus: Long): String = "${kurus / 100},${String.format(TR, "%02d", kurus % 100)}"

/** (gelir, gider) toplamı, kuruş. */
fun monthTotals(entries: List<Entry>, key: Int): Pair<Long, Long> {
    val m = entries.filter { monthKey(it.date) == key }
    return m.filter { it.income }.sumOf { it.kurus } to m.filterNot { it.income }.sumOf { it.kurus }
}

private const val PREFS = "zirnik"
private const val KEY = "entries"

fun loadEntries(ctx: Context): List<Entry> {
    val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null) ?: return emptyList()
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) {
            val o = arr.getJSONObject(it)
            Entry(o.getLong("id"), o.getLong("k"), o.getBoolean("i"), o.optString("n"), o.getLong("d"), o.optString("r"))
        }
    } catch (e: org.json.JSONException) {
        emptyList() // bozuk kayıt uygulamayı çökertmesin
    }
}

fun saveEntries(ctx: Context, list: List<Entry>) {
    val arr = JSONArray()
    list.forEach {
        arr.put(JSONObject().put("id", it.id).put("k", it.kurus).put("i", it.income).put("n", it.note).put("d", it.date).put("r", it.receipt))
    }
    ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
}
