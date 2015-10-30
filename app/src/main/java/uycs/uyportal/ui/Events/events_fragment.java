package uycs.uyportal.ui.Events;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uycs.uyportal.R;
import uycs.uyportal.model.Events_Model;
import uycs.uyportal.util.Events.Calendar_Decorators.EventDecorator;
import uycs.uyportal.util.Events.Calendar_Decorators.HighlightWeekendsDecorator;
import uycs.uyportal.util.Events.Calendar_Decorators.OneDayDecorator;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.Events.RecyclerItemClickListener;
import uycs.uyportal.util.ParseConstants;

/**
 * Created by All User on 10/17/2015.
 */
public class events_fragment extends android.support.v4.app.Fragment {

    @Bind(R.id.calendar_MaterialCalendarView)
    MaterialCalendarView calendar_MaterialCalendarView;
    @Bind(R.id.onLoad_view) TextView onLoad_view;
    @Bind(R.id.noEvent_view) TextView noEvent_view;
    @Bind(R.id.event_fragment_Relative_Layout) RelativeLayout event_fragment_Relative_Layout;
    private RecyclerView eventList_RecyclerView;
    private EventList_RecyclerViewAdapter eventList_RecyclerView_Adapter;
    private List<Events_Model> events = new ArrayList<>();

    private List<ParseObject> localObjectList;

    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private int textview_Detail_Color;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.events_fragment, container, false);
        ButterKnife.bind(this, inflatedView);

        //Show icons in action bar of this fragment
        setHasOptionsMenu(true);

         //Initializing Recycler View
        eventList_RecyclerView = (RecyclerView) inflatedView.findViewById(R.id.eventList_RecyclerView);
        eventList_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Clicked on calendar Date
        calendar_MaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {

                //Query from local database if there is NO internet connection
                if (!netConnectionCheck()) {

                    events.clear();

                    //Show Snack bar
                    Snackbar.make(event_fragment_Relative_Layout, "Turn on your internet to get latest events", Snackbar.LENGTH_SHORT).show();


                    // Query from the Local Datastore
                    ParseQuery<ParseObject> localQuery = ParseQuery.getQuery(ParseConstants.CLASS_EVENTS);
                    localQuery.fromLocalDatastore().whereEqualTo(ParseConstants.KEY_EVENT_DATE, "" + date.getDay() + " / " + changeMonthToString(date.getMonth() + 1) + " / " + date.getYear());
                    localQuery.addAscendingOrder(ParseConstants.KEY_EVENT_HOUR);
                    localQuery.addAscendingOrder(ParseConstants.KEY_EVENT_MINUTE);
                    localQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {

                                //No events
                                if (list.size() == 0) {

                                    noEvent_view.setVisibility(View.VISIBLE);
                                    eventList_RecyclerView.setVisibility(View.GONE);
                                    onLoad_view.setVisibility(View.GONE);


                                } else {

                                    //Displaying Events in recycler view
                                    eventList_RecyclerView.setVisibility(View.VISIBLE);
                                    noEvent_view.setVisibility(View.GONE);
                                    onLoad_view.setVisibility(View.GONE);

                                    for (int i = 0; i < list.size(); i++) {
                                        String objectId = list.get(i).getObjectId();
                                        String eventName = list.get(i).get(ParseConstants.KEY_EVENT_NAME).toString();
                                        String eventDate = list.get(i).get(ParseConstants.KEY_EVENT_DATE).toString();
                                        String eventLocation = list.get(i).get(ParseConstants.KEY_EVENT_LOCATION).toString();
                                        String importance = list.get(i).get(ParseConstants.KEY_IMPORTANCE).toString();
                                        int importance_Int = list.get(i).getInt(ParseConstants.KEY_IMPORTANCE_INT);
                                        Events_Model event = new Events_Model(objectId, eventName, eventDate, eventLocation, importance, importance_Int);
                                        events.add(event);
                                    }

                                    localObjectList = list;

                                    eventList_RecyclerView_Adapter = new EventList_RecyclerViewAdapter(getActivity(), (ArrayList<Events_Model>) events);
                                    eventList_RecyclerView.setAdapter(eventList_RecyclerView_Adapter);

                                }
                            }
                        }
                    });
                }
                //Query from Parse Online Database if there is internet connection
                else {

                    events.clear();
                    // Query from the Local Datastore
                    ParseQuery<ParseObject> localQuery = ParseQuery.getQuery(ParseConstants.CLASS_EVENTS);
                    localQuery.fromLocalDatastore().whereEqualTo(ParseConstants.KEY_EVENT_DATE, "" + date.getDay() + " / " + changeMonthToString(date.getMonth() + 1) + " / " + date.getYear());
                    localQuery.addAscendingOrder(ParseConstants.KEY_EVENT_HOUR);
                    localQuery.addAscendingOrder(ParseConstants.KEY_EVENT_MINUTE);
                    localQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            if (e == null) {

                                //No events
                                if (list.size() == 0) {

                                    noEvent_view.setVisibility(View.VISIBLE);
                                    eventList_RecyclerView.setVisibility(View.GONE);
                                    onLoad_view.setVisibility(View.GONE);


                                } else {

                                    //Displaying Events in recycler view
                                    eventList_RecyclerView.setVisibility(View.VISIBLE);
                                    noEvent_view.setVisibility(View.GONE);
                                    onLoad_view.setVisibility(View.GONE);

                                    for (int i = 0; i < list.size(); i++) {
                                        String objectId = list.get(i).getObjectId();
                                        String eventName = list.get(i).get(ParseConstants.KEY_EVENT_NAME).toString();
                                        String eventDate = list.get(i).get(ParseConstants.KEY_EVENT_DATE).toString();
                                        String eventLocation = list.get(i).get(ParseConstants.KEY_EVENT_LOCATION).toString();
                                        String importance = list.get(i).get(ParseConstants.KEY_IMPORTANCE).toString();
                                        int importance_Int = list.get(i).getInt(ParseConstants.KEY_IMPORTANCE_INT);
                                        Events_Model event = new Events_Model(objectId, eventName, eventDate, eventLocation, importance, importance_Int);
                                        events.add(event);
                                    }

                                    localObjectList = list;

                                    eventList_RecyclerView_Adapter = new EventList_RecyclerViewAdapter(getActivity(), (ArrayList<Events_Model>) events);
                                    eventList_RecyclerView.setAdapter(eventList_RecyclerView_Adapter);

                                }
                            }
                        }
                    });
                }
            }
        });


        //Clicked on recyclerview's item
        eventList_RecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent intent = new Intent(getActivity(), Event_Detail_Activity.class);
                        intent.putExtra("event_Name", localObjectList.get(position).get(ParseConstants.KEY_EVENT_NAME).toString());
                        intent.putExtra("event_Location", localObjectList.get(position).get(ParseConstants.KEY_EVENT_LOCATION).toString());
                        intent.putExtra("event_Date", localObjectList.get(position).get(ParseConstants.KEY_EVENT_DATE).toString());
                        intent.putExtra("event_Time", localObjectList.get(position).get(ParseConstants.KEY_EVENT_TIME).toString());
                        intent.putExtra("importance", localObjectList.get(position).get(ParseConstants.KEY_IMPORTANCE).toString());
                        intent.putExtra("related_To", localObjectList.get(position).get(ParseConstants.KEY_RELATED_TO).toString());
                        intent.putExtra("event_Description", localObjectList.get(position).get(ParseConstants.KEY_EVENT_DESCRIPTION).toString());
                        intent.putExtra("textview_Detail_Color", textview_Detail_Color);
                        startActivity(intent);
                    }
                })
        );

        //Setting calendarview's decorators
        calendar_MaterialCalendarView.addDecorators(
                new HighlightWeekendsDecorator(),
                oneDayDecorator
        );

        //Displaying events(dots) in calendar
        getEventsToDisplay();

        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.create_event_item:
                Intent intent = new Intent(getActivity(), Event_DataEntry_Activity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void getEventsToDisplay() {
        final ArrayList<CalendarDay> dates = new ArrayList<>();
        if(!netConnectionCheck()){
            ParseQuery<ParseObject> localQuery = ParseQuery.getQuery(ParseConstants.CLASS_EVENTS);
            localQuery.fromLocalDatastore();
            localQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    //Adding dates to display events
                    for (int i = 0; i < list.size(); i++) {
                        int day = list.get(i).getInt(ParseConstants.KEY_EVENT_DAY);
                        int month = (list.get(i).getInt(ParseConstants.KEY_EVENT_MONTH) - 1);
                        int year = list.get(i).getInt(ParseConstants.KEY_EVENT_YEAR);
                        dates.add(CalendarDay.from(year, month, day));
                    }
                    calendar_MaterialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
                }
            });
        }
        else{

            //Unpinning previous remaining objects
            try {
                ParseObject.unpinAll();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_EVENTS);
            query.whereEqualTo(ParseConstants.KEY_EVENT_CONFIRM, true);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        //Saving in Local Database
                        ParseObject.pinAllInBackground(list);

                        //Adding dates to display events
                        for (int i = 0; i < list.size(); i++) {
                            int day = list.get(i).getInt(ParseConstants.KEY_EVENT_DAY);
                            int month = (list.get(i).getInt(ParseConstants.KEY_EVENT_MONTH) - 1);
                            int year = list.get(i).getInt(ParseConstants.KEY_EVENT_YEAR);
                            dates.add(CalendarDay.from(year, month, day));
                        }
                        calendar_MaterialCalendarView.addDecorator(new EventDecorator(Color.RED, dates));
                    } else {
                        return;
                    }
                }
            });
        }
    }


    private boolean netConnectionCheck() {
        CheckConnection checkConnection = new CheckConnection(getContext());
        return checkConnection.isNetworkAvailable();
    }

    private String changeMonthToString(int m) {

        String month = "";

        switch (m) {
            case 1:month = "January";
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


    public class EventList_RecyclerViewAdapter extends RecyclerView.Adapter<EventList_RecyclerViewAdapter.ViewHolder> {

        private LayoutInflater inflator;
        ArrayList<Events_Model> inside_List;

        public EventList_RecyclerViewAdapter(FragmentActivity activity, ArrayList<Events_Model> eventList) {
            this.inside_List = eventList;
            inflator = LayoutInflater.from(activity);
        }

        @Override
        public EventList_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
            View v = inflator.inflate(R.layout.eventlist_cardview_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Events_Model events = inside_List.get(position);

            String importance = events.getImportance();
            int importance_int = events.getImportance_Int();
            if(importance_int == 1) {
                holder.textview_Color.setBackgroundColor(Color.parseColor("#a3e71518"));
                textview_Detail_Color = Color.parseColor("#a3e71518");
            }
            else if (importance_int == 2){
                holder.textview_Color.setBackgroundColor(Color.parseColor("#e7b315"));
                textview_Detail_Color = Color.parseColor("#e7b315");
            }
            else if(importance_int == 3){
                holder.textview_Color.setBackgroundColor(Color.parseColor("#e74915"));
                textview_Detail_Color = Color.parseColor("#e74915");
            }

            holder.eventName_TextView_CardView.setText("" + events.getEventName() + " (" + importance + ")");
            holder.eventDate_TextView_CardView.setText("" + events.getEventDate());
            holder.eventLocation_TextView_CardView.setText("" + events.getEventLocation());

        }

        @Override
        public int getItemCount() {
            return inside_List.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView eventList_CardView;
            TextView textview_Color;
            TextView eventName_TextView_CardView;
            TextView eventDate_TextView_CardView;
            TextView eventLocation_TextView_CardView;

            public ViewHolder(View v) {
                super(v);
                eventList_CardView = (CardView) v.findViewById(R.id.eventlist_cardview);
                textview_Color = (TextView) v.findViewById(R.id.textview_color);
                eventName_TextView_CardView = (TextView) v.findViewById(R.id.eventName_TextView＿CardView);
                eventDate_TextView_CardView = (TextView) v.findViewById(R.id.eventDate_TextView_CardView);
                eventLocation_TextView_CardView = (TextView) v.findViewById(R.id.eventLocation_TextView_CardView);
            }
        }
    }


}
