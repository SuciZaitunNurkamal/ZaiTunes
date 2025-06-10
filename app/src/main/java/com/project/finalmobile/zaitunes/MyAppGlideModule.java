package com.project.finalmobile.zaitunes; // Your application's package

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

// Konfigurasi Glide untuk loading gambar
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    // Class ini digunakan untuk konfigurasi Glide
    // Bisa dikosongkan untuk menggunakan konfigurasi default
    
    // Contoh konfigurasi yang bisa ditambahkan:
    // @Override
    // public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    //     builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
    // }

    // Nonaktifkan parsing manifest lama
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}