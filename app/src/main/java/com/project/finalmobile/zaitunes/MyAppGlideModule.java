package com.project.finalmobile.zaitunes; // Your application's package

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

// This class must be public and not abstract.
// The @GlideModule annotation is required.
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    // You can leave this class empty for now.
    // Later, you can override methods here to customize Glide's behavior,
    // such as default request options, memory cache size, disk cache strategy, etc.

    // Example:
    // @Override
    // public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
    //     builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565));
    // }

    // Ensure this is false if you're not using legacy manifest parsing.
    // It's generally recommended to keep it false and use the annotation processor.
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}