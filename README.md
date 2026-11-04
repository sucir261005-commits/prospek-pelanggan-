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

<img width="720" height="1549" alt="e5356d5f-a684-4e78-b525-30cb68f68ffc" src="https://github.com/user-attachments/assets/9205cac9-d087-43b2-9150-112ac423f2b6" />
<img width="720" height="1553" alt="0f62e0a4-f19b-43e0-8f50-5f55c1fb7739" src="https://github.com/user-attachments/assets/98cf399f-1a6b-4616-9e36-dadf898e83a1" />
<img width="720" height="1547" alt="b314a3af-f76b-4500-b6bf-758fec7952f8" src="https://github.com/user-attachments/assets/0b06cd2c-2e60-4cef-a08f-3b62e5c36486" />


