package id.go.bandarlampungkota.laporapp.model;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Lapor {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Long id;
    public String path;
    public String keterangan;
    public Date waktu;
    public String lokasi;
    public String pil;//pileg pilpres
}

