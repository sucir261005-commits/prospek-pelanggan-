# Prospek Pelanggan - Android

Aplikasi Android berbasis Java untuk mengelola prospek pelanggan melalui REST API Laravel. Aplikasi menggunakan Retrofit untuk komunikasi HTTP, Repository sebagai perantara data, dan MVVM dengan LiveData untuk menampilkan perubahan data.

## Fitur

- Login dan logout menggunakan akun ERP.
- Melihat daftar prospek dan detail lengkap setiap prospek.
- Menambahkan, mengubah, dan menghapus prospek.
- Mengirim token akses sebagai Bearer token pada permintaan API yang memerlukan autentikasi.

## Teknologi

- Java
- Android SDK 34
- Retrofit 2 dan OkHttp
- AndroidX ViewModel dan LiveData
- Laravel REST API dengan autentikasi Sanctum

## Arsitektur

```text
MainActivity (View)
       |
ProspectViewModel (ViewModel dan LiveData)
       |
ProspectRepository
       |
ApiService (Retrofit) -> Laravel REST API
```

Model request dan response berada di `app/src/main/java/com/neosantara/prospek/model`.

## Menjalankan aplikasi

1. Buka proyek ini di Android Studio dan tunggu Gradle selesai melakukan sinkronisasi.
2. Jalankan server Laravel pada komputer:

   ```powershell
   php artisan serve --port=8000
   ```

3. Untuk perangkat Android fisik yang tersambung melalui USB, aktifkan USB debugging, izinkan komputer pada prompt di perangkat, lalu jalankan:

   ```powershell
   adb reverse tcp:8000 tcp:8000
   ```

   URL bawaan aplikasi adalah `http://127.0.0.1:8000/api/`. Perintah `adb reverse` meneruskan koneksi dari perangkat ke server pada komputer.

4. Pilih perangkat di Android Studio lalu tekan **Run**. Login menggunakan akun ERP yang sudah terdaftar.

Untuk Android Emulator, bangun aplikasi dengan alamat khusus emulator:

```powershell
.\gradlew.bat assembleDebug -PPROSPECT_API_BASE_URL=http://10.0.2.2:8000/api/
```

Izin HTTP hanya diaktifkan untuk build debug untuk kebutuhan pengembangan lokal. Gunakan HTTPS untuk server produksi.

## Build

```powershell
.\gradlew.bat assembleDebug
```

APK debug dibuat di `app/build/outputs/apk/debug/app-debug.apk`.

## Screenshot

Screenshot aplikasi belum disertakan. Tambahkan screenshot demo yang tidak menampilkan data pribadi sebelum repository dibagikan secara publik.
