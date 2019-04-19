package id.go.bandarlampungkota.laporapp;

import android.app.Application;
import androidx.room.Room;
import id.go.bandarlampungkota.laporapp.db.AppDatabase;

public class MyApp extends Application {
    AppDatabase db;
    private static MyApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "lapor.db").build();
    }

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public AppDatabase getDatabase() {
        return db;
    }
}

