package uycs.uyportal.ui.Events;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import uycs.uyportal.R;

/**
 * Created by All User on 10/26/2015.
 */
public class Event_Detail_Activity extends AppCompatActivity {
    @Bind(R.id.event_Name_Detail_TextView)
    TextView event_Name_Detail_TextView;
    @Bind(R.id.event_Informations_Detail_TextView) TextView event_Information_Detail_Textview;
    @Bind(R.id.textview_detail_color) TextView textview_detail_color;
    @Bind(R.id.event_Description_Detail_TextView) TextView event_Description_Detail_Textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_layout);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        int textview_Detail_Color = intent.getIntExtra("textview_Detail_Color", Color.parseColor("#000000"));
        String event_Name = intent.getStringExtra("event_Name");
        String event_Location = intent.getStringExtra("event_Location");
        String event_Date = intent.getStringExtra("event_Date");
        String event_Time = intent.getStringExtra("event_Time");
        String event_Related_To = intent.getStringExtra("related_To");
        String event_Description = intent.getStringExtra("event_Description");


        textview_detail_color.setBackgroundColor(textview_Detail_Color);
        event_Name_Detail_TextView.setText("" + event_Name);
        event_Information_Detail_Textview.setText("" + "At : " + event_Location + "\n" +
                                                      event_Date + ", " + event_Time + "\n" +
                                                     "Related to : " + event_Related_To + " Deparment" + "\n\n" +
                                                    "Event Description : ");
        event_Description_Detail_Textview.setText("" + event_Description);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
