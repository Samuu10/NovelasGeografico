package com.example.novelasgeografico.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.novelasgeografico.R;
import com.example.novelasgeografico.Almacenamiento.PreferencesManager;
import com.example.novelasgeografico.GestionNovelas.Novela;

import java.util.List;

//Clase DetallesNovelasFragment que representa el fragmento que muestra los detalles de una novela
public class DetallesNovelasFragment extends Fragment implements PreferencesManager.LoadNovelasCallback {

    //Variables
    private Novela novela;
    private PreferencesManager preferencesManager;

    //Metodo para crear la vista del fragmento
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_detalles_novela, container, false);
        TextView textViewTitulo = view.findViewById(R.id.textViewTituloDetalle);
        TextView textViewAutor = view.findViewById(R.id.textViewAutorDetalle);
        TextView textViewAño = view.findViewById(R.id.textViewAñoDetalle);
        TextView textViewSinopsis = view.findViewById(R.id.textViewSinopsisDetalle);
        CheckBox checkBoxFavorito = view.findViewById(R.id.checkbox_favorito);

        //Instanciar el PreferencesManager
        preferencesManager = new PreferencesManager(requireContext());

        //Obtener la novela seleccionada y mostrar sus detalles
        if (getArguments() != null) {
            novela = getArguments().getParcelable("novela");
            if (novela != null) {
                textViewTitulo.setText(novela.getTitulo());
                textViewAutor.setText(novela.getAutor());
                textViewAño.setText(String.valueOf(novela.getAñoPublicacion()));
                textViewSinopsis.setText(novela.getSinopsis());
                checkBoxFavorito.setChecked(novela.getFavorito());
            }
        }

        //Checkbox para marcar como favorito
        checkBoxFavorito.setOnCheckedChangeListener((buttonView, isChecked) -> {
            novela.setFavorito(isChecked);
            preferencesManager.loadNovelas(new PreferencesManager.LoadNovelasCallback() {
                @Override
                public void onNovelasLoaded(List<Novela> loadedNovelas) {
                    for (Novela n : loadedNovelas) {
                        if (n.equals(novela)) {
                            n.setFavorito(novela.getFavorito());
                            break;
                        }
                    }
                    preferencesManager.saveNovelas(loadedNovelas);
                }
            });
        });

        //Boton para volver a la lista de novelas
        view.findViewById(R.id.btn_volver).setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    //Metodo para gestionar el checkbox de favoritos
    @Override
    public void onNovelasLoaded(List<Novela> loadedNovelas) {
        for (Novela n : loadedNovelas) {
            if (n.equals(novela)) {
                n.setFavorito(novela.getFavorito());
                break;
            }
        }
        preferencesManager.saveNovelas(loadedNovelas);
        Toast.makeText(getContext(), novela.getFavorito() ? "Añadida a favoritos" : "Eliminada de favoritos", Toast.LENGTH_SHORT).show();
    }

    //Metodo para gestionar la pausa del fragmento y liberar recursos
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Liberar referencias a objetos grandes o contextos
        preferencesManager = null;
    }
}