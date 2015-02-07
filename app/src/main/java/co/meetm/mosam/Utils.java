package co.meetm.mosam;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Utils {
    private static final String TAG = Utils.class.getName();

    public static boolean isFirstRun(Context context){
        SharedPreferences pref = getSharedPref(context);
        if(pref.contains(Constants.FIRST_RUN)){
            return false;
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.FIRST_RUN,false);
        editor.apply();
        return true;
    }

    public static void metricUnits(Context context){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(Constants.AMERICAN_UNITS,false);
        editor.apply();
    }

    public static void americanUnits(Context context){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putBoolean(Constants.AMERICAN_UNITS,true);
        editor.apply();
    }

    public static boolean isAmericanUnits(Context context){
        SharedPreferences preferences = getSharedPref(context);
        return preferences.getBoolean(Constants.AMERICAN_UNITS,true);
    }

    private static SharedPreferences getSharedPref(Context context){
        return context.getSharedPreferences(Constants.APP_NAME,Context.MODE_PRIVATE);
    }

    public static double getLatitude(Context context){
        return getSharedPref(context).getFloat(Constants.LATITUDE,Constants.DEFAULT_LAT);
    }

    public static double getLongitude(Context context){
        return getSharedPref(context).getFloat(Constants.LONGITUDE,Constants.DEFAULT_LONGT);
    }

    public static void setLatitude(Context context, double latitude){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putFloat(Constants.LATITUDE, (float) latitude);
        editor.commit();
    }

    public static void setLongitude(Context context, double longitude){
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putFloat(Constants.LONGITUDE,(float)longitude);
        editor.commit();
    }

    public static void storeWeather(Context context, WeatherData data) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putFloat(Constants.TEMPERATURE,(float)data.getTemperature(true));
        editor.putFloat(Constants.MIN_TEMP,(float)data.getMin_temp(true));
        editor.putFloat(Constants.MAX_TEMP,(float)data.getMax_temp(true));
        editor.putInt(Constants.HUMIDITY, data.getHumidity());
        editor.putFloat(Constants.PRESSURE, (float) data.getPressure());
        editor.putFloat(Constants.VISIBILITY, (float) data.getVisibility(true));
        editor.putFloat(Constants.WIND, (float) data.getWind_speed(true));
        editor.putString(Constants.DESCRIPTION,data.getWeather_description());
        editor.putString(Constants.ICON,data.getIcon());
        double[][] temps = data.getForecastArray(true);

        for(int i=0;i<temps.length;i++){
            editor.putFloat(Constants.MIN_TEMP+i, (float) temps[i][0]);
            editor.putFloat(Constants.MAX_TEMP+i, (float) temps[i][1]);
            Log.d(TAG,temps[i][0]+" "+temps[i][1]);
        }
        editor.apply();
    }

    public static String getCity(Context context) {
        return getSharedPref(context).getString(Constants.CITY,"Las Vegas");
    }

    public static void setCity(Context context, String city) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putString(Constants.CITY,city);
        editor.commit();

    }

    public static WeatherData getWeather(Context context){
        SharedPreferences preferences = getSharedPref(context);
        WeatherData weatherData = new WeatherData();
        weatherData.setIcon(preferences.getString(Constants.ICON,"clear-day"));
        weatherData.setTemperature(preferences.getFloat(Constants.TEMPERATURE,40));
        weatherData.setMin_temp(preferences.getFloat(Constants.MIN_TEMP,35));
        weatherData.setMax_temp(preferences.getFloat(Constants.MAX_TEMP,45));
        weatherData.setWind_speed(preferences.getFloat(Constants.WIND,8));
        weatherData.setHumidity(preferences.getInt(Constants.HUMIDITY,20));
        weatherData.setVisibility(preferences.getFloat(Constants.VISIBILITY,6));
        weatherData.setPressure(preferences.getFloat(Constants.PRESSURE,28));
        weatherData.setWeather_description(preferences.getString(Constants.DESCRIPTION,"Clear Sky"));
        double[][] forcast = new double[7][2];
        for (int i=0;i<forcast.length;i++){
            forcast[i][0]=preferences.getFloat(Constants.MIN_TEMP+i,30);
            forcast[i][1]=preferences.getFloat(Constants.MAX_TEMP+i,35);
            Log.d(TAG,"retrieving "+forcast[i][0]+" "+forcast[i][1]);

        }
        weatherData.setForecast(forcast);
        return weatherData;
    }

    public static void storeUpdateTime(Context context, long timeMillis) {
        SharedPreferences.Editor editor = getSharedPref(context).edit();
        editor.putLong(Constants.TIME,timeMillis);
        editor.apply();

    }

    public static boolean refreshNow(Context context){
        long lastUpadate = getSharedPref(context).getLong(Constants.TIME,0);
        long currTime = System.currentTimeMillis();
        Log.d(TAG,lastUpadate+" "+currTime);
        return currTime - lastUpadate > 30000;
    }

}
