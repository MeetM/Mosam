package co.meetm.mosam;

import android.util.Log;

/**
 * Created by Meet on 1/30/2015.
 */
public class WeatherData {

    private static final String TAG = WeatherData.class.getName();
    private double temperature;
    private double min_temp;
    private double max_temp;
    private int humidity;
    private double wind_speed;
    private double pressure;
    private double visibility;
    private String weather_description;
    private double[][] forecast;
    private String icon;

    WeatherData(){
        forecast = new double[7][2];
    }

    public void setForecast(double forecast[][]){
        this.forecast = forecast;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getIcon(){
        return icon;
    }

    public void setWeather_description(String description){
        weather_description = description;
    }

    public String getWeather_description(){
        return weather_description;
    }

    public double[][] getForecastArray(boolean isAmericanUnits){
        double new_forecast_array[][] = new double[forecast.length][2];
        if (isAmericanUnits) {
            for (int i=0;i<forecast.length;i++){
                Log.d(TAG,"x "+forecast[i][0]+" "+forecast[i][1]);
                new_forecast_array[i][0]=forecast[i][0];
                new_forecast_array[i][1]=forecast[i][1];
            }
        } else {
            for (int i = 0; i < forecast.length; i++) {
                new_forecast_array[i][0] = (((forecast[i][0] - 32.0) * 5) / 9);
                new_forecast_array[i][1] = (((forecast[i][1] - 32.0) * 5) / 9);
            }
        }
        return new_forecast_array;
    }

    public double getTemperature(boolean isAmericanUnits) {
        if(isAmericanUnits)
            return temperature;
        return ((temperature-32.0)*5)/9;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getMin_temp(boolean isAmericanUnits) {
        if(isAmericanUnits)
            return min_temp;
        return ((min_temp-32.0)*5)/9;
    }

    public void setMin_temp(double min_temp) {
        this.min_temp = min_temp;
    }

    public double getMax_temp(boolean isAmericanUnits) {
        if(isAmericanUnits)
            return max_temp;
        return ((max_temp-32.0)*5)/9;
    }

    public void setMax_temp(double max_temp) {
        this.max_temp = max_temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = (humidity);
    }

    public double getWind_speed(boolean isAmericanUnits) {
        if(isAmericanUnits){
            return wind_speed;
        }
        return (wind_speed*1.60934);
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getVisibility(boolean isAmericanUnits) {
        if(isAmericanUnits)
            return visibility;
        return (visibility*1.60934);
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

}
