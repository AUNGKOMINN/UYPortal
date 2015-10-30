package uycs.uyportal.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import uycs.uyportal.R;
import uycs.uyportal.adapter.postadapter;
import uycs.uyportal.model.posts;
import uycs.uyportal.util.CheckConnection;
import uycs.uyportal.util.EndlessRecyclerOnScrollListener;


public class PlacesFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    @Bind(R.id.fab)
    FloatingActionButton fab_Places;
    @Bind(R.id.Errortextview)
    TextView errorText;
    @Bind(R.id.progressBar)
            ProgressBar bottomloading;
    String username;
    String myname;
    public static String places;
    boolean postable=false;
    private postadapter adapter;
    private String places_array[]={
            "Art Departments"
           , "Art Canteen"
            ,"Girl Hostels"
            ,"Adipadi Road"
            ,"Boy Hostels"
           , "ULB"
            ,"Recreation Centre"
            ,"Central Library"
            ,"Science Canteen"
            ,"Science Departments"
            ,"Student Service Council"
           , "Thit Pote"
            ,"Convocation Hall"
            ,"Dagon_Hostel"
    };
    Marker  Art_Departments
            ,Art_Canteen
            ,Girl_Hostels
            ,Adipadi_Road
            ,Boy_Hostels
            ,ULB
            ,Recreation_Centre
            ,Central_Library
            ,Science_Canteen
            ,Science_Departments
            ,Student_Service_Council
            ,Thit_Pote
            ,Convocation_Hall
            ,Dagon_Hostel;
    private List<Marker> Markerlist =new ArrayList<Marker>();


    private GoogleMap mMap;
    private LatLng NEBOUND =  new LatLng(16.835285, 96.142251);
    private LatLng SWBOUND = new LatLng(16.825524, 96.128848);
    private LatLngBounds MapBoundary = new LatLngBounds(SWBOUND, NEBOUND);
    private LatLng CENTRE = new LatLng(16.828693, 96.135320);
    private Handler MapHandler;
    private int MAX_ZOOM = 20;
    private int MIN_ZOOM = 16;
    public static String current_place;
    public List<posts> mainpost;
    private Context mContext;
    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar bar;
    private SwipeRefreshLayout swipe;
    public static final String TAG = PlacesFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, rootView);
        bottomloading.setVisibility(View.GONE);
        errorText.setVisibility(View.GONE);
        mContext = rootView.getContext();
       username = ParseUser.getCurrentUser().getUsername();
        fab_Places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postable) {
                    postDialog();
                } else
                    Toast.makeText(getActivity(), "choose marker to post", Toast.LENGTH_SHORT).show();
            }
        });

        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.mappostlist);
        linearLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(linearLayoutManager);
       /* bar =(ProgressBar) rootView.findViewById(R.id.progressBar);
        bar.setVisibility(View.GONE);*/
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        initalizeMap();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onrefreshdoing();

            }
        });

        myRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {

                ParseQuery<posts> loadOldpost = ParseQuery.getQuery(posts.class);
                int a=mainpost.size() - 1;
                loadOldpost.whereLessThan("updatedAt", mainpost.get(a).getUpdatedAt());
                loadOldpost.whereEqualTo("to", places);
                loadOldpost.setLimit(15);

                loadOldpost.addDescendingOrder("updatedAt");
                bottomloading.setVisibility(View.VISIBLE);
                loadOldpost.findInBackground(new FindCallback<posts>() {

                    @Override
                    public void done(List<posts> list, ParseException e)  {

                        if (e == null) {

                            if(!list.isEmpty()) {


                                int mainpostnum=mainpost.size();
                                int newpostnum=list.size();
                                for(int i=0,position=mainpostnum-1;i<newpostnum && position < mainpostnum+newpostnum;i++,position++) {

                                    mainpost.add(position+1, list.get(i));
                                }
                                adapter.notifyItemRangeInserted(mainpost.size() - 1, list.size());

                                adapter.notifyDataSetChanged();
                                bottomloading.setVisibility(View.GONE);
                            }
                            if(list.isEmpty()){
                                Toast.makeText(getActivity(),"no data", Toast.LENGTH_SHORT).show();
                                bottomloading.setVisibility(View.GONE);
                            }

                        } else {
                            Log.e("errror", e.toString());
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.GONE);
                        }
                    }

                });
            }

        });
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUpHandler();
        MapHandler.sendEmptyMessageDelayed(0, 50);
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpHandler() {
        MapHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                fixMapBounds();
                sendEmptyMessageDelayed(0, 50);
            }
        };
    }

    private void fixMapBounds() {
        CameraPosition position = mMap.getCameraPosition();
        VisibleRegion region = mMap.getProjection().getVisibleRegion();
        float zoom = 0;
        if (position.zoom < MIN_ZOOM) zoom = MIN_ZOOM;
        if (position.zoom > MAX_ZOOM) zoom = MAX_ZOOM;
        LatLng correction = correctLatLng(region.latLngBounds);
        if (zoom != 0 || correction.latitude != 0 || correction.longitude != 0) {
            zoom = (zoom==0)? position.zoom:zoom;
            if (zoom == 0) zoom = position.zoom;
            double lat = position.target.latitude + correction.latitude;
            double lon = position.target.longitude + correction.longitude;
            CameraPosition newPosition = new CameraPosition(new LatLng(lat, lon), zoom, position.tilt, position.bearing);
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(newPosition);
            mMap.moveCamera(update);
        }
    }

    private LatLng correctLatLng(LatLngBounds cameraBounds) 	{
        double latitude = 0;
        double longitude = 0;
        if (cameraBounds.southwest.latitude < MapBoundary.southwest.latitude) {
            latitude = MapBoundary.southwest.latitude - cameraBounds.southwest.latitude;
        }
        if(cameraBounds.southwest.longitude < MapBoundary.southwest.longitude) {
            longitude = MapBoundary.southwest.longitude - cameraBounds.southwest.longitude;
        }
        if(cameraBounds.northeast.latitude > MapBoundary.northeast.latitude) {
            latitude = MapBoundary.northeast.latitude - cameraBounds.northeast.latitude;
        }
        if(cameraBounds.northeast.longitude > MapBoundary.northeast.longitude) {
            longitude = MapBoundary.northeast.longitude - cameraBounds.northeast.longitude;
        }
        return new LatLng(latitude, longitude);
    }


    private void initalizeMap() {

        //main places

        MarkerOptions Adipadi = new MarkerOptions()
                .position(new LatLng(16.82999, 96.135)).title("road")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.road_icon));

      Adipadi_Road = mMap.addMarker(Adipadi);


        MarkerOptions convocation = new MarkerOptions()
                .position(new LatLng(16.833418, 96.1363163)).title("convocation")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.convo_icon));
      Convocation_Hall=  mMap.addMarker(convocation);

        MarkerOptions Thit_pote = new MarkerOptions()
                .position(new LatLng(16.8321626, 96.1360843)).title("thit_pote")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.thit_pote_icon));
      Thit_Pote=  mMap.addMarker(Thit_pote);

        MarkerOptions RC = new MarkerOptions()
                .position(new LatLng(16.8312409, 96.1352018)).title("RC")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.rc_icon));
       Recreation_Centre= mMap.addMarker(RC);

        MarkerOptions ssc = new MarkerOptions()
                .position(new LatLng(16.8318096, 96.1367441)).title("SSC")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ssc_icon));
        Student_Service_Council=mMap.addMarker(ssc);

        MarkerOptions libaray = new MarkerOptions()
                .position(new LatLng(16.8308038, 96.1359569)).title("library")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.library_icon))
             ;
        Central_Library=mMap.addMarker(libaray);

        mMap.setOnMarkerClickListener(this);


        //departments
        MarkerOptions science_de = new MarkerOptions()
                .position(new LatLng(16.8313333, 96.136432)).title("Science Departments")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.department_icon));
        Science_Departments=mMap.addMarker(science_de);

        MarkerOptions art_de = new MarkerOptions()
                .position(new LatLng(16.8275838, 96.132885)).title("Art Departments")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.department_icon))
                ;
        Art_Departments=mMap.addMarker(art_de);

        MarkerOptions ulb = new MarkerOptions()
                .position(new LatLng(16.8279034, 96.1372698)).title("ULB")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.department_icon));
       ULB= mMap.addMarker(ulb);

        //canteen
        MarkerOptions science_canteen = new MarkerOptions().title("science canteen")
                .position(new LatLng(16.8304489, 96.1364122))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.canteen_icon));
      Science_Canteen= mMap.addMarker(science_canteen);


        MarkerOptions art_canteen = new MarkerOptions()
                .position(new LatLng(16.8280504, 96.1337347)).title("art canteen")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.canteen_icon));
        Art_Canteen = mMap.addMarker(art_canteen);

        MarkerOptions girl_hostel = new MarkerOptions()
                .position(new LatLng(16.830775, 96.1385848)).title("girl hostel")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hostel_icon));
        Girl_Hostels = mMap.addMarker(girl_hostel);

        MarkerOptions boy_hostel = new MarkerOptions()
                .position(new LatLng(16.8289355, 96.1361205)).title("boys hostel")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hostel_icon));
      Boy_Hostels= mMap.addMarker(boy_hostel);


        MarkerOptions dg_hostel = new MarkerOptions()
                .position(new LatLng(16.828938, 96.1337078)).title("DG hostel")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.hostel_icon));
       Dagon_Hostel= mMap.addMarker(dg_hostel);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(SWBOUND).zoom(17).tilt(50).build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Markerlist.add(Art_Departments);
        Markerlist.add(Art_Canteen);
        Markerlist.add(Girl_Hostels);
        Markerlist.add(Adipadi_Road);
        Markerlist.add(Boy_Hostels);
        Markerlist.add(ULB);
        Markerlist.add(Recreation_Centre);
        Markerlist.add(Central_Library);
        Markerlist.add(Science_Canteen);
        Markerlist.add(Science_Departments);
        Markerlist.add(Student_Service_Council);
        Markerlist.add(Thit_Pote);
        Markerlist.add(Convocation_Hall);
        Markerlist.add(Dagon_Hostel);
        }


    private boolean CheckingConnection(){
        CheckConnection checkConnection=new CheckConnection(mContext);
        return checkConnection.isNetworkAvailable();
    }
    private void queryForMainpost(){
        if(CheckingConnection()) {

            ParseQuery<posts> MainpostQuery = ParseQuery.getQuery(posts.class);
            MainpostQuery.addDescendingOrder("updatedAt");
            MainpostQuery.whereEqualTo("to", places);
            MainpostQuery.setLimit(6);
            MainpostQuery.whereEqualTo("to", places);
            MainpostQuery.findInBackground(new FindCallback<posts>() {
                @Override
                public void done(List<posts> list, ParseException e) {

                    if (e == null) {
                        if (!list.isEmpty()) {

                            mainpost = list;
                            adapter = new postadapter(mainpost);
                            myRecyclerView.setAdapter(adapter);

                        } else {

                            Toast.makeText(getActivity(), "no post yet try to post something new :D", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Log.e("errror", e.toString());
                        Toast.makeText(getActivity(), "Error occur why downloading newpost", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_SHORT).show();

    }

    private void postDialog(){

        final AlertDialog builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialog).create();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final View post_dialog= layoutInflater.inflate(R.layout.postdialogbox, null);
        final EditText editText=(EditText) post_dialog.findViewById(R.id.statustext);
        final TextInputLayout ErrorTxtlayout=(TextInputLayout) post_dialog.findViewById(R.id.errorforstatus);
        final ImageView anonymousOff =(ImageView) post_dialog.findViewById(R.id.btn_anonymousOff);
        final ImageView anonymousOn=(ImageView) post_dialog.findViewById(R.id.btn_anonymousOn);


        anonymousOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anonymousOff.setVisibility(View.GONE);
                anonymousOn.setVisibility(View.VISIBLE);
                myname="Anonymous";

            }
        });

        anonymousOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anonymousOn.setVisibility(View.GONE);
                anonymousOff.setVisibility(View.VISIBLE);
                myname=Signup_Activity.username;
            }
        });

        builder.setView(post_dialog);
        builder.setTitle(places);
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.dismiss();
            }
        });
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String status = editText.getText().toString();
                if (!status.isEmpty()) {
                    posts post = new posts();
                    post.setUsername(myname);
                    post.setFromUserid(ParseUser.getCurrentUser());
                    post.setToPlace(places);
                    post.setStatus(status);
                    post.saveInBackground();
                } else
                    ErrorTxtlayout.setError("This cannot be empty");


            }
        });
        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.dismiss();
            }
        });
        builder.show();


    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        for(int i=0;i<places_array.length;i++){
            if (marker.equals(Markerlist.get(i))){
                postable = true;
             places = places_array[i];
                queryForMainpost();

            }
        }

         return false;

    }

    public void onrefreshdoing() {

        final ParseQuery<posts> refreshpost = ParseQuery.getQuery(posts.class);
        if(mainpost!=null) {
            refreshpost.addDescendingOrder("updatedAt");
            refreshpost.whereGreaterThan("updatedAt", mainpost.get(0).getUpdatedAt());
            refreshpost.whereEqualTo("to", places);
            refreshpost.findInBackground(new FindCallback<posts>() {

                @Override
                public void done(List<posts> list, ParseException e) {

                    if (e == null) {

                        if (!list.isEmpty()) {
                            for (int i = 0; i < list.size(); i++) {
                                mainpost.add(i, list.get(i));
                                adapter.notifyItemRangeInserted(0, list.size());
                                adapter.notifyDataSetChanged();
                                onrefreshcomplete(false);
                            }

                        } else if (list.isEmpty()) {
                            Toast.makeText(getActivity(), "no new data", Toast.LENGTH_SHORT).show();
                            onrefreshcomplete(false);
                        }

                    } else {
                        Log.e("errror", e.toString());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        onrefreshcomplete(false);
                    }

                }


            });
        }else if(mainpost==null){
            queryForMainpost();
            onrefreshcomplete(false);
        }
    }
    public void onrefreshcomplete(boolean ok) {

        swipe.setRefreshing(ok);
    }
}