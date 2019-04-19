package id.go.bandarlampungkota.laporapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import id.go.bandarlampungkota.laporapp.model.Lapor;

@Database(entities = {Lapor.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract LaporDao laporDao();
}

