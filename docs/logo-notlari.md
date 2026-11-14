# Logo notları

**Fikir:** Vişne çürüğü zeminde tek, küçük bir altın madeni para ve yanında kırıntı kadar bir para daha. Etrafı bomboş. "Zırnık koklamamak" sessizce kendini anlatır.

**Renkler:** vişne çürüğü `#6B1E34`, koyu vişne `#4A1224`, altın/bakır `#C9A24B`, krem `#F6EFE8` (ikonda kullanılmadı, uygulama içi için).

## Dosyalar (`app/src/main/res/`)
- `drawable/ic_launcher_background.xml`, `ic_launcher_foreground.xml`: adaptive icon katmanları (108dp, içerik 66dp güvenli alanda).
- `drawable/ic_launcher_monochrome.xml`: Android 13 temalı ikon (tek renk).
- `mipmap-anydpi-v26/ic_launcher.xml`, `ic_launcher_round.xml`: adaptive-icon tanımı (API 26+).
- `drawable/ic_launcher.xml`: API 24-25 için tek parça düz logo.
- `drawable/ic_stat_zirnik.xml`: bildirim simgesi, 24dp, beyaz silüet.

## Manifestte bağlama
`<application>` içinde:
```
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"
```
Dikkat: `@mipmap/ic_launcher` yalnızca `mipmap-anydpi-v26` içinde var, API 24-25'te çözülmez. Çözüm: `res/mipmap-anydpi/ic_launcher.xml` ve `ic_launcher_round.xml` aç, içine `<layer-list><item android:drawable="@drawable/ic_launcher"/></layer-list>` koy (ya da basitçe `android:icon="@drawable/ic_launcher"` kullan, ama o zaman adaptive/temalı ikon olmaz).
Bildirimde: `setSmallIcon(R.drawable.ic_stat_zirnik)`.
