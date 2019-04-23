package id.go.bandarlampungkota.laporapp.model;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Lapor implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Long id;
    public String path;
    public String keterangan;
    public Date waktu;
    public String lokasi;
    public String pil;//pileg pilpres
    public int sent;
    public double latitude;
    public double longitude;
}

