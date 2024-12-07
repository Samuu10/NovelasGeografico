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

        //Inflamos el layout para este fragmento e instanciamos los elementos de la vista
        View view = inflater.inflate(R.layout.fragment_detalles_novela, container, false);
        TextView textViewTitulo = view.findViewById(R.id.textViewTituloDetalle);
        TextView textViewAutor = view.findViewById(R.id.textViewAutorDetalle);
        TextView textViewAño = view.findViewById(R.id.textViewAñoDetalle);
        TextView textViewSinopsis = view.findViewById(R.id.textViewSinopsisDetalle);
        CheckBox checkBoxFavorito = view.findViewById(R.id.checkbox_favorito);
        checkBoxUbicacion = view.findViewById(R.id.checkbox_ubicacion);

        //Instanciamos el PreferencesManager
        preferencesManager = new PreferencesManager(requireContext());

        //Obtenemos la novela seleccionada y mostramos sus detalles
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
                mostrarDialogoUbicacion();
            } else {
                novela.setLatitude(0);
                novela.setLongitude(0);
                preferencesManager.saveNovelas(preferencesManager.loadNovelasSync());
                Toast.makeText(requireContext(), "Ubicación eliminada", Toast.LENGTH_SHORT).show();
            }
        });

        //Botón para volver a la lista de novelas
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

    //Metodo para mostrar un dialogo para añadir una ubicación a la novela
    private void mostrarDialogoUbicacion() {
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

    //Clase interna GeocodeCityTask que extiende AsyncTask y geocodifica una ciudad a coordenadas en segundo plano
    private class GeocodeCityTask extends AsyncTask<String, Void, Address> {
        @Override
        protected Address doInBackground(String... params) {
            String cityName = params[0];
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                //Obtenemos las coordenadas de la ciudad introducida por el usuario
                List<Address> addresses = geocoder.getFromLocationName(cityName, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    return addresses.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        //Metodo onPostExecute para añadir las coordenadas a la novela despues de geocodificar la ciudad
        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                //Establecemos las coordenadas de la novela
                novela.setLatitude(address.getLatitude());
                novela.setLongitude(address.getLongitude());
                //Guardamos las novelas actualizadas en sharedPreferences
                preferencesManager.loadNovelas(loadedNovelas -> {
                    for (Novela n : loadedNovelas) {
                        if (n.equals(novela)) {
                            n.setLatitude(novela.getLatitude());
                            n.setLongitude(novela.getLongitude());
                            break;
                        }
                    }
                    preferencesManager.saveNovelas(loadedNovelas);
                    Toast.makeText(requireContext(), "Ubicación añadida", Toast.LENGTH_SHORT).show();
                    //Actualizamos el mapa
                    ((MainActivity) requireActivity()).actualizarMapa();
                });
            } else {
                Toast.makeText(requireContext(), "No se pudo encontrar la ubicación", Toast.LENGTH_SHORT).show();
                checkBoxUbicacion.setChecked(false);
            }
        }
    }
}