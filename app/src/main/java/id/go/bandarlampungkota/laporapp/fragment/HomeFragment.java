package id.go.bandarlampungkota.laporapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.go.bandarlampungkota.laporapp.InputActivity;
import id.go.bandarlampungkota.laporapp.MyApp;
import id.go.bandarlampungkota.laporapp.R;
import id.go.bandarlampungkota.laporapp.adapter.LaporAdapter;
import id.go.bandarlampungkota.laporapp.db.AppDatabase;
import id.go.bandarlampungkota.laporapp.model.Lapor;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView list;
    LaporAdapter laporAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        list = view.findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);

        List<Lapor> lapors = AppDatabase.getInstance(getActivity()).laporDao().getAll();
        laporAdapter = new LaporAdapter(getActivity(),lapors);
        list.setAdapter(laporAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),InputActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
