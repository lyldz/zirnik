# Zırnık — hafıza dosyası

Her adım ve karar buraya yazılır. Yeni oturum önce bunu okur.

## Uygulama tanıtımı
**Zırnık**, gelir ve gideri takip eden basit bir Android uygulaması. Veriler yalnızca telefonda durur, internet ve hesap gerekmez.
- Gelir/gider ekle (tutar, not), ay ay gez, aylık kalanı gör, kaydı sil.
- Tutarlar kuruş olarak (Long) saklanır, yuvarlama hatası olmaz.
- Kotlin + Jetpack Compose, `SharedPreferences` içinde JSON.
- Repo: github.com/lyldz/zirnik (public). Paket: `com.zirnik.app`.

## Çalışma düzeni (kullanıcının kararı)
Uygulama **soru sorarak** yapılır: karar gerektiren her şeyi önce kullanıcıya sorarım.

| Rol | Sorumlu | İş |
|---|---|---|
| Logo + bildirim simgesi | Ajan 1 | İsme uygun logo, başlatıcı ve bildirim simgesi |
| Uygulama içi tasarım | Ajan 2 | Ekranlar, renkler, yazı, boş durumlar |
| Kod yazımı | Claude (ana oturum) | Tüm kod, derleme, test, commit |
| Olmazsa olmaz özellikler | Ajan 3 | Uygulamanın eksiksiz çalışması için şart olanlar |
| Yayın eksikleri ve düzeltmeler | Ajan 4 | İmza, CI, sürüm, mağaza/yayın hazırlığı |
| Repo takibi ve düzen | Ajan 5 | README, issue/etiket, sürüm notları, repo bakımı |

Kısıtlar: sunucuda 2 GB RAM var. **Ajanlar `gradlew` çalıştırmaz**, derlemeyi yalnızca ana oturum yapar. Ajanlar `/root/yeter`, `/root/anahtarlar` ve diğer projelere dokunmaz.

## Adımlar (kronolojik)
- 2026-11-14: Proje `/root/zirnik` olarak açıldı. Yeter'den gradle sarmalayıcı, `.gitignore`, `check.yml` kopyalandı.
- 2026-11-14: İskelet yazıldı: `Ledger.kt` (mantık + kayıt), `MainActivity.kt` (ekran), `LedgerTest.kt` (3 test), ikon, tema, manifest. **Henüz derlenmedi, git'e eklenmedi.**
- 2026-11-14: Kullanıcı rol dağılımını ve soru-cevapla ilerlemeyi istedi.

## Kararlar (kullanıcıdan)
- İsim "Zırnık" rastgele seçildi. Hava: **esprili ama sakin ve ciddi**.
- Renkler: **vişne çürüğü** ve ağır, zengin tonlar.
- Başlangıç paleti (tasarım ajanı inceltebilir, logo bunu kullanır): vişne çürüğü `#6B1E34`, koyu vişne `#4A1224`, altın/bakır vurgu `#C9A24B`, açık zemin krem `#F6EFE8`, koyu zemin `#1A1014`.
- Kullanım: **kendi bütçesi**. Bir harcamaya **fiş fotoğrafı eklenebilmeli** (market fişi gibi).
- 2026-11-14: Logo ve tasarım ajanları başlatıldı (kod ve derleme bende).

- 2026-11-14: Logo ajanı bitti (koyu vişne zeminde küçük altın para + yanında kırıntı para; notlar `docs/logo-notlari.md`). Manifest bağlandı: `@mipmap/ic_launcher` + `roundIcon`; API 24-25 için `mipmap-anydpi/` altına düz vektöre işaret eden alias eklendi. Çizim telefonda görülmedi.

- 2026-11-14: Tasarım ajanı bitti: `docs/tasarim.md` (iki tema rol tablosu, gelir yeşil / gider kiremit, sistem fontu, taslaklar, mikro metinler, erişilebilirlik, 11 maddelik uygulama listesi). Altın metinde kullanılmaz (kontrast 2.2:1), metin için koyu bakır. Koddaki sabit `Green`/`Red` temaya duyarlı hale gelecek. Henüz koda işlenmedi, fiş kararını bekliyor.

- Karar: fiş fotoğrafı **kamera ve galeri ikisi birden**, **OCR yok** (tutar elle girilir). Fotoğraf `filesDir/receipts/` altında küçültülmüş JPEG olarak saklanır, kayıt silinince dosya da silinir.
- 2026-11-14: Yayın ajanı (imza anahtarı + build-apk.yml + release notu) ve repo ajanı (README, SURUMLER.md) başlatıldı. Kamera/galeri/fiş kodu yazılıyor.

- 2026-11-14: Fiş altyapısı yazıldı: `Receipts.kt` (küçült + EXIF döndür + sakla), `file_paths.xml`, manifestte FileProvider, `exifinterface` bağımlılığı. `Entry.receipt` alanı ve arayüz düğmeleri henüz yok.
- Soru (kullanıcı): PDF yüklenince giderler otomatik çıkarılabilir mi? Cevap: mümkün ama v1 dışı, ayrıntı aşağıda "Açık sorular".

- 2026-11-14: Yayın ajanı bitti. İmza anahtarı `/root/anahtarlar/zirnik-signing/` (yedek: `zirnik-signing-yedek/`), `build-apk.yml`, `release-notes.md` yazıldı, `.gitignore`'a `zirnik-password.txt` eklendi. Repo ajanı bitti: README, SURUMLER, CHANGELOG.
- Yayın riskleri: `allowBackup=false` (telefon değişince veri gider, kullanıcıya söylenecek); Play için targetSdk 35, AAB, gizlilik politikası ve mağaza görselleri gerekir (APK dağıtımı için gerekmez); kamera `TakePicture` ile harici kamera uygulamasını açtığı için `CAMERA` izni **bildirilmez**.

- 2026-11-14: Kod tamam ve derlendi: `Theme.kt` (tasarım belgesi renkleri, açık/koyu), `MainActivity.kt` (kalan kartı, boş durum, kamera/galeri fiş düğmeleri, fiş görüntüleme), `Entry.receipt`. `testDebugUnitTest` ve `assembleDebug` geçti. Repo public açıldı: github.com/lyldz/zirnik, secret'lar (`ZIRNIK_KEYSTORE_B64`, `ZIRNIK_KS_PASS`) eklendi, ilk push yapıldı (commit 9d1ba2d).
- **Karar değişti:** Kullanıcı sonradan "ocr olmalı" dedi; önceki "OCR yok" kararı geçersiz. Kapsam (yalnızca tutar mı, tarih/mağaza da mı, PDF dahil mi) sorulacak.

- 2026-11-14: CI başarılı (2 dk 47 sn), **v0.1.0 yayında**: https://github.com/lyldz/zirnik/releases/tag/v0.1.0 (Zirnik.apk). APK telefonda hiç denenmedi (logo görünümü, kamera/galeri, tema).
- Oturum kaydedildi ve kapatıldı. Sonraki oturum: önce OCR kapsamı sorusunu sor (yalnızca tutar mı, tarih/mağaza da mı), önerim tutar + tarih; PDF içe aktarma ayrı iş.

- 2026-11-16: Kullanıcı OCR kapsamını seçti: **tutar, tarih ve mağaza adı**. `Parse.kt` (saf ayrıştırıcı: TOPLAM/GENEL TOPLAM/ÖDENECEK önceliği, ARA TOPLAM ve KDV hariç, tutar sonraki satıra kayabilir, gelecek ve geçersiz tarih reddedilir, mağaza ilk 6 satırdan), `Ocr.kt` (ML Kit Latin, çevrimdışı), `ParseTest.kt` (4 test). Forma yalnızca boş alanlar doldurulur; fiş tarihi varsa kayıt o güne yazılır. v0.2.0 derlendi, testler geçti.
- APK boyutu: ML Kit ile 14,8 → 59 MB çıktı, x86 çıkarılınca 36 MB (debug). R8 kapalı; küçültme gerekirse ayrıca test et.
- OCR gerçek fişlerle denenmedi, yalnızca uydurma metinle test edildi.

- 2026-11-17: PDF ekstre içe aktarma (v0.3.0). Kullanıcı: ekstre **kredi kartı ekstresi**; banka adı ve örnek PDF verilmedi, bu yüzden ayrıştırıcı **genel** yazıldı. `Statement.kt` (satır başında tarih, ilk tutar, eksi başta/sonda = iade, eksi + ÖDEME/TAHSİLAT = kart borcu ödemesi → atlanır, `dupKey` ile çakışma), `Pdf.kt` (pdfbox-android, şifreli PDF için parola), `ImportDialog.kt` (seç → önizle → işaretle → ekle), `StatementTest.kt`. Ay çubuğunda yükleme düğmesi.
- Hata düzeltildi: `titleCaseTr` büyük I'yı ı yapıyordu (NETFLIX → Netflıx); artık I/İ → i sonra başlık harfi. Kusur: "IŞIK" → "İşik".
- APK 45 MB (pdfbox +9 MB). Derleme artık ~3-7 dk.
- **Açık:** gerçek banka ekstresiyle denenmedi. Kullanıcıdan banka adı + örnek satırlar (ad/IBAN/kart no silinmiş) iste, `parseStatement`'ı ona göre ayarla. Bilinen sınırlar: alt satıra taşan açıklama, hesap (banka hesabı) ekstresi desteklenmez, taranmış PDF için OCR yedeği yok.

## Sonraki oturum için sıra
1. Kullanıcıdan OCR kapsamını al, sonra ML Kit (çevrimdışı, ~4 MB) ile fiş fotoğrafından taslak doldur; kullanıcı düzeltip ekler.
2. Telefonda dene (logo, tema, kamera/galeri, fiş görüntüleme), bulguları düzelt.
3. Sürüm artır (`version.properties` + `.github/release-notes.md`), push.
4. Sonra: PDF'ten içe aktarma (hangi banka/belge sorusu açık), kategori, yedekleme.
5. Dikkat: main'e her push release üretir; yalnızca belge değişikliğinde commit mesajına `[skip ci]` ekle. Derlemede tek gradle, alt ajan gradlew çalıştırmaz.

## Bekleyen işler
- [ ] İlk derleme ve testler (`:app:compileDebugKotlin`, `testDebugUnitTest`)
- [ ] Yeni imza anahtarı (`/root/anahtarlar/zirnik-signing`), GitHub secret'ları `ZIRNIK_KEYSTORE_B64` ve `ZIRNIK_KS_PASS`
- [ ] `build-apk.yml` (release workflow), `README.md`, `.github/release-notes.md`
- [ ] `git init`, GitHub'da public `zirnik` reposu, ilk push
- [ ] Logo, bildirim simgesi, tasarım (sorulardan sonra)

## Açık sorular
- Fiş fotoğrafı: kamera mı galeri mi, fişten tutarı otomatik okuma (OCR) istenir mi
- PDF'ten otomatik gider çıkarma: hangi PDF (banka ekstresi mi, e-fatura mı, market fişi mi) ve hangi banka? Metinli PDF için bankaya özel ayrıştırıcı gerekir, taranmış PDF için OCR gerekir.
- Kategori gerekir mi, yedekleme isteniyor mu
