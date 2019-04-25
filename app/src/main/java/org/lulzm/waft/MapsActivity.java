package org.lulzm.waft;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import org.lulzm.waft.ChatHome.ChatMainActivity;
import xyz.hasnat.sweettoast.SweetToast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private GoogleMap mMap;
    private Marker currentMarker = null;
    private static long backPressed;
    private static final int TIME_LIMIT = 1500;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    boolean needRequest = false;
    private FusedLocationProviderClient mFusedLocation;
    private LocationRequest locationRequest;
    Location mCurrentLocation;
    LatLng currentPosition;
    EditText edt_search;
    LinearLayout linlay;
    String TAG = "MapsActivity";

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d("알림", "onCreate");


        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }
        permissions();
        checkPermission();

        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Log.d("알림", "onMapReady :");
        mMap = map;
        startLocationUpdates();
    }

    //권한 설정
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void permissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }
    // 권한 체크
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                // 위치 권한 허용X
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
                    SweetToast.error(this, "사용하려면 권한설정을 하세요");
                }
                // 위치 권한 허용
                mFusedLocation.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        startLocationUpdates();
                    }
                });
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());
                Log.d("알림", "onLocationResult : " + markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocation = location;
            }
        }
    };

    // 위치 업데이트
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            needRequest = true;
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("알림", "onStart");

        if (checkPermission()) {
            Log.d("알림", "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocation != null) {
            Log.d("알림", "onStop : call stopLocationUpdates");
            mFusedLocation.removeLocationUpdates(locationCallback);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

    }

    // 런타임 퍼미션 처리
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }

    public String getCurrentAddress(LatLng latlng) {
        // 지오코더 GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);

        } catch (IOException ioException) {
            // 네트워크 문제
            SweetToast.error(this, "지오코더 서비스 사용불가");
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            SweetToast.error(this, "잘못된 GPS 좌표");
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            SweetToast.error(this, "주소 미발견");
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
    }
    // edittext clearfocus
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if(TIME_LIMIT + backPressed > System.currentTimeMillis()){
            Intent intent_home = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent_home);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
        else {
            SweetToast.info(getApplicationContext(), getString(R.string.press_back_main));
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...
}
