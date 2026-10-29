# PS IA-ITB — Aplikasi Android (tidak resmi)

Aplikasi Android pembungkus (wrapper) untuk **https://www.psiaitb.com**, dibangun dengan Capacitor 8.
Semua konten (jadwal, hasil, squad, statistik, berita, registrasi, fantasy, akun) dimuat langsung
dari situs, sehingga setiap pembaruan di situs otomatis tampil di aplikasi tanpa perlu update APK.

> **Status: tidak resmi.** Dibuat oleh anggota, bukan pengelola situs. Minta persetujuan pengurus
> PS IA-ITB sebelum menyebarkan APK ke anggota lain atau memakai crest resmi sebagai ikon.

## Fitur

| Fitur | Cara kerja |
|---|---|
| Layar penuh tanpa address bar | WebView Capacitor memuat psiaitb.com |
| Tombol Back Android | Mundur ke halaman sebelumnya; keluar app jika sudah di halaman awal |
| Pull-to-refresh | Tarik ke bawah saat di posisi paling atas halaman |
| Link eksternal | Instagram, WhatsApp, maps, dll. dibuka di aplikasi/browser bawaan HP |
| Halaman offline | Muncul saat tidak ada internet, dengan tombol "Coba lagi" |
| Upload file | Form yang meminta foto/berkas bisa memilih dari galeri/file |
| Splash & status bar | Warna tema situs (#070C18) |
| Sesi login | Cookie tersimpan di aplikasi; user tetap login antar sesi |

## Cara build APK (tanpa install apa pun di komputer)

1. Buat repository baru di GitHub (boleh private), lalu upload seluruh isi folder ini.
   ```bash
   git init && git add . && git commit -m "Initial"
   git branch -M main
   git remote add origin https://github.com/<akun>/psiaitb-app.git
   git push -u origin main
   ```
2. Buka tab **Actions**. Workflow **Build APK** berjalan otomatis (±5–8 menit).
3. Setelah selesai (centang hijau), buka run tersebut, lalu unduh artefak
   `psiaitb-apk-…`. Di dalamnya ada `psiaitb-1.0.N-debug.apk`.
4. Kirim APK ke HP, buka, lalu izinkan "Install dari sumber tidak dikenal".

APK **debug** cukup untuk uji coba dan pemakaian pribadi.

## APK release bertanda tangan (untuk dibagikan ke anggota)

APK yang dibagikan sebaiknya ditandatangani dengan kunci milik Anda sendiri. Kunci yang sama
**wajib** dipakai untuk setiap update; tanpa kunci itu, user harus uninstall dulu sebelum update.

1. Buat keystore sekali saja (butuh Java/JDK di komputer):
   ```bash
   keytool -genkeypair -v -keystore psiaitb-release.jks -alias psiaitb \
     -keyalg RSA -keysize 2048 -validity 10000
   ```
   Simpan file `.jks` dan password-nya di tempat aman (password manager). Jangan commit ke Git.
2. Ubah ke base64:
   - Linux/macOS: `base64 -w0 psiaitb-release.jks > ks.txt`
   - Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("psiaitb-release.jks")) > ks.txt`
3. Di GitHub: **Settings → Secrets and variables → Actions → New repository secret**, buat:

   | Secret | Isi |
   |---|---|
   | `ANDROID_KEYSTORE_BASE64` | isi `ks.txt` |
   | `ANDROID_KEYSTORE_PASSWORD` | password keystore |
   | `ANDROID_KEY_ALIAS` | `psiaitb` |
   | `ANDROID_KEY_PASSWORD` | password key |

4. Rilis versi dengan tag:
   ```bash
   git tag v1.0.0 && git push origin v1.0.0
   ```
   Workflow membuat **GitHub Release** berisi `psiaitb-1.0.0.apk` (signed). Link rilis ini bisa
   dibagikan ke anggota.

## Build lokal (opsional, dengan Android Studio)

Butuh Node.js 22+, JDK 21, dan Android Studio (SDK 36).
```bash
npm ci
npx cap sync android
npx cap open android     # lalu Build > Build APK(s)
```

## Ikon & splash

Ikon dan splash memakai crest PS IA-ITB (`assets/source/assets_img_psia-crest.png`, diambil dari
psiaitb.com oleh workflow `fetch-logo.yml`) di atas latar navy situs (#070C18).

Untuk memperbarui setelah logo di situs berubah:
1. Jalankan workflow **Fetch PS IA-ITB logo** (tab Actions → Run workflow).
2. `git pull`, lalu `python3 scripts/make-icons.py && npm run assets`.
3. Commit & push; APK baru ter-build otomatis.

## Konfigurasi penting (`capacitor.config.json`)

- `server.url` — alamat situs yang dimuat.
- `server.allowNavigation` — domain yang tetap dibuka **di dalam** aplikasi. Domain lain dibuka di luar.
  Jika login situs memakai penyedia pihak ketiga (mis. Supabase/Firebase di domain lain) dan terlempar
  ke browser, tambahkan domain tersebut ke daftar ini.
- `appId` — `com.psiaitb.community`. Jangan diubah setelah APK dibagikan (Android menganggapnya app berbeda).

## Batasan yang perlu diketahui

- **Login dengan Google** tidak bisa dilakukan di dalam WebView karena kebijakan Google. Jika situs
  memakai "Sign in with Google", login akan terbuka di browser dan sesinya tidak ikut ke aplikasi.
  Login email/password di situs tidak terdampak.
- **Pull-to-refresh** mendeteksi scroll halaman utama. Jika situs memakai area scroll internal,
  tarik ke bawah bisa memicu refresh lebih awal dari yang diharapkan.
- **Google Play Store** cenderung menolak aplikasi yang hanya membungkus situs. Distribusi yang realistis
  adalah APK langsung (GitHub Release / grup WhatsApp). Untuk masuk Play Store perlu fitur native
  tambahan (mis. push notification jadwal matchday) dan idealnya dikelola pengurus situs.
- **Tidak bisa dipakai offline**: semua data berasal dari situs.

## Struktur

```
capacitor.config.json        konfigurasi app (URL, domain, splash, status bar)
www/offline.html             halaman saat tidak ada koneksi
assets/                      sumber ikon & splash
android/                     project native Android
  app/src/main/java/.../MainActivity.java   pull-to-refresh
  app/build.gradle                          signing & versi dari environment
.github/workflows/build-apk.yml             build otomatis di GitHub
```
