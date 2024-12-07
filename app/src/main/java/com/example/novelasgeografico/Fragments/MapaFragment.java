package com.example.novelasgeografico.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.novelasgeografico.Almacenamiento.PreferencesManager;
import com.example.novelasgeografico.GestionNovelas.Novela;
import com.example.novelasgeografico.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.List;

//Clase MapaFragment que representa el fragmento que muestra un mapa con la ubicación de las novelas y del usuario
public class MapaFragment extends Fragment {

    //Variables
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        try {
            //Configuramos el contexto de la librería osmdroid
            Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

            mapView = view.findViewById(R.id.map);
            if (mapView != null) {
                //Configuramos el mapa
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                mapView.setMultiTouchControls(true);

                //Añadimos la capa de ubicación
                locationOverlay = new MyLocationNewOverlay(mapView);
                locationOverlay.enableMyLocation();
                locationOverlay.enableFollowLocation();
                mapView.getOverlays().add(locationOverlay);

                //Centramos el mapa en la ubicación actual del usuario
                IMapController mapController = mapView.getController();
                mapController.setZoom(4.0);

                //Si se ha concedido el permiso de ubicación, gesionamos la ubicación del usuario
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location lastKnownLocation = locationOverlay.getLastFix();
                    if (lastKnownLocation != null) {
                        GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        mapController.setCenter(startPoint);

                        //Añadimos al mapa el marcador de la ubicación actual del usuario
                        Marker startMarker = new Marker(mapView);
                        startMarker.setPosition(startPoint);
                        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        startMarker.setTitle("You are here");
                        mapView.getOverlays().add(startMarker);
                    }
                } else {
                    //Si no se ha concedido el permiso de ubicación, solicitarlo
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } else {
                Log.e("MapaFragment", "MapView is null");
            }
        } catch (Exception e) {
            Log.e("MapaFragment", "Error initializing MapView", e);
        }

        new LoadNovelaMarkersTask().execute();

        return view;
    }

    //Metodo para actualizar los marcadores en el mapa
    public void actualizarMarcadores() {
        if (mapView != null) {
            //Limpiamos los marcadores existentes
            mapView.getOverlays().clear();
            mapView.getOverlays().add(locationOverlay);

            //Cargamos y agregamos los nuevos marcadores
            new LoadNovelaMarkersTask().execute();
        }
    }

    //Metodo para gestionar la pausa del fragmento y liberar recursos
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    //Metodo para gestionar la pausa del fragmento y liberar recursos
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    //Clase interna LoadNovelaMarkersTask que extiende AsyncTask y carga los marcadores de las novelas en el mapa en segundo plano
    private class LoadNovelaMarkersTask extends AsyncTask<Void, Void, List<Novela>> {
        @Override
        protected List<Novela> doInBackground(Void... voids) {
            PreferencesManager preferencesManager = new PreferencesManager(requireContext());
            return preferencesManager.loadNovelasSync();
        }

        //Metodo onPostExecute para añadir los marcadores al mapa despues de cargar las novelas
        @Override
        protected void onPostExecute(List<Novela> novelas) {
            if (mapView != null && novelas != null) {
                for (Novela novela : novelas) {
                    if (novela.getLatitude() != 0 && novela.getLongitude() != 0) {
                        //Añadimos al mapa el marcador de la ubicación de la novela
                        GeoPoint point = new GeoPoint(novela.getLatitude(), novela.getLongitude());
                        Marker marker = new Marker(mapView);
                        marker.setPosition(point);
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        marker.setTitle(novela.getTitulo() + " - " + novela.getAutor());
                        mapView.getOverlays().add(marker);
                    }
                }
                mapView.invalidate();
            }
        }
    }
}