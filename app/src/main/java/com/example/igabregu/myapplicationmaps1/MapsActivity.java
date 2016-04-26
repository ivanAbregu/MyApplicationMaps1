package com.example.igabregu.myapplicationmaps1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //Lista donde se almacenaran los Items
    private ArrayList listItems;
    private String url= "https://redarmyserver.appspot.com/_ah/api/myApi/v1/torretinfocollection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listItems= new ArrayList();
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

    private class  consultarWebService extends AsyncTask<String, Integer, Boolean> {
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
                    String line,aux="";
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
            classItem it = (classItem)listItems.get(0);
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
    public void cargarItemsMaps(ArrayList list){
        int i=0;
        while(i<list.size()){
            classItem item = (classItem)list.get(i++);
            LatLng lat_lng = new LatLng( item.getLocationLatitude(),item.getLocationLongitude());
            mMap.addMarker(new MarkerOptions().position(lat_lng).title(item.getCode()));
            mMap.addCircle(new CircleOptions()
                            .center(lat_lng)
                            .radius(item.getRadiusInMeter())
                            .fillColor(ContextCompat.getColor(MapsActivity.this, R.color.ORANGE))
            );
            //// TODO: 25/04/2016 buscar metodo que posicione la camara entre varios puntos.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng,15));
        }
    }
}
