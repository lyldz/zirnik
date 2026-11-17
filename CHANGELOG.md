# Değişiklik günlüğü

## v0.3.0 — 2026-11-17
- Kredi kartı ekstresi PDF'inden içe aktarma: önizleme, işaretleme, yön değiştirme.
- İade gelir olur, kart borcu ödemesi atlanır, mevcut kayıtla çakışan satır "zaten var" diye işaretlenmez.
- Şifreli PDF için parola sorulur.
- Düzeltme: büyük harfli açıklamalar "Netflıx" yerine "Netflix" olur (fiş mağaza adı için de).

## v0.2.0 — 2026-11-16
- Fiş OCR'ı: fotoğraftan toplam tutar, tarih ve mağaza adı okunup forma taslak olarak yazılır (çevrimdışı, ML Kit).
- Fişten tarih okunursa kayıt o tarihe yazılır.
- Yalnızca ARM işlemciler için derlenir (x86 çıkarıldı), APK yaklaşık 36 MB.

## v0.1.0 — 2026-11-14
İlk sürüm.
- Gelir ve gider ekleme (tutar, not), kayıt silme.
- Ay ay gezinme ve aylık kalan tutarı.
- Harcamaya fiş fotoğrafı ekleme (kamera ya da galeri).
- Veriler yalnızca cihazda; hesap ve internet gerekmez.
- Vişne çürüğü temalı açık/koyu tasarım ve yeni logo.
