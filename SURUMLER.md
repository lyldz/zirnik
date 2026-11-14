# Sürüm düzeni

## Kural
- Biçim: `MAJOR.MINOR.PATCH` (ör. 0.1.0). Git etiketi ve gösterim `v` önekiyle: `v0.1.0`.
- Tek doğruluk kaynağı: `version.properties` (`VERSION_NAME`, `VERSION_CODE`). Kodda elle sürüm yazılmaz.
- Her `main` push'u otomatik release üretir. Sürüm aynı kalırsa eski release ezilir; bu yüzden her değişiklikte en az PATCH artır.
- Yeni özellik: MINOR artar, PATCH sıfırlanır. Büyük yeniden tasarım: MAJOR artar, diğerleri sıfırlanır.
- `VERSION_CODE` her sürümde 1 artar, asla düşmez.

## Release adı ve notu
- Etiket: `v<sürüm>`. Ad ve etiket iş akışında `version.properties`'ten üretilir.
- Release notu: `.github/release-notes.md`.

## Sürüm artırırken dokunulacak dosyalar
1. `version.properties`
2. `.github/release-notes.md`
3. `CHANGELOG.md` (yeni girdi)
