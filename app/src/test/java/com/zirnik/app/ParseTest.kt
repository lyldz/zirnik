package com.zirnik.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ParseTest {
    private val now = Calendar.getInstance().apply { clear(); set(2026, 10, 16) }.timeInMillis
    private fun day(y: Int, m: Int, d: Int) = Calendar.getInstance().apply { clear(); set(y, m - 1, d, 12, 0) }.timeInMillis

    @Test fun marketReceipt() {
        val r = parseReceipt(
            "MİGROS TİCARET A.Ş.\nATAŞEHİR ŞUBESİ\nTARİH: 12.11.2026 SAAT 14:32\nSÜT 1L *45,00\n" +
                "ARA TOPLAM 45,00\nTOPKDV 3,00\nTOPLAM *1.245,50\nNAKİT 1.300,00", now)
        assertEquals(124550L, r.kurus) // ara toplam, KDV ve nakit değil
        assertEquals(day(2026, 11, 12), r.date)
        assertTrue(r.store!!.startsWith("Migros"))
    }

    @Test fun amountOnNextLine() {
        assertEquals(8990L, parseReceipt("BİM\nTOPLAM\n*89,90", now).kurus)
    }

    @Test fun twoDigitYearAndBadDates() {
        assertEquals(day(2026, 3, 5), parseReceipt("05/03/26", now).date)
        assertNull(parseReceipt("31.02.2026", now).date)
        assertNull(parseReceipt("12.11.2030", now).date) // gelecek
    }

    @Test fun nothingFound() {
        val r = parseReceipt("", now)
        assertNull(r.kurus); assertNull(r.date); assertNull(r.store)
    }
}
