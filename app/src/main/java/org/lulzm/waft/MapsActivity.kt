package org.lulzm.waft

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.config.GoogleDirectionConfiguration
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.constant.Unit
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*
import noman.googleplaces.NRPlaces
import noman.googleplaces.PlaceType
import noman.googleplaces.PlacesException
import noman.googleplaces.PlacesListener
import xyz.hasnat.sweettoast.SweetToast
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, PlacesListener, DirectionCallback {
    private var mMap: GoogleMap? = null
    private val currentMarker: Marker? = null
    private var AUTOCOMPLETE_REQUEST_CODE = 1
    private var androidKey: String = ""
    private var serverKey: String = ""
    private var needRequest = false
    private var mFusedLocation: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    internal lateinit var mCurrentLocation: Location
    internal lateinit var currentPosition: LatLng
    private var selected: LatLng? = null
    internal var TAG = "MapsActivity"
    private var previous_marker: MutableList<Marker>? = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var linearLayoutBSheet: LinearLayout? = null
    private var tbUoDown: ToggleButton? = null
    private lateinit var tv_duration: TextView
    private lateinit var tv_distance: TextView
    private var btn_direction: Button? = null
    private lateinit var btn_walking: MaterialButton
    private lateinit var btn_driving: MaterialButton
    private lateinit var edt_duration: EditText
    private lateinit var edt_distance: EditText
    private lateinit var locationManager: LocationManager

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val locationList = locationResult!!.locations

            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]

                currentPosition = LatLng(location.latitude, location.longitude)

                val markerTitle = "내위치"
                val markerSnippet = getCurrentAddress(currentPosition)
                Log.d(TAG, "onLocationResult : $markerSnippet")

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet)
                mCurrentLocation = location
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        // 다크모드 적용
        val sharedPreferences = getSharedPreferences("change_theme", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            setTheme(R.style.darktheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Log.d(TAG, "onCreate")

        // 상태표시줄 색상 변경
        val view = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 23 버전 이상일 때 상태바 하얀 색상, 회색 아이콘
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                window.statusBarColor = Color.BLACK
            } else {
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.parseColor("#f2f2f2")
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때 상태바 검은 색상, 흰색 아이콘
            window.statusBarColor = Color.BLACK
        }
        //GPS ON/OFF 확인해서 OFF이면 GPS 설정화면으로 이동하기
        //LocationManager , GPS 기능 사용 유무 확인 이벤트
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            SweetToast.error(applicationContext,"GPS기능을 켜주세요")
            //다크테마
            if (sharedPreferences.getBoolean("dark_theme", false)) {
                val builder = AlertDialog.Builder(this@MapsActivity, R.style.alertDialog_dark)
                val view_gps = LayoutInflater.from(this@MapsActivity).inflate(R.layout.gps_dialog, null)

                builder.setCancelable(true)
                builder.setNegativeButton(getString(R.string.gps_cancel)) { dialog, which -> dialog.cancel() }
                builder.setPositiveButton(getString(R.string.gps_success)) { dialog, which ->
                    val gpsOptionsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(gpsOptionsIntent)
                }
                builder.setView(view_gps)
                builder.show()
            } else { // 기본 앱테마
                val builder = AlertDialog.Builder(this@MapsActivity, R.style.alertDialog)
                val view_gps = LayoutInflater.from(this@MapsActivity).inflate(R.layout.gps_dialog, null)

                builder.setCancelable(true)
                builder.setNegativeButton(getString(R.string.gps_cancel)) { dialog, which -> dialog.cancel() }
                builder.setPositiveButton(getString(R.string.gps_success)) { dialog, which ->
                    val gpsOptionsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(gpsOptionsIntent)
                }
                builder.setView(view_gps)
                builder.show()
            }
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            maps.visibility = View.VISIBLE
        }


        // Bottom_sheet 연결
        init()

        // bottomSheet 버튼이벤트
        tbUoDown!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        bottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                tbUoDown!!.isChecked = newState == BottomSheetBehavior.STATE_EXPANDED
            }

            override fun onSlide(view: View, v: Float) {

            }
        })


        // Maps Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        permissions()
        checkPermission()
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        previous_marker = ArrayList()
        locationRequest = LocationRequest()
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)

        // Initialize Places.
        androidKey = resources.getString(R.string.google_maps_key)
        Places.initialize(applicationContext, androidKey)

        // Place
        findViewById<View>(R.id.searchInput).setOnClickListener {
            val fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields
            )
                //                    .setCountry("KR")
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        // 주변 탐색 버튼이벤트
        findViewById<View>(R.id.btn_search_bank).setOnClickListener { showBank(currentPosition) }
        findViewById<View>(R.id.btn_search_restaurant).setOnClickListener { showRestaurant(currentPosition) }
        findViewById<View>(R.id.btn_search_busStop).setOnClickListener { showBusStation(currentPosition) }
        findViewById<View>(R.id.btn_search_police).setOnClickListener { showPolice(currentPosition) }

        // 경로찾기 버튼이벤트
        // 경로찾기 버튼이벤트 - Walk 모드
        btn_walking.setOnClickListener {
            serverKey = resources.getString(R.string.serverKey)
            val origin = currentPosition
            val destination = selected
            // 목적지 설정 x
            if (destination == null) {
                SweetToast.error(this@MapsActivity, "내위치 $origin\n목적지 없음")
            } else {   // 목적지 설정o
                btn_walking.isSelected = true
                btn_driving.isSelected = false
                SweetToast.success(this@MapsActivity, "WALK모드\n내위치 $origin\n목적지 $destination")
                GoogleDirection.withServerKey(serverKey)
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.WALKING)
                    .alternativeRoute(true)
                    .unit(Unit.METRIC)
                    .execute(this)
            }
        }
        // 경로찾기 버튼이벤트 - Drive 모드
        btn_driving.setOnClickListener {
            serverKey = resources.getString(R.string.serverKey)
            val origin = currentPosition
            val destination = selected
            // 목적지 설정 x
            if (destination == null) {
                SweetToast.error(this@MapsActivity, "내위치 $origin\n목적지 없음")
            } else {   // 목적지 설정o
                btn_walking.isSelected = false
                btn_driving.isSelected = true
                SweetToast.success(this@MapsActivity, "DRIVE모드\n내위치 $origin\n목적지 $destination")
                GoogleDirection.withServerKey(serverKey)
                    .from(origin)
                    .to(destination)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .unit(Unit.METRIC)
                    .execute(this)
            }
        }

        // 수정 불가
        edt_duration.isFocusable = false
        edt_duration.isClickable = false
        edt_distance.isFocusable = false
        edt_distance.isClickable = false

        // bottomSheet 내위치 / 목적지 클릭 이벤트
        edt_duration.setOnClickListener { edt_duration.setText(getCurrentAddress(currentPosition)) }
        edt_distance.setOnClickListener {
            val fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields
            ).build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

    }

    // bottomSheet 연결
    private fun init() {
        this.linearLayoutBSheet = findViewById(R.id.bottom_sheet)
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet!!)
        this.tbUoDown = findViewById(R.id.toggleButton)
        this.tv_distance = findViewById(R.id.tv_distance)
        this.tv_duration = findViewById(R.id.tv_duration)
        this.btn_driving = findViewById(R.id.btn_driving)
        this.btn_walking = findViewById(R.id.btn_walking)
        this.edt_duration = findViewById(R.id.edt_duration)
        this.edt_distance = findViewById(R.id.edt_distance)
    }

    //   Direction 이벤트 Callback
    override fun onDirectionSuccess(direction: Direction, rawBody: String) {
        GoogleDirectionConfiguration.getInstance().isLogEnabled = true
        if (direction.isOK) {     // 성공 시
            mMap!!.clear()
            val route = direction.routeList[0]
            val leg = route.legList[0]
            mMap!!.addMarker(MarkerOptions().position(selected!!))
            // 경로표시
            val directionPositionList = leg.directionPoint
            val polylineOptions = DirectionConverter.createPolyline(this, directionPositionList, 3, Color.RED)
            mMap!!.addPolyline(polylineOptions)
            setCameraWithCoordinationBounds(route)

            // 시간, 거리
            val distanceInfo = leg.distance
            val durationInfo = leg.duration
            val distance = distanceInfo.text
            val duration = durationInfo.text
            tv_distance.text = distance
            tv_duration.text = duration
        } else {    // 그 외 status 표시
            Snackbar.make(btn_walking, direction.status, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDirectionFailure(t: Throwable) {
        t.message?.let { Snackbar.make(btn_direction!!, it, Snackbar.LENGTH_SHORT).show() }
    }

    // 카메라 위치 업데이트
    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    // Place Select Event
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mMap?.clear()
                val place = Autocomplete.getPlaceFromIntent(data!!)
                selected = place.latLng
                val cameraUpdate = CameraUpdateFactory.newLatLng(selected)
                val markerOptions = MarkerOptions()
                if (selected != null) {
                    markerOptions.position(selected!!)
                    markerOptions.title(place.name)
                    markerOptions.snippet(place.address)
                    markerOptions.draggable(true)
                    edt_duration.setText(getCurrentAddress(currentPosition))
                    edt_distance.setText(place.address)
                    mMap!!.addMarker(markerOptions)
                    mMap!!.moveCamera(cameraUpdate)
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(16.0f))
                    tbUoDown!!.isChecked = true
                }
                SweetToast.success(this, "이름 : " + place.name + "\n" + "위치 : " + place.latLng)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.d(TAG, status.statusMessage)

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "onMapReady :")
        mMap = map
        GoogleMap.OnMyLocationButtonClickListener { true }
        val sharedPreferences = getSharedPreferences("change_theme", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            mMap!!.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {maps.visibility = View.VISIBLE}

        startLocationUpdates()
    }

    //권한 설정
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun permissions() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // 권한 체크
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                // 위치 권한 허용X
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                        REQUEST_CODE_PERMISSIONS
                    )
                    SweetToast.error(this, "사용하려면 권한설정을 하세요")
                }
                // 위치 권한 허용
                mFusedLocation!!.lastLocation.addOnSuccessListener(this) { location ->
                    if (location != null) {
                        startLocationUpdates()
                    }
                }
            }
        }
    }

    // 위치 업데이트
    private fun startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            needRequest = true
        } else {

            val hasFineLocationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasCoarseLocationPermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return
            }
            mFusedLocation!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            if (checkPermission()) {
                mMap!!.isMyLocationEnabled = true    // 내위치 버튼
                mMap!!.uiSettings.isZoomControlsEnabled = true  // 맵 컨트롤 버튼
                mMap!!.uiSettings.isMapToolbarEnabled = false
                mMap!!.setPadding(0, 0, 0, 150)      // 맵 컨트롤 버튼 위치조정
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")

        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates")
            mFusedLocation!!.requestLocationUpdates(locationRequest, locationCallback, null)
            if (mMap != null)
                mMap?.isMyLocationEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (mFusedLocation != null) {
            Log.d(TAG, "onStop : call stopLocationUpdates")
            mFusedLocation?.removeLocationUpdates(locationCallback)
        }
    }

    override fun onResume() {
        super.onResume()
        //GPS 셋팅 화면에서 체크 하고 돌아왔을때 체크가 되어있다면 맵을 보여줌
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            maps.visibility = View.VISIBLE
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun setCurrentLocation(location: Location, markerTitle: String, markerSnippet: String) {
        currentMarker?.remove()

        val currentLatLng = LatLng(location.latitude, location.longitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)

        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap?.moveCamera(cameraUpdate)
        mMap?.animateCamera(CameraUpdateFactory.zoomTo(16.0f))

    }

    // 런타임 퍼미션 처리
    private fun checkPermission(): Boolean {

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun getCurrentAddress(latlng: LatLng): String {
        // 지오코더 GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            // 네트워크 문제
            SweetToast.error(this, "인터넷을 연결해주세요")
            return "인터넷을 연결해주세요"
        } catch (illegalArgumentException: IllegalArgumentException) {
            SweetToast.error(this, "잘못된 GPS 좌표")
            return "잘못된 GPS 좌표"
        }

        if (addresses == null || addresses.size == 0) {
            SweetToast.error(this, "주소 미발견")
            return "주소 미발견"
        } else {
            val address = addresses[0]
            return address.getAddressLine(0)
        }
    }

    // edittext clearfocus
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    // This method is used to detect back button
    override fun onBackPressed() {
        if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
            val intent_home = Intent(this@MapsActivity, MainActivity::class.java)
            startActivity(intent_home)
            finish()
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)
        } else {
            SweetToast.info(applicationContext, getString(R.string.press_back_main))
        }
        backPressed = System.currentTimeMillis()
    } //End Back button press for exit...

    // 은행 검색
    fun showBank(location: LatLng) {
        mMap?.clear()//지도 클리어
        androidKey = resources.getString(R.string.google_maps_key)
        if (previous_marker != null)
            previous_marker!!.clear()//지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this@MapsActivity)
            .key(androidKey)
            .latlng(location.latitude, location.longitude)//현재 위치
            .radius(500) //500 미터 내에서 검색
            .type(PlaceType.BANK) //은행
            .build()
            .execute()
    }

    // 음식점 검색
    fun showRestaurant(location: LatLng) {
        mMap?.clear()//지도 클리어
        androidKey = resources.getString(R.string.google_maps_key)
        if (previous_marker != null)
            previous_marker!!.clear()//지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this@MapsActivity)
            .key(androidKey)
            .latlng(location.latitude, location.longitude)//현재 위치
            .radius(500) //500 미터 내에서 검색
            .type(PlaceType.RESTAURANT) //음식점
            .build()
            .execute()
    }

    // 버스정류장 검색
    fun showBusStation(location: LatLng) {
        mMap?.clear()//지도 클리어
        androidKey = resources.getString(R.string.google_maps_key)
        if (previous_marker != null)
            previous_marker!!.clear()//지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this@MapsActivity)
            .key(androidKey)
            .latlng(location.latitude, location.longitude)//현재 위치
            .radius(500) //500 미터 내에서 검색
            .type(PlaceType.BUS_STATION) //버스
            .build()
            .execute()
    }

    // 경찰서 검색
    fun showPolice(location: LatLng) {
        mMap?.clear()//지도 클리어
        androidKey = resources.getString(R.string.google_maps_key)

        if (previous_marker != null)
            previous_marker!!.clear()//지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this@MapsActivity)
            .key(androidKey)
            .latlng(location.latitude, location.longitude)//현재 위치
            .radius(500) //500 미터 내에서 검색
            .type(PlaceType.POLICE) //경찰서
            .build()
            .execute()

    }

    override fun onPlacesFailure(e: PlacesException) {

    }

    override fun onPlacesStart() {

    }

    // 주변검색
    override fun onPlacesSuccess(places: List<noman.googleplaces.Place>) {
        runOnUiThread {
            for (place in places) {
                val latLng = LatLng(place.latitude, place.longitude)
                val markerSnippet = getCurrentAddress(latLng)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(place.name)
                markerOptions.snippet(markerSnippet)
                val item = mMap!!.addMarker(markerOptions)
                previous_marker!!.add(item)
            }
            //중복 마커 제거
            val hashSet = HashSet<Marker>()
            previous_marker?.let { hashSet.addAll(it) }
            previous_marker!!.clear()
            previous_marker!!.addAll(hashSet)
        }
    }

    override fun onPlacesFinished() {

    }

    companion object {
        private var backPressed: Long = 0
        private val TIME_LIMIT = 1500
        private val REQUEST_CODE_PERMISSIONS = 1000
    }
}






