package co.meetm.mosam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class ForecastFragment extends Fragment {

    private static final String TAG = ForecastFragment.class.getName();
    private TextView[] mDates;
    private TextView[] mTempLow;
    private TextView[] mTempHigh;

    public ForecastFragment() {
        mDates = new TextView[6];
        mTempLow = new TextView[6];
        mTempHigh = new TextView[6];
    }

    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int dateIds[] = {R.id.date0,R.id.date1,R.id.date2,R.id.date3,R.id.date4,R.id.date5};
        int tempLowIds[] = {R.id.temp_low0,R.id.temp_low1,R.id.temp_low2,R.id.temp_low3,R.id.temp_low4,R.id.temp_low5};
        int tempHighIds[] = {R.id.temp_high0,R.id.temp_high1,R.id.temp_high2,R.id.temp_high3,R.id.temp_high4,R.id.temp_high5};

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forcast, container, false);
        for (int i=0;i<6;i++){
            mDates[i]= (TextView) v.findViewById(dateIds[i]);
            mTempLow[i] = (TextView) v.findViewById(tempLowIds[i]);
            mTempHigh[i] = (TextView) v.findViewById(tempHighIds[i]);
        }
        update_views();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void update_views(){
        Calendar calendar = Calendar.getInstance();
        WeatherData data = Utils.getWeather(getActivity());
        double forcast[][];
        if(Utils.isAmericanUnits(getActivity()))
            forcast = data.getForecastArray(true);
        else
            forcast = data.getForecastArray(false);

        int curDay = calendar.get(Calendar.DAY_OF_WEEK);
        for(int i=1;i<forcast.length;i++){
            Log.i(TAG,"Processing for i="+i);
            mDates[i-1].setText(getDayOfWeek((curDay+i-1)%7));
            calendar.add(Calendar.DATE,1);
            Log.d(TAG,"**"+(int)forcast[i][0]);
            mTempLow[i-1].setText(String.valueOf((int)forcast[i][0]));
            mTempHigh[i-1].setText(String.valueOf((int)forcast[i][1]));
        }
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 0:
                day = "sun";
                break;
            case 1:
                day = "mon";
                break;
            case 2:
                day = "tue";
                break;
            case 3:
                day = "wed";
                break;
            case 4:
                day = "thr";
                break;
            case 5:
                day = "fri";
                break;
            case 6:
                day = "sat";
                break;
        }
        return day;
    }

}