package uycs.uyportal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import yu.cs.yuwall.R;


/**
 * Created by All User on 10/17/2015.
 */
public class Events_Fragment extends android.support.v4.app.Fragment {

    @Bind(R.id.createEvent_TextView)
    TextView createEvent_TextView;
    @Bind(R.id.calendar_MaterialCalendarView)
    MaterialCalendarView calendar_MaterialCalendarView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.events_fragment, container, false);
        ButterKnife.bind(this, inflatedView);

        //Clicking on create event button
        createEvent_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Event_DataEntry_Activity.class);
                startActivity(intent);
            }
        });

        //Clicking on calendar
        calendar_MaterialCalendarView.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView widget, CalendarDay date) {
                //Toast.makeText(getActivity(), "" + date.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "" + date.getYear() + " / " + changeMonthToString(date.getMonth() + 1) + " / " + date.getDay(), Toast.LENGTH_SHORT).show();
            }
        });

        return inflatedView;
    }


    private String changeMonthToString(int m) {
        String month = "";
        switch (m) {
            case 1:
                month = "January";
                break;
            case 2:
                month = "February";
                break;
            case 3:
                month = "March";
                break;
            case 4:
                month = "April";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "June";
                break;
            case 7:
                month = "July";
                break;
            case 8:
                month = "August";
                break;
            case 9:
                month = "September";
                break;
            case 10:
                month = "October";
                break;
            case 11:
                month = "November";
                break;
            case 12:
                month = "December";
                break;
            default:
                break;
        }

        return month;
    }


}
