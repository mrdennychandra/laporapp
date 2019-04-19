package id.go.bandarlampungkota.laporapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import id.go.bandarlampungkota.laporapp.InputActivity;
import id.go.bandarlampungkota.laporapp.R;
import id.go.bandarlampungkota.laporapp.model.Lapor;

public class LaporAdapter extends RecyclerView.Adapter<LaporAdapter.ViewHolder> {

    Context context;
    List<Lapor> lapors;
    private SharedPreferences prefs;

    public LaporAdapter(Context context, List<Lapor> lapors) {
        this.context = context;
        this.lapors = lapors;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lapor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Lapor lapor = lapors.get(position);
        if(lapor.path != null){
            Glide.with(context).load(lapor.path).into(holder.imgLapor);
        }
        holder.txtPil.setText(lapor.pil);
        holder.txtKet.setText(lapor.keterangan);
        if(lapor.waktu!= null) {
            holder.txtWaktu.setText(new SimpleDateFormat("dd-MM-yyyy").format(lapor.waktu));
        }
        holder.txtLokasi.setText(lapor.lokasi);
        if(lapor.sent == 1){
            holder.txtSent.setText("terkirim");
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,InputActivity.class);
                intent.putExtra("lapor",lapor);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (lapors != null) ? lapors.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgLapor;
        TextView txtPil,txtKet,txtWaktu,txtLokasi,txtSent;
        CardView card;

        public ViewHolder(View itemView) {
            super(itemView);
            imgLapor = (ImageView) itemView.findViewById(R.id.img_lapor);
            txtPil = (TextView) itemView.findViewById(R.id.txt_pil);
            txtKet = (TextView) itemView.findViewById(R.id.txt_keterangan);
            txtWaktu = (TextView) itemView.findViewById(R.id.txt_waktu);
            txtLokasi = (TextView) itemView.findViewById(R.id.txt_lokasi);
            txtSent = (TextView) itemView.findViewById(R.id.txt_sent);
            card = (CardView) itemView.findViewById(R.id.card);

        }
    }
}
