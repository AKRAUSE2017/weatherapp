package com.example.weatherapp;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DataActivity extends FragmentActivity implements OnMapReadyCallback {
    /*******CLASS DATA*******/

    private GoogleMap mMap;

    double mapLat;
    double mapLon;

    //TextView objects to display table data
    TextView displayLat;
    TextView displayLon;
    TextView displayTemp;
    TextView displayFeel;
    TextView displayWind;
    TextView displayPress;
    TextView displayHumid;
    TextView displayLow;
    TextView displayHigh;
    TextView displayLoc;
    TextView displayCountry;

    //variables
    String lon;
    String lat;
    String name;
    String temp;
    String feel;
    String low;
    String high;
    String speed;
    String press;
    String humid;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //find XML views by id
        displayLat = (TextView) findViewById(R.id.trLatData);
        displayLon = (TextView) findViewById(R.id.trLonData);
        displayTemp = (TextView) findViewById(R.id.trTempData);
        displayFeel = (TextView) findViewById(R.id.trFeelData);
        displayLow = (TextView) findViewById(R.id.trLowData);
        displayHigh = (TextView) findViewById(R.id.trHighData);
        displayWind = (TextView) findViewById(R.id.trWindData);
        displayPress = (TextView) findViewById(R.id.trPressData);
        displayHumid = (TextView) findViewById(R.id.trHumidData);
        displayLoc = (TextView) findViewById(R.id.locName);
        displayCountry = (TextView) findViewById(R.id.countryText);

        //obtain info from the Search Activity
        Intent intent = getIntent();

        lat = intent.getStringExtra(SearchActivity.LAT_NEEDED);
        lon= intent.getStringExtra(SearchActivity.LON_NEEDED);
        name = intent.getStringExtra(SearchActivity.NAME_NEEDED);
        temp = intent.getStringExtra(SearchActivity.TEMP_NEEDED);
        feel = intent.getStringExtra(SearchActivity.FEEL_NEEDED);
        low = intent.getStringExtra(SearchActivity.LOW_NEEDED);
        high = intent.getStringExtra(SearchActivity.HIGH_NEEDED);
        press= intent.getStringExtra(SearchActivity.PRESS_NEEDED);
        humid = intent.getStringExtra(SearchActivity.HUMID_NEEDED);
        speed = intent.getStringExtra(SearchActivity.WIND_NEEDED);
        country = intent.getStringExtra(SearchActivity.COUNTRY_NEEDED);

        //prepare map coordinates
        mapLat =  Double.parseDouble(lat);
        mapLon = Double.parseDouble(lon);

        lat += "\u00B0";
        lon += "\u00B0";
        temp += "\u00B0F";
        feel += "\u00B0F";
        high += "\u00B0F";
        low += "\u00B0F";
        press += " hPa";
        humid += "%";
        speed += " m/s";

        //change view text to show obtained data
        displayLat.setText(lat);
        displayLon.setText(lon);
        displayTemp.setText(temp);
        displayFeel.setText(feel);
        displayLow.setText(low);
        displayHigh.setText(high);
        displayPress.setText(press);
        displayHumid.setText(humid);
        displayWind.setText(speed);
        displayLoc.setText(name);
        displayCountry.setText(country);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //homeView function called when the back button is clicked
    public void homeView(View view){
        //brings the user back to the home page
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    //function manipulates map once available
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //toast lat/lon coordinates
        Toast.makeText(getApplicationContext(), "Location is - \nLat: "
                + mapLat + "\nLong: " + mapLon, Toast.LENGTH_LONG).show();

        mMap = googleMap;
        // Add a marker to location and move map
        LatLng loc = new LatLng(mapLat, mapLon);
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in " + name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }
}