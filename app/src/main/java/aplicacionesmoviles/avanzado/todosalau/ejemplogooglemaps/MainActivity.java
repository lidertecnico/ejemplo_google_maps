package aplicacionesmoviles.avanzado.todosalau.ejemplogooglemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private boolean isAddingMarker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el proveedor de ubicación fusionada
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtener el fragmento de mapa y registrar el callback cuando esté listo
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Solicitar permiso de ubicación
        requestLocationPermission();

        // Configurar el botón para agregar marcador
        Button btnAddMarker = findViewById(R.id.btnAddMarker);
        btnAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAddingMarker = true;
                Toast.makeText(MainActivity.this, "Toque en el mapa para agregar un marcador.", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar botón para hacer zoom in
        Button btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        // Configurar botón para hacer zoom out
        Button btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        // Configurar botón para cambiar el tipo de mapa
        Button btnChangeMapType = findViewById(R.id.btnChangeMapType);
        btnChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Habilitar la ubicación actual del usuario en el mapa
        enableMyLocation();
        // Obtener la ubicación del dispositivo
        getDeviceLocation();

        // Cambiar el tipo de mapa a híbrido
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Listener para detectar toques en el mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isAddingMarker) {
                    addCustomMarker(latLng);
                    isAddingMarker = false;
                }
            }
        });
    }

    // Método para agregar un marcador personalizado
    private void addCustomMarker(LatLng latLng) {
        LatLng userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        // Agregar un marcador con título, descripción y un ícono personalizado
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marcador Personalizado")
                .snippet("Este es un marcador personalizado con un ícono personalizado.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Dibujar una línea entre la ubicación del usuario y el marcador
        mMap.addPolyline(new PolylineOptions()
                .add(userLatLng, latLng)
                .width(10)
                .color(Color.RED));

        // Dibujar un círculo alrededor del marcador
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(100)
                .strokeColor(Color.BLUE)
                .fillColor(Color.argb(50, 50, 50, 150)));

        // Mostrar información detallada al hacer clic en el marcador
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "Marcador: " + marker.getTitle() + "\n" + marker.getSnippet(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Animar la cámara a la ubicación del marcador
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
    }


    private void enableMyLocation() {
        // Verificar si se ha otorgado el permiso de ubicación y habilitar la capa de ubicación
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }
    }

    // Solicitar permiso de ubicación
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Verificar si se otorgó el permiso de ubicación y habilitar la capa de ubicación
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
                getDeviceLocation();
            }
        }
    }

    // Obtener la ubicación del dispositivo y centrar el mapa en ella
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Agregar un marcador en la ubicación actual del dispositivo
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Estás aquí"));
                            // Mover la cámara al punto actual con un zoom de 15
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        }
                    }
                });
    }
}