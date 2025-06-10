package com.project.finalmobile.zaitunes.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.project.finalmobile.R;
import com.project.finalmobile.zaitunes.util.ThemePreference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set theme sebelum super.onCreate
        ThemePreference themePref = new ThemePreference(this);
        if (themePref.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button getStartedButton = findViewById(R.id.btn_get_started);

        getStartedButton.setOnClickListener(v -> {
            // Pindah ke MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Tutup SplashActivity setelah pindah agar tidak bisa kembali
            finish();
        });
    }
}
