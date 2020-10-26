package com.example.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


public class SearchActivity extends AppCompatActivity {
    /*******CLASS DATA*******/

    //Strings used for passing info between activities
    public static final String LAT_NEEDED = "com.example.weatherapp.LAT";
    public static final String LON_NEEDED = "com.example.weatherapp.LON";
    public static final String NAME_NEEDED = "com.example.weatherapp.NAME";
    public static final String TEMP_NEEDED = "com.example.weatherapp.TEMP";
    public static final String FEEL_NEEDED = "com.example.weatherapp.FEEL";
    public static final String WIND_NEEDED = "com.example.weatherapp.WIND";
    public static final String PRESS_NEEDED = "com.example.weatherapp.PRESS";
    public static final String HUMID_NEEDED = "com.example.weatherapp.HUMID";
    public static final String LOW_NEEDED = "com.example.weatherapp.LOW";
    public static final String HIGH_NEEDED = "com.example.weatherapp.HIGH";
    public static final String COUNTRY_NEEDED = "com.example.weatherapp.COUNTRY";

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

    //API URL
    String JsonURL;
    // Defining the Volley request queue
    RequestQueue requestQueue;
    //Weather API key
    String weatherAPIKey = "d357a85c57f1265efcdeed5894a1b40d";

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    // GPSTracker class
    GPSTracker gps;

    //boolean that toggles when using current location
    boolean usingCurrLoc;
    //variables to hold user current lat/long
    double currLat;
    double currLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    /*function called when the map icon is clicked
    retrieves user's current location*/
    public void getCurrLoc(View view){
        //toggle current location boolean
        usingCurrLoc = true;

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
                // If any permission above not allowed by user, this condition will
                //execute every time, else the else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create GPS class object
        gps = new GPSTracker(SearchActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currLat = gps.getLatitude();
            currLon = gps.getLongitude();
            doSearch(view);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    //method called by doSearch
    public void viewData(View view){
        //New intent, links to the Data Activity
        Intent intent = new Intent(this, DataActivity.class);

        //Data to transfer to new activity
        intent.putExtra(LAT_NEEDED, lat);
        intent.putExtra(LON_NEEDED, lon);
        intent.putExtra(NAME_NEEDED, name);
        intent.putExtra(TEMP_NEEDED, temp);
        intent.putExtra(FEEL_NEEDED, feel);
        intent.putExtra(LOW_NEEDED, low);
        intent.putExtra(HIGH_NEEDED, high);
        intent.putExtra(WIND_NEEDED, speed);
        intent.putExtra(PRESS_NEEDED, press);
        intent.putExtra(HUMID_NEEDED, humid);
        intent.putExtra(COUNTRY_NEEDED, country);

        startActivity(intent);
    }

    //converts kelvin to fahrenheit
    public int convert(String str){
        double strD = Double.parseDouble(str);
        strD = ((strD - 273.15) * (1.8)) + 32;
        return (int) strD;
    }

    //method to check if a string is a number
    public static boolean isNum(String test) {
        try {
            Double.parseDouble(test);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    //function connects to weather API, activated when search button is clicked
    //or as a result of the map icon being clicked for current location
    public void doSearch (final View view){
        //obtain user input
        EditText editText = (EditText) findViewById(R.id.userInput);
        String uInput = editText.getText().toString();

        if(usingCurrLoc != true) {
            //if the user is not using their current location, toggle boolean to false
            usingCurrLoc = false;
        }else{
            //if the user is using their current location, clear the user input variable
            //to avoid conflict
            uInput = "";
        }

        if(isNum(uInput)){
            //Configure URL for zip code
            JsonURL = "http://api.openweathermap.org/data/2.5/weather?zip=" + uInput + "&appid=" + weatherAPIKey;
        }else if(usingCurrLoc == true){
            //configure URL for lat/lon (when using current loc
            JsonURL = "https://api.openweathermap.org/data/2.5/weather?lat=" + currLat + "&lon=" + currLon + "&appid=" + weatherAPIKey;
        }else{
            //Configure URL for location name
            JsonURL = "http://api.openweathermap.org/data/2.5/weather?q=" + uInput + "&appid=" + weatherAPIKey;
        }

        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        //open new request with weather API
        JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET, JsonURL, null,
                new Response.Listener<JSONObject>() {

                    // Takes the response from the JSON request
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Access the JSON data
                            JSONObject coord = response.getJSONObject("coord");
                            lon = coord.getString("lon");
                            lat = coord.getString("lat");

                            name = response.getString("name");

                            JSONObject sys = response.getJSONObject("sys");
                            country = sys.getString("country");

                            JSONObject main = response.getJSONObject("main");

                            String tempTxt = main.getString("temp");
                            int tempI = convert(tempTxt);
                            temp = Integer.toString(tempI);
                            String feelTxt = main.getString("feels_like");
                            int feelI = convert(feelTxt);
                            feel = Integer.toString(feelI);
                            String lowTxt = main.getString("temp_min");
                            int lowI = convert(lowTxt);
                            low = Integer.toString(lowI);
                            String highTxt = main.getString("temp_max");
                            int highI = convert(highTxt);
                            high = Integer.toString(highI);

                            press = main.getString("pressure");
                            humid = main.getString("humidity");

                            JSONObject wind = response.getJSONObject("wind");
                            speed = wind.getString("speed");

                            //method call to start data activity
                            viewData(view);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                //if an error occurs
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //display error message (toast)
                        Toast.makeText(getApplicationContext(), "Location not found" , Toast.LENGTH_LONG).show();
                        Log.e("Volley", "Error");
                    }
                }
        );
        //toggle (reset) current location boolean for next search
        usingCurrLoc = false;

        requestQueue.add(weatherReq);
    }

}