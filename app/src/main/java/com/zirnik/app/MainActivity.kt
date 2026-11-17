package com.zirnik.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val Cream = Color(0xFFF6EFE8)
private val IncomeOnDark = Color(0xFF6FCB8E)
private val ExpenseOnDark = Color(0xFFFF8F70)

/** Gösterimde eksi işareti U+2212; formatTl ASCII "-" üretir. */
private fun signed(kurus: Long) = formatTl(kurus).replace('-', '−')

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZirnikTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { ZirnikApp() }
            }
        }
    }
}

@Composable
fun ZirnikApp() {
    val ctx = LocalContext.current
    val flow = LocalFlow.current
    val dark = isSystemInDarkTheme()
    var entries by remember { mutableStateOf(loadEntries(ctx)) }
    var month by rememberSaveable { mutableIntStateOf(monthKey(System.currentTimeMillis())) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var importing by rememberSaveable { mutableStateOf(false) }
    var viewing by rememberSaveable { mutableStateOf<String?>(null) }
    var deleting by remember { mutableStateOf<Entry?>(null) }

    fun update(list: List<Entry>) { entries = list; saveEntries(ctx, list) }

    val (income, expense) = monthTotals(entries, month)
    val net = income - expense
    val shown = entries.filter { monthKey(it.date) == month }.sortedByDescending { it.date }
    fun label(e: Entry) = e.note.ifBlank { if (e.income) "Gelir" else "Gider" }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { adding = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) { Icon(Icons.Filled.Add, "Kayıt ekle") }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton({ month-- }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Önceki ay") }
                Text(monthTitle(month), Modifier.weight(1f).semantics { heading() }, fontSize = 20.sp,
                    fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                IconButton({ month++ }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Sonraki ay") }
                IconButton({ importing = true }) { Icon(Icons.Filled.UploadFile, "Ekstreden aktar") }
            }

            Card(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (dark) Color(0xFF5A1A2E) else Color(0xFF4A1224), contentColor = Cream),
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Bu ay kalan", fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.alpha(0.8f))
                    Text(signed(net), fontSize = 40.sp, lineHeight = 44.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp,
                        color = if (net < 0) ExpenseOnDark else Cream)
                    if (net < 0) Text("Gider geliri geçti. Hesap defteri bunu not etti.", fontSize = 14.sp)
                    else if (net == 0L && shown.isNotEmpty()) Text("Başabaş.", fontSize = 14.sp)
                    HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Cream.copy(alpha = 0.2f))
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text("Gelir", fontSize = 12.sp)
                            Text("+" + formatTl(income), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = IncomeOnDark)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Gider", fontSize = 12.sp)
                            Text("−" + formatTl(expense), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = ExpenseOnDark)
                        }
                    }
                }
            }

            if (shown.isEmpty()) {
                Column(Modifier.fillMaxWidth().padding(top = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Savings, null, Modifier.size(48.dp).alpha(0.4f), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Bu ay sessiz.", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                    Text("Henüz kayıt yok, kasa da kıpırdamamış.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton({ adding = true }, Modifier.heightIn(min = 48.dp)) { Text("İlk kaydı ekle") }
                }
            } else {
                Text("Bu ayın kayıtları", Modifier.padding(top = 24.dp, bottom = 4.dp), fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
                items(shown, key = { it.id }) { e ->
                    val color = if (e.income) flow.income else flow.expense
                    val amountText = (if (e.income) "+" else "−") + formatTl(e.kurus)
                    Row(Modifier.fillMaxWidth().heightIn(min = 64.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(label(e), fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(dayText(e.date), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (e.receipt.isNotEmpty()) {
                            IconButton({ viewing = e.receipt }) { Icon(Icons.Filled.Receipt, "Fişi göster: ${label(e)}") }
                        }
                        Text(amountText, Modifier.padding(start = 8.dp).semantics {
                            contentDescription = "${if (e.income) "gelir" else "gider"} ${formatTl(e.kurus)}"
                        }, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = color)
                        IconButton({ deleting = e }) { Icon(Icons.Filled.Delete, "Sil: ${label(e)}, ${formatTl(e.kurus)}") }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }

    if (importing) ImportDialog(entries, onDismiss = { importing = false }) { list ->
        update(entries + list)
        list.maxOfOrNull { it.date }?.let { month = monthKey(it) } // eklenenler hemen görünsün
        importing = false
        Toast.makeText(ctx, "${list.size} kayıt eklendi", Toast.LENGTH_SHORT).show()
    }

    if (adding) AddDialog(
        onDismiss = { adding = false },
        onAdd = { id, kurus, isIncome, note, receipt, receiptDate ->
            val date = if (receiptDate > 0) receiptDate else System.currentTimeMillis() // fişten okunan tarih varsa o
            update(entries + Entry(id, kurus, isIncome, note.trim(), date, receipt))
            month = monthKey(date) // yeni kayıt hemen görünsün
            adding = false
        }
    )

    viewing?.let { name ->
        Dialog({ viewing = null }) {
            Surface(shape = RoundedCornerShape(28.dp)) {
                val bmp = remember(name) { BitmapFactory.decodeFile(receiptFile(ctx, name).path)?.asImageBitmap() }
                if (bmp != null) Image(bmp, "Fiş fotoğrafı", Modifier.fillMaxWidth().padding(8.dp), contentScale = ContentScale.Fit)
                else Text("Fiş açılamadı.", Modifier.padding(24.dp))
            }
        }
    }

    deleting?.let { e ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Silinsin mi?") },
            text = {
                Column {
                    Text("${label(e)} · ${formatTl(e.kurus)}")
                    Text("Geri alınmaz.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                TextButton({ deleteReceipt(ctx, e.receipt); update(entries - e); deleting = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Sil") }
            },
            dismissButton = { TextButton({ deleting = null }) { Text("Vazgeç") } }
        )
    }
}

@Composable
fun AddDialog(onDismiss: () -> Unit, onAdd: (Long, Long, Boolean, String, String, Long) -> Unit) {
    val ctx = LocalContext.current
    val flow = LocalFlow.current
    // rememberSaveable: ekran dönse ya da kamera uygulaması açılırken etkinlik yeniden kurulsa da form kaybolmasın.
    val id = rememberSaveable { System.currentTimeMillis() }
    var income by rememberSaveable { mutableStateOf(false) }
    var amount by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var receipt by rememberSaveable { mutableStateOf("") }
    var receiptDate by rememberSaveable { mutableLongStateOf(0L) } // 0 = fişten okunmadı, kayıt bugüne yazılır
    var reading by remember { mutableStateOf(false) }
    val kurus = parseKurus(amount)
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }

    // shortcut: fotoğraf küçültme ana iş parçacığında (1600 px'e kadar, birkaç yüz ms); yavaş telefonda arka plana al.
    fun store(uri: android.net.Uri?) {
        if (uri == null) return
        val name = saveReceipt(ctx, uri, id)
        if (name == null) { Toast.makeText(ctx, "Fotoğraf kaydedilemedi", Toast.LENGTH_SHORT).show(); return }
        receipt = name
        reading = true
        readReceipt(ctx, name) { info ->
            reading = false
            if (info == null || (info.kurus == null && info.date == null && info.store == null)) {
                Toast.makeText(ctx, "Fiş okunamadı, elle gir", Toast.LENGTH_SHORT).show()
                return@readReceipt
            }
            // Kullanıcının yazdığını ezme; yalnızca boş alanları doldur.
            if (amount.isEmpty()) info.kurus?.let { amount = plainKurus(it) }
            if (note.isEmpty()) info.store?.let { note = it }
            info.date?.let { receiptDate = it }
            Toast.makeText(ctx, "Fişten okundu, kontrol et", Toast.LENGTH_SHORT).show()
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok) store(cameraTarget(ctx).second)
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { store(it) }

    fun cancel() { deleteReceipt(ctx, receipt); onDismiss() }

    AlertDialog(
        onDismissRequest = ::cancel,
        title = { Text("Yeni kayıt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for ((isIncome, text) in listOf(false to "Gider", true to "Gelir")) {
                        val c = if (isIncome) flow.income else flow.expense
                        FilterChip(
                            selected = income == isIncome, onClick = { income = isIncome },
                            label = { Text(text) }, modifier = Modifier.heightIn(min = 48.dp),
                            leadingIcon = if (income == isIncome) ({ Icon(Icons.Filled.Check, null, Modifier.size(18.dp)) }) else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = c.copy(alpha = 0.12f), selectedLabelColor = c, selectedLeadingIconColor = c),
                        )
                    }
                }
                OutlinedTextField(
                    amount, { amount = it }, Modifier.focusRequester(focus), label = { Text("Tutar (₺)") }, singleLine = true,
                    isError = amount.isNotEmpty() && kurus == null,
                    supportingText = { if (amount.isNotEmpty() && kurus == null) Text("Tutarı rakamla yaz, örn. 12,50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(note, { note = it }, label = { Text("Not (isteğe bağlı)") }, singleLine = true)
                if (receipt.isEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton({ camera.launch(cameraTarget(ctx).second) }, Modifier.heightIn(min = 48.dp)) {
                            Icon(Icons.Filled.CameraAlt, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Fiş çek")
                        }
                        OutlinedButton({ gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            Modifier.heightIn(min = 48.dp)) {
                            Icon(Icons.Filled.PhotoLibrary, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Galeri")
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Receipt, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp))
                        Text("Fiş eklendi", Modifier.weight(1f))
                        TextButton({ deleteReceipt(ctx, receipt); receipt = ""; receiptDate = 0L }) { Text("Kaldır") }
                    }
                    if (reading) Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp)); Text("Fiş okunuyor…", fontSize = 14.sp)
                    } else if (receiptDate > 0) Text("Fiş tarihi: ${fullDayText(receiptDate)}", fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        confirmButton = { TextButton({ kurus?.let { onAdd(id, it, income, note, receipt, receiptDate) } }, enabled = kurus != null) { Text("Ekle") } },
        dismissButton = { TextButton(::cancel) { Text("Vazgeç") } }
    )
}
