package uycs.uyportal.ui.Events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uycs.uyportal.R;
import uycs.uyportal.ui.NewsFeedActivity;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.ParseConstants;

/**
 * Created by All User on 10/19/2015.
 */
public class Event_DataEntry_Activity extends AppCompatActivity {
    @Bind(R.id.eventName_EditText) EditText eventName_EditText;
    @Bind(R.id.eventLocation_EditText) EditText eventLocation_EditText;
    @Bind(R.id.eventDate_EditText) EditText eventDate_EditText;
    @Bind(R.id.eventTime_EditText) EditText eventTime_EditText;
    @Bind(R.id.importance_RadioGroup) RadioGroup importance_RadioGroup;
    @Bind(R.id.relatedTo_EditText) EditText relatedTo_EditText;
    @Bind(R.id.eventDescription_EditText) EditText eventDescription_EditText;
    @Bind(R.id.requestEvent_Button) Button requestEvent_Button;

    private DatePickerDialog eventDatePickerDialog;
    private TimePickerDialog eventTimePickerDialog;

    private Toolbar toolbar;

    private String event_Name;
    private String event_Location;
    private int year = 0;
    private int month;
    private String month_String;
    private int day;
    private String event_Date;
    private int hour = 25;
    private int displayHour;
    private int minute;
    private String event_Time;
    private String am_pm_String;
    private String importance;
    private int importance_Int;
    private String related_To_String = "All";
    private String event_Description;
    private ParseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_dataentry_layout);
        ButterKnife.bind(this);
        currentUser = ParseUser.getCurrentUser();

        //Making layout fixed when keyboard pops up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setToolbar();


        //Show Date Picker Dialog
        Calendar newCalendar = Calendar.getInstance();
        eventDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int yearOfDate, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(yearOfDate, monthOfYear, dayOfMonth);
                day = dayOfMonth;
                month = monthOfYear + 1;
                year = yearOfDate;
                month_String = changeMonthToString(month);
                if( day >= 0 && day <= 9){
                    eventDate_EditText.setText("" + "0" + day + " / " + month_String + " / " + year);
                }
                else if(day > 9) {
                    eventDate_EditText.setText("" + day + " / " + month_String + " / " + year);
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        eventDatePickerDialog.setTitle("Select Date");

        //Clicked on Event Date EditText
        eventDate_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDatePickerDialog.show();
            }
        });



        //Show Time Picker Dialog
        eventTimePickerDialog = new TimePickerDialog(Event_DataEntry_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                am_pm_String = changeToAMPM(hour);
                if(minute >= 0 && minute <= 9){
                    eventTime_EditText.setText(displayHour + " : " + "0" + minute + " " + am_pm_String);
                }
                else if (minute > 9) {
                    eventTime_EditText.setText(displayHour + " : " + minute + " " + am_pm_String);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        eventTimePickerDialog.setTitle("Select Time");

        //Clicked on Event Time EditText
        eventTime_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventTimePickerDialog.show();
            }
        });



        //Related To list Pop up
        related_To_String = "All(Public)";
        final ListPopupWindow majorListPopup;
        final List<String> majorList = Arrays.asList(getResources().getStringArray(R.array.department_list_event_dataentry));
        majorListPopup = new ListPopupWindow(this);
        majorListPopup.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, majorList));
        majorListPopup.setAnchorView(relatedTo_EditText);
        majorListPopup.setModal(true);
        majorListPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                related_To_String = majorList.get(position);
                relatedTo_EditText.setText(related_To_String);
                majorListPopup.dismiss();
            }
        });

        //Clicked on related to edittext
        relatedTo_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                majorListPopup.show();
            }
        });



        //Clicked on request Button
        requestEvent_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if there is a blank field
                if (!validate()) {
                    return;
                } else {

                        //Ask confirmation with dialog box
                        submitOrNot();

                }
            }
        });
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.event_Dataentry_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    private String changeToAMPM(int h) {
        if(h >= 0 && h < 12){
            displayHour = hour;
            return "A.M";
        }
        else if(h >= 12 && h <= 24){
            displayHour = hour - 12;
            return "P.M";
        }

        return null;
    }

    private boolean validate() {
        boolean valid;

        if (eventName_EditText.getText().toString().trim().isEmpty()) {
            Toast.makeText(getBaseContext(), "Your event must have a name.", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else{
            if(eventLocation_EditText.getText().toString().trim().isEmpty()){
                Toast.makeText(getBaseContext(), "Your event must have a location", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            else{
                if(year == 0){
                    Toast.makeText(getBaseContext(), "Please select a date for event", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                else{
                    if(hour == 25){
                        Toast.makeText(getBaseContext(), "Please set time for the event", Toast.LENGTH_SHORT).show();
                        valid = false;
                    }
                    else{
                        valid = true;
                    }
                }
            }
        }

        return valid;
    }

    private boolean netConnectionCheck() {
        CheckConnection checkConnection = new CheckConnection(Event_DataEntry_Activity.this);
        return checkConnection.isNetworkAvailable();
    }

    private void submitOrNot() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Clicked Yes
                        if(!netConnectionCheck()){
                            Toast.makeText(getBaseContext(), "You have no internet connection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //get values from fields
                            assignValues();
                            //Make the button unclickable
                            requestEvent_Button.setClickable(false);
                            //Send values to parse
                            sendEventDataToParse();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //CLicked No
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Event_DataEntry_Activity.this);
        builder.setMessage("Confirm to submit this event? This request will be sent to the administrators and respective teachers, and when the request has been approved, You will receive a notification.").setPositiveButton("Submit", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }


    private void sendEventDataToParse() {
        ParseObject eventrequest = new ParseObject(ParseConstants.CLASS_EVENTS);
        eventrequest.put(ParseConstants.KEY_EVENT_NAME, event_Name);
        eventrequest.put(ParseConstants.KEY_EVENT_LOCATION, event_Location);
        eventrequest.put(ParseConstants.KEY_EVENT_YEAR, year);
        eventrequest.put(ParseConstants.KEY_EVENT_MONTH, month);
        eventrequest.put(ParseConstants.KEY_EVENT_DAY, day);
        eventrequest.put(ParseConstants.KEY_EVENT_DATE, event_Date);
        eventrequest.put(ParseConstants.KEY_EVENT_TIME, event_Time);
        eventrequest.put(ParseConstants.KEY_EVENT_HOUR, hour);
        eventrequest.put(ParseConstants.KEY_EVENT_MINUTE, minute);
        eventrequest.put(ParseConstants.KEY_IMPORTANCE, importance);
        eventrequest.put(ParseConstants.KEY_IMPORTANCE_INT, importance_Int);
        eventrequest.put(ParseConstants.KEY_RELATED_TO, related_To_String);
        eventrequest.put(ParseConstants.KEY_EVENT_DESCRIPTION, event_Description);
        eventrequest.put(ParseConstants.KEY_CREATED_BY, currentUser.getString("username").toString());
        eventrequest.put(ParseConstants.KEY_EVENT_CONFIRM, false);
        eventrequest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(Event_DataEntry_Activity.this, "Your event request was successfully submitted.", Toast.LENGTH_SHORT).show();
                    goBackToNewsFeed();
                } else {
                    Toast.makeText(Event_DataEntry_Activity.this, "Something went wrong with the event request, Please try again later", Toast.LENGTH_SHORT).show();
                    requestEvent_Button.setClickable(true);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        leaveActivityOrNot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                leaveActivityOrNot();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void leaveActivityOrNot(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Clicked Yes
                        goBackToNewsFeed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //CLicked No

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Event_DataEntry_Activity.this);
        builder.setMessage("Are you sure you want to leave? Your event's information will be discarded.").setPositiveButton("Leave", dialogClickListener)
                .setNegativeButton("Stay", dialogClickListener).show();
    }


    private void goBackToNewsFeed(){
        Intent intent = new Intent(getBaseContext(), NewsFeedActivity.class);
        intent.putExtra("calendar_fragment_id", R.id.navigation_item_2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void assignValues() {
        event_Name = eventName_EditText.getText().toString().trim();
        event_Location = eventLocation_EditText.getText().toString().trim();
        event_Date = eventDate_EditText.getText().toString();
        event_Time = eventTime_EditText.getText().toString();
        RadioButton selectedRadioButton = (RadioButton) findViewById(importance_RadioGroup.getCheckedRadioButtonId());
        if(selectedRadioButton.getId() == R.id.forFun_RadioButton){
            importance_Int = 1;
        }
        else if(selectedRadioButton.getId() == R.id.important_RadioButton){
            importance_Int= 2;
        }
        else if(selectedRadioButton.getId() == R.id.veryImportant_RadioButton){
            importance_Int = 3;
        }
        importance = selectedRadioButton.getText().toString();
        event_Description = eventDescription_EditText.getText().toString().trim();
    }



}

