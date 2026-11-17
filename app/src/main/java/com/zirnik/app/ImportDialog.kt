package com.zirnik.app

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import kotlin.concurrent.thread

private enum class Stage { Idle, Reading, Password, Error, Empty, Ready }

private data class ImportRow(val row: StatementRow, val checked: Boolean, val income: Boolean, val dup: Boolean)

/** Kart ekstresi PDF'ini seç, satırları önizle, işaretlediklerini kayıt olarak ekle. */
@Composable
fun ImportDialog(existing: List<Entry>, onDismiss: () -> Unit, onImport: (List<Entry>) -> Unit) {
    val ctx = LocalContext.current
    val flow = LocalFlow.current
    var uri by remember { mutableStateOf<Uri?>(null) }
    var password by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(Stage.Idle) }
    var rows by remember { mutableStateOf<List<ImportRow>>(emptyList()) }
    val have = remember(existing) { existing.map { dupKey(it.date, it.kurus, it.income) }.toSet() }

    fun load(u: Uri, pw: String) {
        stage = Stage.Reading
        val main = ContextCompat.getMainExecutor(ctx)
        thread { // PDF çözümü ağır: ana iş parçacığını tutma
            val result = runCatching { pdfText(ctx, u, pw) }
            main.execute {
                val e = result.exceptionOrNull()
                stage = when {
                    e is InvalidPasswordException -> Stage.Password
                    e != null -> Stage.Error
                    else -> {
                        rows = parseStatement(result.getOrThrow()).map {
                            val dup = dupKey(it.date, it.kurus, it.income) in have
                            ImportRow(it, checked = !dup && !it.payment, income = it.income, dup = dup)
                        }
                        if (rows.isEmpty()) Stage.Empty else Stage.Ready
                    }
                }
            }
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { u ->
        if (u != null) { uri = u; password = ""; load(u, "") }
    }
    val pick = { picker.launch(arrayOf("application/pdf")) }
    val selected = rows.count { it.checked }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ekstreden aktar") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (stage) {
                    Stage.Idle -> {
                        Text("Kredi kartı ekstresi PDF'ini seç. Okuma telefonda yapılır, dosya hiçbir yere gönderilmez.")
                        Button(pick, Modifier.heightIn(min = 48.dp)) { Text("PDF seç") }
                    }
                    Stage.Reading -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp)); Text("Okunuyor…")
                    }
                    Stage.Password -> {
                        Text("PDF şifreli. Bankanın verdiği parolayı gir.")
                        OutlinedTextField(password, { password = it }, label = { Text("Parola") }, singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            supportingText = { if (password.isNotEmpty()) Text("Parola yanlışsa tekrar dene") })
                        Button({ uri?.let { load(it, password) } }, Modifier.heightIn(min = 48.dp), enabled = password.isNotEmpty()) { Text("Aç") }
                    }
                    Stage.Error -> {
                        Text("PDF okunamadı.")
                        Button(pick, Modifier.heightIn(min = 48.dp)) { Text("Başka PDF seç") }
                    }
                    Stage.Empty -> {
                        Text("Ekstre satırı bulunamadı. PDF taranmış ya da biçimi farklı olabilir.")
                        Button(pick, Modifier.heightIn(min = 48.dp)) { Text("Başka PDF seç") }
                    }
                    Stage.Ready -> {
                        Text("${rows.size} satır bulundu. İşaretlenenler eklenir; Gider/İade yazısına dokunarak yönü değiştir.",
                            fontSize = 14.sp)
                        LazyColumn(Modifier.heightIn(max = 360.dp)) {
                            itemsIndexed(rows) { i, r ->
                                val color = if (r.income) flow.income else flow.expense
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(r.checked, { c -> rows = rows.toMutableList().also { it[i] = r.copy(checked = c) } })
                                    Column(Modifier.weight(1f)) {
                                        Text(r.row.desc, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(
                                            dayText(r.row.date) + when {
                                                r.dup -> " · zaten var"
                                                r.row.payment -> " · kart ödemesi, atlanır"
                                                else -> ""
                                            },
                                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text((if (r.income) "+" else "−") + formatTl(r.row.kurus), fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold, color = color)
                                        TextButton({ rows = rows.toMutableList().also { it[i] = r.copy(income = !r.income) } },
                                            Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(horizontal = 4.dp)) {
                                            Text(if (r.income) "İade" else "Gider", fontSize = 12.sp)
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (stage == Stage.Ready) TextButton({
                val base = System.currentTimeMillis()
                onImport(rows.filter { it.checked }.mapIndexed { i, r ->
                    Entry(base + i, r.row.kurus, r.income, r.row.desc, r.row.date)
                })
            }, enabled = selected > 0) { Text("Ekle ($selected)") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Vazgeç") } }
    )
}
