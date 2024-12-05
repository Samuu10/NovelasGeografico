package com.example.novelasgeografico.Fragments;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.novelasgeografico.Activities.MainActivity;
import com.example.novelasgeografico.R;
import com.example.novelasgeografico.Almacenamiento.PreferencesManager;
import com.example.novelasgeografico.GestionNovelas.Novela;
import java.io.IOException;
import java.util.List;

//Clase DetallesNovelasFragment que representa el fragmento que muestra los detalles de una novela
public class DetallesNovelasFragment extends Fragment implements PreferencesManager.LoadNovelasCallback {

    //Variables
    private Novela novela;
    private PreferencesManager preferencesManager;
    private CheckBox checkBoxUbicacion;

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
        checkBoxUbicacion = view.findViewById(R.id.checkbox_ubicacion);

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
                checkBoxUbicacion.setChecked(novela.getLatitude() != 0 && novela.getLongitude() != 0);
            }
        }

        //Checkbox para marcar como favorito
        checkBoxFavorito.setOnCheckedChangeListener((buttonView, isChecked) -> {
            novela.setFavorito(isChecked);
            preferencesManager.loadNovelas(loadedNovelas -> {
                for (Novela n : loadedNovelas) {
                    if (n.equals(novela)) {
                        n.setFavorito(novela.getFavorito());
                        break;
                    }
                }
                preferencesManager.saveNovelas(loadedNovelas);
            });
        });

        //Checkbox para añadir ubicación
        checkBoxUbicacion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showAddLocationDialog();
            } else {
                novela.setLatitude(0);
                novela.setLongitude(0);
                preferencesManager.saveNovelas(preferencesManager.loadNovelasSync());
                Toast.makeText(requireContext(), "Ubicación eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        //Boton para volver a la lista de novelas
        view.findViewById(R.id.btn_volver).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
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

    private void showAddLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_location, null);
        EditText editTextCity = dialogView.findViewById(R.id.editTextCity);

        builder.setView(dialogView)
                .setTitle("Agregar Ubicación")
                .setNegativeButton("Cancelar", (dialog, id) -> checkBoxUbicacion.setChecked(false))
                .setPositiveButton("Agregar", (dialog, id) -> {
                    String cityName = editTextCity.getText().toString();
                    new GeocodeCityTask().execute(cityName);
                });
        builder.create().show();
    }

    private class GeocodeCityTask extends AsyncTask<String, Void, Address> {
        @Override
        protected Address doInBackground(String... params) {
            String cityName = params[0];
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    return addresses.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                novela.setLatitude(address.getLatitude());
                novela.setLongitude(address.getLongitude());
                preferencesManager.saveNovelas(preferencesManager.loadNovelasSync());
                Toast.makeText(requireContext(), "Ubicación añadida al mapa", Toast.LENGTH_SHORT).show();
                // Notificar al MapaFragment para actualizar el mapa
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).actualizarMapa();
                }
            } else {
                Toast.makeText(requireContext(), "Ciudad no encontrada", Toast.LENGTH_SHORT).show();
                checkBoxUbicacion.setChecked(false);
            }
        }
    }
}