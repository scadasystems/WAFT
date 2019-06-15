package org.lulzm.waft;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
import xyz.hasnat.sweettoast.SweetToast;

import java.io.IOException;
import java.util.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, PlacesListener, DirectionCallback {
    private GoogleMap mMap;
    private Marker currentMarker;
    private static long backPressed;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int TIME_LIMIT = 1500;
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    final private String androidKey = "AIzaSyD4i9LTNlcP6E9WFcXJOHLEAUgyXYmBDAk";
    final private String serverKey = "AIzaSyChDbn5ae2fxwz56mgspasHXnAwwP_zBbU";
    boolean needRequest = false;
    private FusedLocationProviderClient mFusedLocation;
    private LocationRequest locationRequest;
    Location mCurrentLocation;
    LatLng currentPosition, selected;
    String TAG = "MapsActivity";
    List<Marker> previous_marker = null;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUoDown;
    TextView tv_duration, tv_distance;
    Button btn_direction;
    MaterialButton btn_walking, btn_driving;
    EditText edt_duration, edt_distance;

    private String Status;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 다크모드 적용
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate");

        // 상태표시줄 색상 변경
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                getWindow().setStatusBarColor(Color.BLACK);
            } else {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            getWindow().setStatusBarColor(Color.BLACK);
        }

        // Bottom_sheet 연결
        init();

        // bottomSheet 버튼이벤트
        tbUoDown.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tbUoDown.setChecked(true);
                } else {
                    tbUoDown.setChecked(false);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        // Maps Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        permissions();
        checkPermission();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        previous_marker = new ArrayList<Marker>();
        locationRequest = new LocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        // Initialize Places.
        Places.initialize(getApplicationContext(), androidKey);

        // Place
        findViewById(R.id.searchInput).setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
//                    .setCountry("KR")
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        // 주변 탐색 버튼이벤트
        findViewById(R.id.btn_search_bank).setOnClickListener(v -> showBank(currentPosition));
        findViewById(R.id.btn_search_restaurant).setOnClickListener(v -> showRestaurant(currentPosition));
        findViewById(R.id.btn_search_busStop).setOnClickListener(v -> showBusStation(currentPosition));
        findViewById(R.id.btn_search_police).setOnClickListener(v -> showPolice(currentPosition));

        // 경로찾기 버튼이벤트
        btn_walking.setOnClickListener(direction);
        btn_driving.setOnClickListener(direction);

        // 수정 불가
        edt_duration.setFocusable(false);
        edt_duration.setClickable(false);
        edt_distance.setFocusable(false);
        edt_distance.setClickable(false);

        // bottomSheet 내위치 / 목적지 클릭 이벤트
        edt_duration.setOnClickListener(v -> edt_duration.setText(getCurrentAddress(currentPosition)));
        edt_distance.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields).build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

    }

    // bottomSheet 연결
    private void init() {
        this.linearLayoutBSheet = findViewById(R.id.bottom_sheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUoDown = findViewById(R.id.toggleButton);
        this.tv_distance = findViewById(R.id.tv_distance);
        this.tv_duration = findViewById(R.id.tv_duration);
        this.btn_driving = findViewById(R.id.btn_driving);
        this.btn_walking = findViewById(R.id.btn_walking);
        this.edt_duration = findViewById(R.id.edt_duration);
        this.edt_distance = findViewById(R.id.edt_distance);
    }

    //  direction 초기화
    Button.OnClickListener direction = v -> {
        LatLng origin = currentPosition;
        LatLng destination = selected;
        if (origin == null) {
            SweetToast.error(MapsActivity.this, "목적지 " + destination + "\n" + "내위치 없음");
        } else if (destination == null) {
            SweetToast.error(MapsActivity.this, "내위치 " + origin + "\n" + "목적지 없음");
        } else {
            switch (v.getId()) {
                case R.id.btn_walking:      // walk 모드
                    btn_walking.setSelected(true);
                    btn_driving.setSelected(false);
                    SweetToast.success(MapsActivity.this, "WALK모드" + "\n" + "내위치 " + origin + "\n" + "목적지 " + destination);
                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(destination)
                            .transportMode(TransportMode.WALKING)
                            .alternativeRoute(true)
                            .unit(Unit.METRIC)
                            .execute(this);
                    break;

                case R.id.btn_driving:      // drive 모드
                    btn_walking.setSelected(false);
                    btn_driving.setSelected(true);
                    SweetToast.success(MapsActivity.this, "DRIVE모드" + "\n" + "내위치 " + origin + "\n" + "목적지 " + destination);
                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(destination)
                            .transportMode(TransportMode.DRIVING)
                            .alternativeRoute(true)
                            .unit(Unit.METRIC)
                            .execute(this);
                    break;
            }

        }
    };

    //   Direction 이벤트 Callback
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        GoogleDirectionConfiguration.getInstance().setLogEnabled(true);
        if (direction.isOK()) {     // 성공 시
            mMap.clear();
            Route route = direction.getRouteList().get(0);
            Leg leg = route.getLegList().get(0);
            mMap.addMarker(new MarkerOptions().position(selected));
            // 경로표시
            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
            PolylineOptions polylineOptions = DirectionConverter.createPolyline(this, directionPositionList, 3, Color.RED);
            mMap.addPolyline(polylineOptions);
            setCameraWithCoordinationBounds(route);

            // 시간, 거리
            Info distanceInfo = leg.getDistance();
            Info durationInfo = leg.getDuration();
            String distance = distanceInfo.getText();
            String duration = durationInfo.getText();
            tv_distance.setText(distance);
            tv_duration.setText(duration);
        } else {
            Snackbar.make(btn_walking, direction.getStatus(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Snackbar.make(btn_direction, t.getMessage(), Snackbar.LENGTH_SHORT).show();
    }

    // 카메라 위치 업데이트
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    // Place Select Event
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                selected = place.getLatLng();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(selected);
                MarkerOptions markerOptions = new MarkerOptions();
                if (selected != null) {
                    markerOptions.position(selected);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(place.getAddress());
                    markerOptions.draggable(true);
                    edt_duration.setText(getCurrentAddress(currentPosition));
                    edt_distance.setText(place.getAddress());
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(cameraUpdate);
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                    tbUoDown.setChecked(true);
                }
                SweetToast.success(this, "이름 : " + place.getName() + "\n" + "위치 : " + place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                if (status.equals("OVER_QUERY_LIMIT")) {
                    Toast.makeText(this, "OVER_QUERY_LIMIT", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        Log.d(TAG, "onMapReady :");
        mMap = map;
        SharedPreferences sharedPreferences = getSharedPreferences("change_theme", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        }

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

                String markerTitle = "내위치";
                String markerSnippet = getCurrentAddress(currentPosition);
                Log.d(TAG, "onLocationResult : " + markerSnippet);

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
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission()) {
                mMap.setMyLocationEnabled(true);    // 내위치 버튼
                mMap.getUiSettings().setZoomControlsEnabled(true);  // 맵 컨트롤 버튼
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setPadding(0, 0, 0, 150);      // 맵 컨트롤 버튼 위치조정
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocation.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFusedLocation != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
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

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
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
            SweetToast.error(this, "인터넷을 연결해주세요");
            return "인터넷을 연결해주세요";
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
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // This method is used to detect back button
    @Override
    public void onBackPressed() {
        if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
            Intent intent_home = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent_home);
            finish();
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        } else {
            SweetToast.info(getApplicationContext(), getString(R.string.press_back_main));
        }
        backPressed = System.currentTimeMillis();
    } //End Back button press for exit...

    // 은행 검색
    public void showBank(LatLng location) {
        mMap.clear();//지도 클리어
        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key(androidKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(700) //700 미터 내에서 검색
                .type(PlaceType.BANK) //은행
                .build()
                .execute();
    }

    // 음식점 검색
    public void showRestaurant(LatLng location) {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key(androidKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(700) //700 미터 내에서 검색
                .type(PlaceType.RESTAURANT) //음식점
                .build()
                .execute();
    }

    // 버스정류장 검색
    public void showBusStation(LatLng location) {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key(androidKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(700) //700 미터 내에서 검색
                .type(PlaceType.BUS_STATION) //버스
                .build()
                .execute();
    }

    // 경찰서 검색
    public void showPolice(LatLng location) {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapsActivity.this)
                .key(androidKey)
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(700) //700 미터 내에서 검색
                .type(PlaceType.POLICE) //경찰서
                .build()
                .execute();

    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    // 주변검색
    @Override
    public void onPlacesSuccess(List<noman.googleplaces.Place> places) {
        runOnUiThread(() -> {
            for (noman.googleplaces.Place place : places) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                String markerSnippet = getCurrentAddress(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                markerOptions.snippet(markerSnippet);
                Marker item = mMap.addMarker(markerOptions);
                previous_marker.add(item);
            }
            //중복 마커 제거
            HashSet<Marker> hashSet = new HashSet<Marker>();
            hashSet.addAll(previous_marker);
            previous_marker.clear();
            previous_marker.addAll(hashSet);
        });
    }

    @Override
    public void onPlacesFinished() {

    }
}
