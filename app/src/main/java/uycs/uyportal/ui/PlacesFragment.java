package uycs.uyportal.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import butterknife.ButterKnife;
import yu.cs.yuwall.R;

public class PlacesFragment extends Fragment {

    private GoogleMap mMap;
    private LatLng NEBOUND =  new LatLng(16.835285, 96.142251);
    private LatLng SWBOUND = new LatLng(16.825524, 96.128848);
    private LatLngBounds MapBoundary = new LatLngBounds(SWBOUND, NEBOUND);
    private LatLng CENTRE = new LatLng(16.828693, 96.135320);
    private Handler MapHandler;
    private int MAX_ZOOM = 20;
    private int MIN_ZOOM = 16;
    private Context mContext;
    public static final String TAG = PlacesFragment.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        ButterKnife.bind(this,rootView);

        mContext = rootView.getContext();
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        initalizeMap();
        setHasOptionsMenu(true);

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
        MarkerOptions k = new MarkerOptions()
                .position(new LatLng(16.82999,96.135))
                .title("Delhi Haat")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_media_pause))
                .draggable(true).snippet("Near INA metro station");
        mMap.addMarker(k);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(CENTRE).zoom(17).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }
    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }


}