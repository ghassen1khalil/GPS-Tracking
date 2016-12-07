package com.sifast.gps.tracking;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context context;
    private static final String TAG = "MainActivity";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private EditText uniqueId;
    private EditText serverURL;
    private EditText minDistance;
    private EditText minFreq;
    private TextView longitude;
    private TextView latitude;
    private TextView status;
    private int STARTED = 0;
    private int index = 0;
    private static final String URL = "content://com.sifast.gps.tracking.GeoLocProvider/geoloc";

    private PositionManager positionManager;
    private DataTransfert dataTransfert;
    private NetworkingManager networkingManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        positionManager = new PositionManager(context);
        dataTransfert = new DataTransfert(context, positionManager);
        networkingManager = new NetworkingManager(this);

        uniqueId = (EditText) findViewById(R.id.uniqueIdEditText);
        serverURL = (EditText) findViewById(R.id.serverUrlEditText);
        minDistance = (EditText) findViewById(R.id.minDistanceEditText);
        minFreq = (EditText) findViewById(R.id.minFreqEditText);

        longitude = (TextView) findViewById(R.id.longitudeTextView);
        latitude = (TextView) findViewById(R.id.latitudeTextView);
        status = (TextView) findViewById(R.id.statusTextView);
        status.setText("Géolocation désactivée");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final Button onBtn = (Button) findViewById(R.id.onButton);
        final Button showRec = (Button) findViewById(R.id.ShowBtn);
        final Button deleteRec = (Button) findViewById(R.id.deleteBtn);

        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STARTED == 0) {
                    STARTED = 1;
                    //start
                    if (EditTextUtil.isNull(uniqueId) || EditTextUtil.isNull(serverURL) || EditTextUtil.isNull(minDistance) || EditTextUtil.isNull(minFreq)) {
                        Toast.makeText(context, "Veuillez vérifier les paramètres entrés", Toast.LENGTH_LONG).show();
                    } else {
                        status.setText("Géolocation activée");
                        onBtn.setText("OFF");
                        locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                longitude.setText(String.valueOf(location.getLongitude()));
                                latitude.setText(String.valueOf(location.getLatitude()));
                                //Envoi au content Provier

                                //TODO: Récupération de l'ID depuis l'IMEI de l'appareil

                                positionManager.addGeoLocRecord(Integer.parseInt(uniqueId.getText().toString()), location);
                                if (networkingManager.isNetworkAvailable() == true) {
                                    dataTransfert.sendGeoLocToServer(serverURL.getText().toString());
                                }
                            }

                            public void onStatusChanged(String provider, int status, Bundle extras) {
                            }

                            public void onProviderEnabled(String provider) {
                            }

                            public void onProviderDisabled(String provider) {
                            }
                        };
                        // Register the listener with the Location Manager to receive location updates
                        try {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Integer.parseInt(minDistance.getText().toString()), Integer.parseInt(minFreq.getText().toString()), locationListener);
                        } catch (SecurityException e) {
                            e.getStackTrace();
                        }
                    }//end
                } else {
                    try {
                        status.setText("Géolocation désactivée");
                        onBtn.setText("ON");
                        STARTED = 0;
                        locationManager.removeUpdates(locationListener);
                        Log.i(TAG, "LISTENER ARRETE");
                    } catch (SecurityException e) {
                        Log.e(TAG, e.getStackTrace().toString());
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, e.getStackTrace().toString());
                    }
                }
            }
        });

        showRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionManager.showAllRecords();
            }
        });

        deleteRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionManager.deleteAllRecords();
            }
        });
    }
}
