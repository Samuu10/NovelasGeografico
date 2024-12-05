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

public class MapaFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        try {
            Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

            mapView = view.findViewById(R.id.map);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);

            locationOverlay = new MyLocationNewOverlay(mapView);
            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation();
            mapView.getOverlays().add(locationOverlay);

            IMapController mapController = mapView.getController();
            mapController.setZoom(4.0);

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationOverlay.getLastFix();
                if (lastKnownLocation != null) {
                    GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mapController.setCenter(startPoint);

                    // Add a marker for the user's location
                    Marker startMarker = new Marker(mapView);
                    startMarker.setPosition(startPoint);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    startMarker.setTitle("You are here");
                    mapView.getOverlays().add(startMarker);
                }
            } else {
                // Request location permission if not granted
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } catch (Exception e) {
            Log.e("MapaFragment", "Error initializing MapView", e);
        }

        new LoadNovelaMarkersTask().execute();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    private class LoadNovelaMarkersTask extends AsyncTask<Void, Void, List<Novela>> {
        @Override
        protected List<Novela> doInBackground(Void... voids) {
            PreferencesManager preferencesManager = new PreferencesManager(requireContext());
            return preferencesManager.loadNovelasSync();
        }

        @Override
        protected void onPostExecute(List<Novela> novelas) {
            for (Novela novela : novelas) {
                if (novela.getLatitude() != 0 && novela.getLongitude() != 0) {
                    GeoPoint point = new GeoPoint(novela.getLatitude(), novela.getLongitude());
                    Marker marker = new Marker(mapView);
                    marker.setPosition(point);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setTitle(novela.getTitulo() + " - " + novela.getAutor());
                    mapView.getOverlays().add(marker);
                }
            }
        }
    }
}