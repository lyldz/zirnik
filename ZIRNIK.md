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
