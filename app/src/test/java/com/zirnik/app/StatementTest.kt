package com.zirnik.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class StatementTest {
    private val now = Calendar.getInstance().apply { clear(); set(2026, 10, 17) }.timeInMillis
    private fun day(y: Int, m: Int, d: Int) = Calendar.getInstance().apply { clear(); set(y, m - 1, d, 12, 0) }.timeInMillis

    @Test fun cardRows() {
        val r = parseStatement(
            "KREDİ KARTI EKSTRESİ\n" +
                "12.11.2026 MİGROS İSTANBUL 420,50 4,20\n" +        // harcama, sondaki bonus tutar değil
                "13.11.2026 14:32 NETFLIX 1.149,99 TL\n" +
                "14/11/2026 TRENDYOL İADE -299,00\n" +             // iade = gelir
                "15.11.2026 KART ÖDEMESİ 5.000,00-\n" +            // borç ödemesi: ne gelir ne gider
                "Son ödeme 15.11.2026 tutar 100,00\n" +             // satır başında tarih yok
                "16.11.2026 AÇIKLAMA TUTARSIZ\n", now)
        assertEquals(4, r.size)
        assertEquals(StatementRow(day(2026, 11, 12), "Migros İstanbul", 42050, false), r[0])
        assertEquals(114999L, r[1].kurus); assertFalse(r[1].income); assertEquals("Netflix", r[1].desc)
        assertTrue(r[2].income); assertFalse(r[2].payment); assertEquals(29900L, r[2].kurus)
        assertTrue(r[3].payment); assertFalse(r[3].income) // sondaki eksi
    }

    @Test fun badDates() {
        assertTrue(parseStatement("31.02.2026 X 10,00", now).isEmpty())
        assertTrue(parseStatement("12.11.2030 X 10,00", now).isEmpty())
    }

    @Test fun duplicateKey() {
        assertEquals(dupKey(day(2026, 11, 12), 42050, false), dupKey(day(2026, 11, 12) + 3_600_000, 42050, false))
        assertFalse(dupKey(day(2026, 11, 12), 42050, false) == dupKey(day(2026, 11, 13), 42050, false))
    }
}
