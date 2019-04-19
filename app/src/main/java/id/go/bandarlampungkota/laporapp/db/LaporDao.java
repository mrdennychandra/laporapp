package id.go.bandarlampungkota.laporapp.db;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import id.go.bandarlampungkota.laporapp.model.Lapor;

@Dao
public interface LaporDao {

    @Query("SELECT * FROM lapor")
    List<Lapor> getAll();

    @Query("SELECT * FROM lapor WHERE id=:id")
    List<Lapor> getById(Long id);

    @Insert
    void insert(Lapor lapor);

    @Update
    void update(Lapor lapor);

    @Delete
    void delete(Lapor lapor);

    @Query("SELECT COUNT(*) from lapor")
    Integer count();
}