package com.example.easydiary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CreateFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback, DatePickerDialog.OnDateSetListener {

    private final int REQUEST_IMAGE_CAPTURE = 1001;
    private static int REQUEST_LOCATION = 1;
    private String mode;
    private int id;

    //text
    private EditText txtTitle;
    private EditText txtDesc;
    private TextView lblDate;
    private String title;
    private String desc;
    private String completeDate;
    private int date;
    private int month;
    private int year;

    //images
    private ImageView imgCancel;
    private ImageView imgCamera;
    private byte[] image;

    //map
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double lat;
    private double lng;
    private double oldLat;
    private double oldLng;

    //button
    private Button btnSubmit;

    public CreateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        lblDate = view.findViewById(R.id.lblDate);
        txtTitle = view.findViewById(R.id.txtTitle);
        txtDesc = view.findViewById(R.id.txtDesc);
        imgCancel = view.findViewById(R.id.imgCancel);
        imgCamera = view.findViewById(R.id.imgCamera);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        mode = "create";
        Bundle bundle = getArguments();
        if(bundle != null){
            mode = bundle.getString("mode");
            id = bundle.getInt("diaryID");
            title = bundle.getString("diaryTitle");
            desc = bundle.getString("diaryDesc");
            completeDate = bundle.getString("diaryDate");
            image = bundle.getByteArray("diaryImage");
            oldLat = bundle.getDouble("diaryLat");
            oldLng = bundle.getDouble("diaryLng");

            txtTitle.setText(title);
            txtDesc.setText(desc);
            year = Integer.parseInt(completeDate.substring(0,4));
            month = (Integer.parseInt(completeDate.substring(4,6)) - 1);
            date = Integer.parseInt(completeDate.substring(6,8));
            lblDate.setText(year+"-"+(month+1)+"-"+date);
            lblDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                }
            });
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            imgCamera.setImageBitmap(bitmap);

            btnSubmit.setText(getResources().getString(R.string.diary_update));
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    title = txtTitle.getText().toString();
                    desc = txtDesc.getText().toString();
                    if(title == null || title.equals("")){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_title), Toast.LENGTH_SHORT).show();
                    }else if(desc == null || desc.equals("")){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_desc), Toast.LENGTH_SHORT).show();
                    }else if(image == null){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_image), Toast.LENGTH_SHORT).show();
                    }else{
                        updateDiary();
                    }
                }
            });
        }else{
            //text
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            completeDate = "" + year;
            if(month < 10){ completeDate += "0" + month; }else{ completeDate += month; }
            if(date < 10){ completeDate += "0" + date; }else{ completeDate += date; }
            lblDate.setText(year + "-" + (month+1) + "-" + date);
            lblDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker();
                }
            });

            //image
            imgCamera.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            });
            imgCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    image = null;
                    imgCamera.setImageResource(R.drawable.baseline_add_a_photo_black_24dp);
                }
            });

            //submit
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    title = txtTitle.getText().toString();
                    desc = txtDesc.getText().toString();
                    if(title == null || title.equals("")){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_title), Toast.LENGTH_SHORT).show();
                    }else if(desc == null || desc.equals("")){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_desc), Toast.LENGTH_SHORT).show();
                    }else if(image == null){
                        Toast.makeText(getActivity(), getResources().getText(R.string.message_no_image), Toast.LENGTH_SHORT).show();
                    }else{
                        addDiary();
                    }
                }
            });
        }

        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return view;
    }

    //==================================================================================================================
    //--- date picker
    //==================================================================================================================
    //https://www.youtube.com/watch?v=AdTzD96AhE0
    private void showDatePicker(){
        DatePickerDialog datePicker = new DatePickerDialog(
                getActivity(),
                this,
                year,
                month,
                date);
        datePicker.show();
    }
    @Override
    public void onDateSet(DatePicker view, int y, int m, int d) {
        year = y;
        month = m;
        date = d;
        completeDate = "" + year;
        if(month < 10){ completeDate += "0" + (month+1); }else{ completeDate += (month+1); }
        if(date < 10){ completeDate += "0" + date; }else{ completeDate += date; }
        lblDate.setText(year + "-" + (month+1) + "-" + date);
    }


    //==================================================================================================================
    //--- camera
    //==================================================================================================================
    //from:
    //  stackoverflow.com/questions/11274715/save-bitmap-to-file-function/39116563
    @Override //called on finish taking photo from camere
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                image = stream.toByteArray();

                imgCamera.setImageBitmap(bitmap);
                //Toast.makeText(getActivity(),""+image, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //==================================================================================================================
    //--- map
    //==================================================================================================================
    //https://stackoverflow.com/questions/50461881/java-lang-noclassdeffounderrorfailed-resolution-of-lorg-apache-http-protocolve
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                lat = mLastLocation.getLatitude();
                lng = mLastLocation.getLongitude();
                //Toast.makeText(getActivity(),"got location", Toast.LENGTH_SHORT).show();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        if(mode.equals("create")){
            mLastLocation = location;
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            //Toast.makeText(getActivity(),"location changed", Toast.LENGTH_SHORT).show();
            markMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        markMap();
    }

    private void markMap(){
        LatLng curLocation = new LatLng(lat, lng);
        if(!mode.equals("create")){
            curLocation = new LatLng(oldLat, oldLng);
        }
        String markerMessage = "Your Location";
        if(isAdded()){
            markerMessage = getResources().getString(R.string.map_marker);
        }
        map.addMarker(new MarkerOptions().position(curLocation).title(markerMessage));
        map.moveCamera(CameraUpdateFactory.newLatLng(curLocation));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }
    @Override
    public void onConnectionSuspended(int i) { }

    //==================================================================================================================
    //--- button
    //==================================================================================================================
    public void addDiary(){
        DiaryDB db = new DiaryDB(getActivity());
        int insertedId = db.addDiary(title, desc, completeDate, image, lat, lng);
        Toast.makeText(getActivity(), getResources().getText(R.string.message_create_success), Toast.LENGTH_SHORT).show();

        //push data and open this fragment again
        id = insertedId;
        Bundle bundle = new Bundle();
        bundle.putString("mode", "update");
        bundle.putInt("diaryID", id);
        bundle.putString("diaryTitle", title);
        bundle.putString("diaryDesc", desc);
        bundle.putString("diaryDate", completeDate);
        bundle.putByteArray("diaryImage", image);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateFragment createFragment = new CreateFragment();
        createFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, createFragment);
        fragmentTransaction.commit();
    }
    public void updateDiary(){
        DiaryDB db = new DiaryDB(getActivity());
        db.updateDiary(id, title, desc, completeDate, image);
        Toast.makeText(getActivity(), getResources().getText(R.string.message_update_success), Toast.LENGTH_SHORT).show();
    }
}
