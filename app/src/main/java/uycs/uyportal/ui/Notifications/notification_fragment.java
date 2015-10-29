package uycs.uyportal.ui.Notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import yu.cs.yuwall.R;
import yu.cs.yuwall.ui.Newsfeed_Module.Events.Event_DataEntry_Activity;
import yu.cs.yuwall.ui.Newsfeed_Module.Events.Event_Detail_Activity;
import yu.cs.yuwall.utils.CheckConnection;
import yu.cs.yuwall.utils.Object_Models.Events_Model;
import yu.cs.yuwall.utils.Object_Models.Notifications_Model;
import yu.cs.yuwall.utils.Parse.ParseConstants;
import yu.cs.yuwall.utils.RecyclerItemClickListener;

/**
 * Created by All User on 10/29/2015.
 */
public class notification_fragment extends android.support.v4.app.Fragment {
    @Bind(R.id.notification_RecyclerView)
    RecyclerView notifcation_RecyclerView;
    @Bind(R.id.notification_fragment_linear_layout) LinearLayout notification_fragment_linear_layout;

    private ParseUser currentUser;
    String notification_Type;
    int notification_Type_Int;
    String from_User;

    private Notification_RecyclerViewAdapter notification_RecyclerView_Adapter;
    private List<Notifications_Model> notifications = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.notification_fragment, container, false);
        ButterKnife.bind(this, inflatedView);

        //Show icons in action bar of this fragment
        setHasOptionsMenu(true);

        currentUser = ParseUser.getCurrentUser();

        notifcation_RecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Clicked on recyclerview's item
        notifcation_RecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Intent intent = new Intent(getContext(), Event_Detail_Activity.class);
                        startActivity(intent);
                    }
                })
        );

        return inflatedView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notification_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.notification_refresh__item:
                getNotifications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void getNotifications() {

        //Unpinning previous remaining objects
        try {
            ParseObject.unpinAll();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(netConnectionCheck()) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
            query.whereEqualTo(ParseConstants.KEY_TO_USER, currentUser.getUsername());
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        //Saving in Local Database
                        ParseObject.pinAllInBackground(list);

                        //Adding dates to display events
                        for (int i = 0; i < list.size(); i++) {
                            notification_Type = list.get(i).getString(ParseConstants.KEY_NOTIFICATION_TYPE);
                            from_User = list.get(i).getString(ParseConstants.KEY_FROM_USER);
                            Notifications_Model notification = new Notifications_Model(notification_Type, from_User);
                            notifications.add(notification);
                        }

                        notification_RecyclerView_Adapter = new Notification_RecyclerViewAdapter(getActivity(), (ArrayList<Notifications_Model>) notifications);
                        notifcation_RecyclerView.setAdapter(notification_RecyclerView_Adapter);
                    } else {
                        return;
                    }
                }
            });
        }
        else{
            Snackbar.make(notification_fragment_linear_layout, "Cannot connect to internet", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean netConnectionCheck() {
        CheckConnection checkConnection = new CheckConnection(getContext());
        return checkConnection.isNetworkAvailable();
    }




    public class Notification_RecyclerViewAdapter extends RecyclerView.Adapter<Notification_RecyclerViewAdapter.ViewHolder> {

        private LayoutInflater inflator;
        ArrayList<Notifications_Model> inside_List;

        public Notification_RecyclerViewAdapter(Context activity, ArrayList<Notifications_Model> notifications) {
            this.inside_List = notifications;
            inflator = LayoutInflater.from(activity);
        }

        @Override
        public Notification_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
            View v = inflator.inflate(R.layout.notification_listview_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Notifications_Model notifications = inside_List.get(position);
        }

        @Override
        public int getItemCount() {
            return inside_List.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {


            public ViewHolder(View v) {
                super(v);

            }
        }
    }
}
