package co.meetm.mosam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getName();

    private final OkHttpClient client = new OkHttpClient();

    // Kept as class variable to call specific functions of fragments that update
    // values on display, through their references stored in adapter class
    private WeatherInfoAdapter adapter;

    // indicates that the user has returned after requesting and probably turning on GPS
    private boolean mGpsRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final VerticalViewPager viewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        adapter = new WeatherInfoAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGpsRequested = false;
        update_location();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(Utils.isAmericanUnits(getApplicationContext())){
            menu.findItem(R.id.unit_convert).setIcon(R.drawable.ic_action_c);
        } else {
            menu.findItem(R.id.unit_convert).setIcon(R.drawable.ic_action_f);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.unit_convert) {
            if (Utils.isAmericanUnits(this)) {
                item.setIcon(R.drawable.ic_action_f);
                Utils.metricUnits(this);
            } else {
                item.setIcon(R.drawable.ic_action_c);
                Utils.americanUnits(this);
            }
            adapter.update_info();

        } else if (id == R.id.action_gps) {
            mGpsRequested = true;
            if(!isNetworkAvailable()){
                Toast.makeText(this,"Network not available",Toast.LENGTH_LONG).show();

            } else
                update_location();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void update_location() {
        if(!isNetworkAvailable())
            return;
        Location location = getLocation();

        //If current location is available, use it; or use the last location stored in SharedPref
        if (location != null) {

            // Location obtained successfully, turning mGpsRequested to false, to avoid
            // future auto-location-update requests
            mGpsRequested = false;

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String city = addresses.get(0).getLocality();
                Utils.setCity(this,city);
                double lat = location.getLatitude();
                double longt  = location.getLongitude();
                Utils.setLatitude(this,lat);
                Utils.setLongitude(this,longt);
                update_weather_data(lat, longt);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

        } else
            update_weather_data(Utils.getLatitude(this), Utils.getLongitude(this));

    }

    private void updateTitle() {
        getSupportActionBar().setTitle(Utils.getCity(this));
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager conMgr = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

    private void update_weather_data(double latitude, double longitude) {
        if(latitude==0.0 || longitude==0.0)
            return;
        Request request = new Request.Builder()
                .url("https://api.forecast.io/" +
                        "forecast/0a20a6143421c739fcf20da5122a817a/" +
                        latitude+","+longitude)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback(){
                @Override
                public void onFailure(Request request, IOException e) {
                    if(Utils.refreshNow(MainActivity.this) || Utils.isFirstRun(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "Network Unavailable!", Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG,"onFailure");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(response.isSuccessful()){
                        WeatherData weatherData = new WeatherData();
                        try {
                            mGpsRequested = false;
                            String res = response.body().string();

                            JSONObject jsonResponse = new JSONObject(res);
                            Log.d(TAG,jsonResponse.getDouble("latitude")+
                                    " "+jsonResponse.getDouble("longitude"));
                            JSONObject currentInfo = jsonResponse.getJSONObject("currently");
                            Log.d(TAG,currentInfo.toString());

                            weatherData.setWeather_description(currentInfo.getString("summary"));
                            weatherData.setIcon(currentInfo.getString("icon"));
                            weatherData.setTemperature(currentInfo.getDouble("temperature"));
                            weatherData.setHumidity((int) (currentInfo.getDouble("humidity") * 100));
                            weatherData.setWind_speed(currentInfo.getDouble("windSpeed"));
                            weatherData.setVisibility(currentInfo.getDouble("visibility"));
                            weatherData.setPressure(currentInfo.getDouble("pressure"));
                            JSONArray weeksData = jsonResponse.getJSONObject("daily").getJSONArray("data");
                            double[][] forcast = new double[weeksData.length()][2];
                            forcast[0][0] = weeksData.getJSONObject(0).getDouble("temperatureMin");
                            forcast[0][1] = weeksData.getJSONObject(0).getDouble("temperatureMax");
                            weatherData.setMin_temp(forcast[0][0]);
                            weatherData.setMax_temp(forcast[0][1]);
                            for (int i = 1; i < weeksData.length(); i++) {
                                JSONObject dailyInfo = weeksData.getJSONObject(i);
                                forcast[i][0] = dailyInfo.getDouble("temperatureMin");
                                forcast[i][1] = dailyInfo.getDouble("temperatureMax");
                            }
                            weatherData.setForecast(forcast);
                        } catch (JSONException e){
                            Log.e(TAG,"JSONEx "+e.getMessage());
                        }
                        Utils.storeWeather(getApplicationContext(), weatherData);
                        Utils.storeUpdateTime(getApplicationContext(),System.currentTimeMillis());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTitle();
                                refreshFragments();
                            }
                        });
                    }
                }
            });
    }

    private void refreshFragments(){
        adapter.update_info();
    }

    public Location getLocation() {
        Location location;
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            // First try to get location from Network Provider
            if (isNetworkEnabled) {
                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        return location;
                    }
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        return location;
                    }
                }

            }

        } catch (Exception e) {
            Log.e(TAG, "Exception in getLocation() : "+e.getMessage());
        }

        // If user had previously requested location through GPS,
        // but didn't turn it on, don't ask him to turn it on again
        if(mGpsRequested)
            showSettingsAlert();
        return null;
    }

    public void showSettingsAlert() {

        // Updating the last update time to indicate that the app tried to get
        // updated weather data
        Utils.storeUpdateTime(getApplicationContext(),System.currentTimeMillis());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("To get weather info of your current location, please enable GPS");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    class WeatherInfoAdapter extends FragmentPagerAdapter{

        private CurrentWeatherInfoFragment current;
        private ForecastFragment forecast;

        public WeatherInfoAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
           current = CurrentWeatherInfoFragment.newInstance();
           forecast = ForecastFragment.newInstance();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position){
                case 0:
                    return current;
                case 1:
                    return forecast;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Calls the respective functions of the two fragments, Current and Forecast,
         * to display the updated / converted values of weather data.
         */
        public void update_info(){
            current.update_views();
            forecast.update_views();
        }
    }
}
