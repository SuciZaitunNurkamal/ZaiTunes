package com.project.finalmobile.zaitunes.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// @Database mendeklarasikan class ini sebagai database.
// 'entities' berisi daftar semua class Entity yang ada di database ini.
// 'version' adalah versi database, harus dinaikkan jika ada perubahan skema.
@Database(entities = {RatedTrack.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Database harus menyediakan akses ke setiap DAO yang dimilikinya.
    public abstract RatedTrackDao ratedTrackDao();

    // 'volatile' memastikan bahwa nilai INSTANCE selalu ter-update
    // dan sama untuk semua thread.
    private static volatile AppDatabase INSTANCE;

    // Metode ini menggunakan pola Singleton untuk memastikan hanya ada
    // satu instance database yang dibuat di seluruh aplikasi.
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // 'synchronized' memastikan bahwa hanya satu thread yang bisa
            // masuk ke blok ini pada satu waktu, mencegah pembuatan dua instance.
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "music_database")
                                    .fallbackToDestructiveMigration() // Menghapus data lama saat migrasi
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
