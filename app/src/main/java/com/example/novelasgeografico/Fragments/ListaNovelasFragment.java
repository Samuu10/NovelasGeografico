package com.example.novelasgeografico.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novelasgeografico.R;
import com.example.novelasgeografico.Activities.MainActivity;
import com.example.novelasgeografico.Almacenamiento.PreferencesManager;
import com.example.novelasgeografico.GestionNovelas.Novela;
import com.example.novelasgeografico.GestionNovelas.NovelaAdapter;

import java.util.List;

//Clase ListaNovelasFragment que representa el fragmento que muestra la lista de novelas
public class ListaNovelasFragment extends Fragment implements PreferencesManager.LoadNovelasCallback {

    //Variables
    private NovelaAdapter adapter;
    private RecyclerView recyclerView;
    private PreferencesManager preferencesManager;

    //Metodo para crear la vista del fragmento
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_lista_novelas, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewMainNovelas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        preferencesManager = new PreferencesManager(requireContext());
        preferencesManager.loadNovelas(this);
        return view;
    }

    //Metodo para gestionar los detalles de las novelas cargadas
    @Override
    public void onNovelasLoaded(List<Novela> loadedNovelas) {
        adapter = new NovelaAdapter(getContext(), loadedNovelas, novela -> {
            ((MainActivity) getActivity()).mostrarDetallesNovela(novela);
        });
        recyclerView.setAdapter(adapter);
    }

    //Metodo para actualizar la lista de novelas
    public void actualizarLista(List<Novela> nuevasNovelas) {
        if (adapter != null) {
            adapter.setNovelas(nuevasNovelas);
            adapter.notifyDataSetChanged();
        }
    }

    //Metodo para gestionar la pausa del fragmento y liberar recursos
    @Override
    public void onStop() {
        super.onStop();
        //Liberar referencias a vistas o adaptadores
        recyclerView.setAdapter(null);
    }

    //Metodo para gestionar la destrucción del fragmento y liberar recursos
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Liberar referencias a objetos grandes o contextos
        preferencesManager = null;
    }
}