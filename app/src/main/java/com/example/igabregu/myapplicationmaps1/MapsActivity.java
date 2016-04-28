package com.example.igabregu.myapplicationmaps1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    //Lista donde se almacenaran los Items
    Boolean FLAG_MENSAJE=true;
    private ArrayList listItems;
    protected LocationManager locationManager;
    private String url = "https://redarmyserver.appspot.com/_ah/api/myApi/v1/torretinfocollection";

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker marker;
    // View coordinatorLayoutView = findViewById(R.id.snackbarPosition);

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listItems = new ArrayList();

        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        new consultarWebService().execute();

    }


    private class consultarWebService extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog progres = new ProgressDialog(MapsActivity.this);
        String error;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progres.setMessage("Espere... cargando datos");

            progres.show();

        }


        protected Boolean doInBackground(String... params) {
            boolean resul = true;
            InputStream in = null;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((mobileNetwork != null && mobileNetwork.isConnected()) || (wifiNetwork != null && wifiNetwork.isConnected())) {
                try {
                    in = openHttpConnection(url);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line, aux = "";
                    while ((line = reader.readLine()) != null) {
                        aux = aux + line;
                    }
                    JSONObject json = new JSONObject(aux);
                    //jsonArray con los datos a sacar
                    JSONArray jsArray = json.getJSONArray("items");
                    int i = 0;
                    while (i < jsArray.length()) {
                        json = (JSONObject) jsArray.get(i);
                        listItems.add(new classItem(json));
                        i++;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                error = "Compruebe su conexion de Internet";
            }
            return resul;
        }


        protected void onPostExecute(Boolean result) {
            progres.dismiss();
            classItem it = (classItem) listItems.get(0);
            if (error != null) {
                Toast.makeText(MapsActivity.this, error, Toast.LENGTH_LONG).show();
                //if(error.compareTo("Compruebe su conexion de Internet")==0)
                //  MainActivity.this.finish();
            }
            cargarItemsMaps(listItems);
        }
    }

    public static InputStream openHttpConnection(String urlStr) {
        InputStream in = null;
        int resCode = -1;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    //funcion que carga en el mapa todos los items
    //parametro de entrada ArrayList
    public void cargarItemsMaps(ArrayList list) {
        int i = 0;
        while (i < list.size()) {
            classItem item = (classItem) list.get(i++);
            LatLng lat_lng = new LatLng(item.getLocationLatitude(), item.getLocationLongitude());
            mMap.addMarker(new MarkerOptions().position(lat_lng).title(item.getCode()));
            mMap.addCircle(new CircleOptions()
                            .center(lat_lng)
                            .radius(item.getRadiusInMeter())
                            .fillColor(ContextCompat.getColor(MapsActivity.this, R.color.ORANGE))
            );
            //// TODO: 25/04/2016 buscar metodo que posicione la camara entre varios puntos.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng, 15));
        }

        // Acquire a reference to the system Location Manager

      /*
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);*/
    }


    //////////////////////////////////////////////////////////////////////
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //mLocationRequest.setSmallestDisplacement(0.1F);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //remove previous current location Marker
        if (marker != null){
            marker.remove();
        }

        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude))
                .title("Mi Ubicacion").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 8));
        calcularRango(mLastLocation);
    }
    private void calcularRango(Location miLocation){
        Location locationItem = new Location("");
        classItem item;
        float distancia;
        int i=0;
        int contador=0;
        while (i< listItems.size()){
            item = (classItem) listItems.get(i++);
            locationItem.setLatitude(item.getLocationLatitude());
            locationItem.setLongitude(item.getLocationLongitude());
            distancia = miLocation.distanceTo(locationItem);
            if(distancia<= item.getRadiusInMeter()/2){
                if(FLAG_MENSAJE) {
                    Toast.makeText(MapsActivity.this, "Entraste en el rango del Arma " + item.getCode(), Toast.LENGTH_LONG).show();
                    FLAG_MENSAJE=false;
                }
            }else {
                contador++;
                if(contador==listItems.size()){
                    FLAG_MENSAJE=true;
                }

            }

        }
    }
}
