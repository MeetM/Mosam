package co.meetm.mosam;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrentWeatherInfoFragment extends Fragment {

    private TextView mWeatherDescription;
    private TextView mTemp;
    private TextView mMinTemp;
    private TextView mMaxTemp;
    private TextView mHumidity;
    private TextView mPressure;
    private TextView mWind;
    private TextView mVisibility;
    private TextView mTempUnit;
    private ImageView mWeatherIcon;

    public void update_views(){
        Context context = getActivity();
        WeatherData data = Utils.getWeather(context);
        mWeatherDescription.setText(data.getWeather_description());
        mHumidity.setText(data.getHumidity()+"%");
        mPressure.setText((int)(data.getPressure()*29.9213/1013.25)+" Hg");
        String icon = data.getIcon();
        int icon_id;
        switch (icon) {
            case "clear-day":
                icon_id = R.drawable.clear_day;
                break;
            case "clear-night":
                icon_id = R.drawable.clear_night;
                break;
            case "cloudy":
                icon_id = R.drawable.cloudy;
                break;
            case "rain":
                icon_id = R.drawable.rain;
                break;
            case "snow":
                icon_id = R.drawable.snow;
                break;
            case "sleet":
                icon_id = R.drawable.sleet;
                break;
            case "wind":
                icon_id = R.drawable.wind;
                break;
            case "fog":
                icon_id = R.drawable.fog;
                break;
            case "partly-cloudy-day":
                icon_id = R.drawable.partly_cloudy_day;
                break;
            case "partly-cloudy-night":
                icon_id = R.drawable.partly_cloudy_night;
                break;
            case "hail":
                icon_id = R.drawable.hail;
                break;
            case "thunderstrom":
                icon_id = R.drawable.thunderstorm;
                break;
            case "tornado":
                icon_id = R.drawable.tornado;
                break;
            default:
                icon_id = R.drawable.cloudy;
        }

        mWeatherIcon.setImageResource(icon_id);
        if(Utils.isAmericanUnits(context)){
            mTempUnit.setText("°F");
            mWind.setText((int)data.getWind_speed(true)+" mph");
            mTemp.setText((int)data.getTemperature(true)+"");
            mMinTemp.setText("↓"+(int)data.getMin_temp(true)+"");
            mMaxTemp.setText("↑"+(int)data.getMax_temp(true)+"");
            mVisibility.setText((int)data.getVisibility(true)+" mi");
        } else {
            mTempUnit.setText("°C");
            mWind.setText((int)data.getWind_speed(false)+" kmph");
            mTemp.setText((int)data.getTemperature(false)+"");
            mMinTemp.setText("↓"+(int)data.getMin_temp(false)+"");
            mMaxTemp.setText("↑"+(int)data.getMax_temp(false)+"");
            mVisibility.setText((int)data.getVisibility(false)+" km");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update_views();
    }

    public static CurrentWeatherInfoFragment newInstance() {
        return new CurrentWeatherInfoFragment();
    }

    public CurrentWeatherInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_info, container, false);
        mWeatherDescription = (TextView) view.findViewById(R.id.weather_description);
        mWeatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
        mHumidity = (TextView) view.findViewById(R.id.humidity);
        mMaxTemp = (TextView) view.findViewById(R.id.current_high);
        mMinTemp = (TextView) view.findViewById(R.id.current_low);
        mTemp = (TextView) view.findViewById(R.id.current_temp);
        mPressure = (TextView) view.findViewById(R.id.pressure);
        mWind = (TextView) view.findViewById(R.id.wind_speed);
        mVisibility = (TextView) view.findViewById(R.id.visibility);
        mTempUnit = (TextView) view.findViewById(R.id.temp_unit);
        return view;
    }

}
