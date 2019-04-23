package id.go.bandarlampungkota.laporapp.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import id.go.bandarlampungkota.laporapp.model.Lapor;

@Database(entities = {Lapor.class}, version = 2,exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract LaporDao laporDao();

    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    AppDatabase() {
    }

    private static AppDatabase create(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "lapor.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
}

