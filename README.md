# Zırnık

Gelir ve gideri takip eden basit bir Android uygulaması. Hesap yok, internet yok; veriler yalnızca telefonda durur.

## Özellikler
- **Gelir/gider kaydı:** Tutar ve not ile ekle, istediğin kaydı sil.
- **Ay ay gezinme:** Önceki ve sonraki aylara geç, o ayın kayıtlarını gör.
- **Aylık kalan:** Her ay için gelir, gider ve kalan tutar.
- **Fiş fotoğrafı:** Harcamaya kamera ile çekilen ya da galeriden seçilen fiş eklenir.
- **Kuruş hassasiyeti:** Tutarlar kuruş olarak saklanır, yuvarlama hatası olmaz.

## Kurulum
[GitHub Releases](https://github.com/lyldz/zirnik/releases) sayfasından `Zirnik.apk` dosyasını indirip telefona kur. Bilinmeyen kaynaklardan kuruluma izin vermen gerekebilir.

## Derleme
Gereken: JDK 17 ve `ANDROID_HOME` (Android SDK yolu).

```
export ANDROID_HOME=/yol/android-sdk
./gradlew assembleDebug
```

APK `app/build/outputs/apk/debug/` altında çıkar.

## Proje yapısı
- `app/src/main/java/com/zirnik/app/Ledger.kt`: Mantık ve kayıt (hesaplama, `SharedPreferences` içinde JSON).
- `app/src/main/java/com/zirnik/app/MainActivity.kt`: Arayüz (Jetpack Compose).
- `docs/`: Tasarım ve logo notları.

## Gizlilik
Veriler ve fiş fotoğrafları yalnızca cihazda tutulur, hiçbir yere gönderilmez. Yedekleme yoktur: uygulamayı silersen ya da telefonu değiştirirsen kayıtlar gider.
