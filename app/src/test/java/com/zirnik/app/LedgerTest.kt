package com.zirnik.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class LedgerTest {
    @Test fun parse() {
        assertEquals(1250L, parseKurus("12,50"))
        assertEquals(1250L, parseKurus(" 12.5 "))
        assertEquals(1L, parseKurus("0,005")) // yarım kuruş yukarı yuvarlanır
        assertNull(parseKurus("0"))
        assertNull(parseKurus("-3"))
        assertNull(parseKurus("abc"))
    }

    @Test fun format() {
        assertEquals("1.234,05 ₺", formatTl(123405))
        assertEquals("-0,50 ₺", formatTl(-50))
    }

    @Test fun totals() {
        fun at(y: Int, m: Int) = Calendar.getInstance().apply { clear(); set(y, m, 15) }.timeInMillis
        val list = listOf(
            Entry(1, 1000, true, "", at(2026, 10)),
            Entry(2, 300, false, "", at(2026, 10)),
            Entry(3, 999, false, "", at(2026, 9)),
        )
        assertEquals(1000L to 300L, monthTotals(list, monthKey(at(2026, 10))))
    }
}
