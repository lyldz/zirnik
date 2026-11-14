# Zırnık — Tasarım Belgesi

Hava: sakin, ağır, hafif esprili. Vişne çürüğü + krem + bakır. Gösteriş yok, boşluk ve yazı ağırlığı işi yapar.

**Palet değişikliği:** Logodaki beş renk (#6B1E34, #4A1224, #C9A24B, #F6EFE8, #1A1014) AYNEN korundu. Altın #C9A24B krem üzerinde 2.2:1 verdiği için metin/ikon rengi olarak kullanılmaz; yalnızca süs (ince çizgi, FAB kenarı). Metin gereken yerde koyu bakır #7A5A10 (açık) / #D9B65F (koyu) kullanılır.

## 1. Renk rolleri

Kontrast oranları hesaplanmıştır (WCAG). Metin için AA = 4.5, büyük metin/ikon/kenar = 3.

### Açık tema

| Rol | Hex | Not |
|---|---|---|
| primary | #6B1E34 | krem üstünde 9.9 |
| onPrimary | #FFFFFF | primary üstünde 11.3 |
| primaryContainer | #EBCDD3 | |
| onPrimaryContainer | #3A0C1B | 11.4 |
| secondary | #7A5A10 | koyu bakır, krem üstünde 5.6 |
| onSecondary | #FFFFFF | |
| secondaryContainer | #F1DFAE | |
| onSecondaryContainer | #3B2A00 | 10.5 |
| tertiary | #2F5D4A | sakin çam yeşili, 6.6 |
| onTertiary | #FFFFFF | |
| background | #F6EFE8 | krem |
| onBackground | #241519 | 15.4 |
| surface | #FFFBF7 | kart/diyalog zemini |
| onSurface | #241519 | |
| surfaceVariant | #EADFD8 | |
| onSurfaceVariant | #5C4A4F | surfaceVariant üstünde 6.3 |
| outline | #85727A | 3.9 (kenar için yeterli) |
| outlineVariant | #DCCFC8 | ayraç çizgisi |
| error | #B3261E | 5.7 |
| onError | #FFFFFF | |

### Koyu tema

| Rol | Hex | Not |
|---|---|---|
| primary | #E8A8B8 | zeminde 9.5 |
| onPrimary | #3A0C1B | 8.6 |
| primaryContainer | #5A1A2E | |
| onPrimaryContainer | #FFD9E0 | 10.0 |
| secondary | #D9B65F | bakır, 9.6 |
| onSecondary | #2E2000 | 8.2 |
| secondaryContainer | #4A3A10 | |
| onSecondaryContainer | #F1DFAE | |
| tertiary | #8FC9AE | |
| onTertiary | #0E2E20 | |
| background | #1A1014 | |
| onBackground | #EFE0E3 | 14.6 |
| surface | #261A1F | kart/diyalog |
| onSurface | #EFE0E3 | 13.2 |
| surfaceVariant | #3A2A30 | |
| onSurfaceVariant | #CDBABF | 7.3 |
| outline | #9A858C | 5.4 |
| outlineVariant | #4A383F | |
| error | #FFB4A8 | 10.9 |
| onError | #5C1008 | |

### Gelir / gider (ayrı, rol dışı sabitler)

| | Açık tema | Koyu tema | Kontrast (zemin / kart) |
|---|---|---|---|
| gelir | #1E6B3A | #6FCB8E | 5.7 / 6.3 — 9.4 / 8.5 |
| gider | #A93A1A | #FF8F70 | 5.6 / 6.2 — 8.4 / 7.5 |

Ayrım, vişneyle karışmasın diye üç katmanlı:
1. **Ton:** gelir yeşil; gider tuğla/kiremit (turuncuya kayan kırmızı, #A93A1A). Vişne morumsu-kırmızıdır, tuğla sarıya yakındır; yan yana durunca ayrışır. Gider rengi primary değildir, marka rengi gider anlamına gelmez.
2. **İşaret:** tutarın başında her zaman `+` / `−` (mevcut kodda `-` düz tire; `−` U+2212 kullan).
3. **Yazı etiketi:** satırda not boşsa "Gelir"/"Gider" yazar; ekleme diyaloğunda seçici metinli. Renk tek başına bilgi taşımaz (renk körlüğü).

Kalan kartı iki temada da koyu vişne (#4A1224 açık, #5A1A2E koyu; yazı #F6EFE8). Kartın içindeki gelir/gider/negatif rengi her iki temada **koyu tema varyantı** (#6FCB8E, #FF8F70) olur; koyu vişne üstünde ≥ 7:1. Kalan negatifse tutar #FF8F70, pozitifse krem (yeşile boyama; sakin kalsın).

Not: Kodda `Green`/`Red` sabitleri bu iki çifte dönüşür (temaya göre seçilir, örn. `CompositionLocal` veya tema nesnesi).

## 2. Yazı

Yalnızca sistem fontu (`FontFamily.Default`). Rakamlarda `fontFeatureSettings = "tnum"` (eşit genişlik, liste hizalanır).

| Stil | sp | Ağırlık | Satır | Kullanım |
|---|---|---|---|---|
| Tutar-büyük | 40 | Bold | 44 | kalan tutarı |
| Tutar-orta | 17 | SemiBold | 24 | liste satırı tutarı |
| Başlık (ay) | 20 | Medium | 28 | ay adı, diyalog başlığı (22) |
| Gövde | 16 | Normal | 24 | not, alan metni |
| Etiket | 14 | Medium | 20 | "Bu ay kalan", Gelir/Gider özet |
| Küçük | 12 | Normal | 16 | tarih, ipucu |

Büyük tutar harf aralığı −0.5sp. Etiketler büyük harf yapılmaz. Kullanıcı yazı boyutunu büyütürse düzen taşmasın: sp kullan, satırlarda `maxLines = 1` + `Ellipsis` yalnızca notta; tutar asla kısaltılmaz.

## 3. Yerleşim

Boşluk ölçeği: 4 / 8 / 12 / 16 / 24 dp. Ekran kenar boşluğu 16. Köşe: kart 20, diyalog 28 (M3 varsayılanı), alan 12, chip 8, FAB 16 (yuvarlak kare), ayraç 1dp outlineVariant.

### Ana ekran

```
┌──────────────────────────────┐
│  ‹      Kasım 2026       ›   │  ay çubuğu, 48dp ikon
│                              │
│ ┌──────────────────────────┐ │  kart: radius 20, iç boşluk 20
│ │ Bu ay kalan              │ │  etiket 14, krem %80
│ │ 12.480,50 ₺              │ │  tutar-büyük 40
│ │ ──────────────────────── │ │
│ │ Gelir          Gider     │ │  etiket 12
│ │ +20.000 ₺     −7.519 ₺   │ │  17 SemiBold, yeşil/kiremit
│ └──────────────────────────┘ │
│                              │  24dp
│ Bu ayın kayıtları            │  etiket 14, onSurfaceVariant
│ ──────────────────────────── │
│ Market               −420 ₺ 🗑│  satır min 64dp
│ 12 Kas                       │
│ ──────────────────────────── │
│ Maaş               +20.000 ₺🗑│
│ 1 Kas                        │
│                       [ + ]  │  FAB sağ alt, 56dp, 16 kenar
└──────────────────────────────┘
```

Kalan kartı Gelir ve Gider yan yana, sol/sağ hizalı; sol Gelir, sağ Gider. Liste altı boşluğu 88dp (FAB için) kalır. FAB: primaryContainer değil **primary** zemin, onPrimary ikon, genişletilmiş (ikon + "Kayıt ekle") listenin başında, kaydırınca yalnızca ikon (isteğe bağlı; kısıtlıysa yalnızca ikon, yine de contentDescription şart).

### Ekleme diyaloğu

```
┌────────────────────────────┐
│ Yeni kayıt                 │  başlık 22
│                            │
│ [ Gider ] [ Gelir ]        │  tek seçimli, 48dp yükseklik
│                            │
│ ┌────────────────────────┐ │
│ │ Tutar (₺)              │ │  Decimal klavye, otomatik odak
│ └────────────────────────┘ │
│ ┌────────────────────────┐ │
│ │ Not (isteğe bağlı)     │ │
│ └────────────────────────┘ │
│                            │
│          Vazgeç     Ekle   │  Ekle: tutar geçersizse pasif
└────────────────────────────┘
```
Alanlar arası 12dp, diyalog iç boşluğu 24. Seçili chip: Gider seçiliyken kiremit, Gelir seçiliyken yeşil çerçeve + dolgu %12 + onay işareti (renk tek ipucu olmasın). Geçersiz tutarda alan altında "Tutarı rakamla yaz, örn. 12,50" (error rengi).

### Silme onayı

```
┌────────────────────────────┐
│ Silinsin mi?               │
│ Market · 420,00 ₺          │  not + tutar
│ Geri alınmaz.              │  küçük, onSurfaceVariant
│                            │
│          Vazgeç     Sil    │  Sil: error rengi metin
└────────────────────────────┘
```
Vazgeç solda, Sil sağda (varsayılan konum). Odak başlangıçta Vazgeç'te olsun.

### Boş durum

```
│  (kart: 0,00 ₺ kalan)        │
│                              │
│           ◯                  │  ikon 48dp, primary %40 (zırnık/madeni para çizgisi)
│     Bu ay sessiz.            │  başlık 17 Medium
│  Henüz kayıt yok.            │  gövde 14
│  [ İlk kaydı ekle ]          │  FilledTonalButton, 48dp
│                              │
```
Ortalanmış, üstten 48dp boşluk. Düğme FAB ile aynı diyaloğu açar.

## 4. Mikro metinler (hepsi tek satır)

| Yer | Metin |
|---|---|
| Boş durum başlık | Bu ay sessiz. |
| Boş durum açıklama | Henüz kayıt yok, kasa da kıpırdamamış. |
| Boş durum düğme | İlk kaydı ekle |
| Kalan etiketi | Bu ay kalan |
| Kalan negatif (uyarı) | Gider geliri geçti. Hesap defteri bunu not etti. |
| Kalan sıfır | Başabaş. Ne ekmek ne yoksa. *(alternatif sade: "Başabaş.")* |
| Kalan pozitif (isteğe bağlı) | Cebin hâlâ ayakta. |
| FAB açıklaması / düğme | Kayıt ekle |
| Ekleme başlığı | Yeni kayıt |
| Tutar alanı hatası | Tutarı rakamla yaz, örn. 12,50 |
| Ekle düğmesi | Ekle |
| Silme başlığı | Silinsin mi? |
| Silme gövdesi | Market · 420,00 ₺ |
| Silme uyarısı | Geri alınmaz. |
| Silme düğmeleri | Vazgeç / Sil |
| Silindi bildirimi (isteğe bağlı) | Kayıt silindi. |
| Liste başlığı | Bu ayın kayıtları |

Kural: tek espri, tek satır; uyarıda (negatif kalan) ton ciddi kalır, ünlem yok. Öneri: "Kalan sıfır" için sade "Başabaş." kullan; iki espriyi yan yana tutma. Kısa kalması için mevcut "Bu ay kayıt yok. + ile ekle." yerine yukarıdaki boş durum konur.

## 5. Erişilebilirlik

- **Dokunma hedefi 48dp:** ay okları, silme ikonu, chip'ler, diyalog düğmeleri, boş durum düğmesi. Liste satırı min 64dp. `IconButton` zaten 48dp; chip'te `Modifier.heightIn(min = 48.dp)`. Silme ikonu tutara bitişik: aralarında 8dp.
- **Kontrast:** metin ≥ 4.5 (tablolarda ölçülü), ikon/kenar ≥ 3. Altın metin için kullanılmaz. Soluk metinde (tarih) onSurfaceVariant kullan, alfa düşürme.
- **Renk tek taşıyıcı olmasın:** `+`/`−` işareti, Gelir/Gider yazısı, seçili chip'te onay ikonu.
- **İçerik açıklamaları:** FAB "Kayıt ekle"; ay okları "Önceki ay"/"Sonraki ay"; silme ikonu satıra özel: "Sil: Market, 420 lira" (`contentDescription = "Sil: ${not}, ${tutar}"`). Dekoratif boş durum ikonu `contentDescription = null`.
- **Okuyucu için tutar:** satırı birleştir (`semantics(mergeDescendants = true)`), `contentDescription = "Market, gider, 420 lira 50 kuruş, 12 Kasım"`. Kalan kartı: "Bu ay kalan 12 bin 480 lira 50 kuruş" (negatifse "eksi"). `−` işaretini okuyucu atlayabilir; bu yüzden birleşik açıklama şart.
- **Başlık işareti:** ay adına `heading()` semantiği; diyalog alanlarında etiket alan içinde kalsın (label).
- **Yazı ölçeği:** sp, 200% büyütmede tutar satırı iki satıra sarabilir; kaydırma yapısı bunu karşılar.
- **Hareket:** yalnızca M3 varsayılan geçişleri; özel animasyon yok.
- Sistem koyu/açık temayı izler; dinamik renk (Material You) kapalı, marka paleti sabit.

## 6. Uygulama listesi (kodu yazan için)

1. **Yeni `Theme.kt`:** `lightColorScheme(...)` ve `darkColorScheme(...)` yukarıdaki tablolarla; `Typography` bölüm 2'deki ölçekle; `Shapes(small=8, medium=12, large=20, extraLarge=28)`. `MainActivity.setContent` içindeki `MaterialTheme(...)` bunu çağırır. `Surface` rengi background olur.
2. **Gelir/gider renkleri:** `private val Green/Red` sil; temaya duyarlı `incomeColor()` / `expenseColor()` (isSystemInDarkTheme'e göre bölüm 1 çifti).
3. **`ZirnikApp` kalan kartı:** `Card` renkleri `#4A1224` (açık) / `#5A1A2E` (koyu), yazı krem; tutar 40sp Bold tnum; negatifse #FF8F70 ve altında uyarı metni; gelir/gider özeti kart içinde, koyu varyant renklerle; `RoundedCornerShape(20.dp)`, iç boşluk 20.
4. **Tutar biçimi:** liste ve özette `-` yerine `−`; `+` gelirde kalır. `formatTl` değişmez (yalnızca gösterim önekini `ZirnikApp` ekler).
5. **Liste satırı:** min 64dp, tutar 17sp SemiBold, silme `IconButton`'a satıra özel `contentDescription`; satıra `semantics(mergeDescendants)` + birleşik açıklama; başına "Bu ayın kayıtları" etiketi.
6. **Boş durum:** `Text("Bu ay kayıt yok...")` yerine ortalanmış Column (ikon, iki metin, `FilledTonalButton` → `adding = true`).
7. **FAB:** `containerColor = primary`, `contentDescription = "Kayıt ekle"`.
8. **`AddDialog`:** `FilterChip`'lere `leadingIcon` (seçiliyse Check), min yükseklik 48dp, seçili renkleri gelir/gider renginden; tutar alanına `FocusRequester` ile otomatik odak; hata metni `supportingText`.
9. **Silme `AlertDialog`:** gövdeye "Geri alınmaz." satırı; `Sil` düğmesi `colors = ButtonDefaults.textButtonColors(contentColor = error)`.
10. **Metinleri bölüm 4'ten al;** negatif kalan uyarısı ve boş durum metinleri `strings.xml`'e taşınabilir (isteğe bağlı, tek dil olduğundan zorunlu değil).
11. **Kontrol:** iki temada ekran görüntüsü; TalkBack ile kalan kartı ve bir satır okutulur; yazı ölçeği 200%.
