package truonghuynhhoa.ptit.buscity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import truonghuynhhoa.ptit.adapter.RouteAdapter;
import truonghuynhhoa.ptit.adapter.StationOfRouteAdapter;
import truonghuynhhoa.ptit.model.BusRoute;
import truonghuynhhoa.ptit.model.BusStation;
import truonghuynhhoa.ptit.model.BusTurn;
import truonghuynhhoa.ptit.model.Host;
import truonghuynhhoa.ptit.model.StationOfRoute;

public class RouteActivity extends AppCompatActivity {

    private TextView txtRouteId, txtRouteName;
    private Button btnTurn;

    private ArrayList<BusTurn> busTurnList;

    // Bản đồ
    private GoogleMap mMap;
    private Polyline polyline;

    // Thuộc tính lắng nghe thay đổi vị trí trên bản đồ
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;

    // Nội dung cho các Tab
    private TabHost tabHost;

    private TextView txtTabInformationRouteId, txtTabInformationRouteName,
            txtTabInformationActivityTime,
            txtTabInformationGoOn, txtTabInformationGoBack, txtTabInformationRouteLength,
            txtTabInformationRunTime, txtTabInformationSpacingTime;

    private int routeId;
    private String routeName;

    private StationOfRouteAdapter stationOfRouteAdapter;

    private ArrayList<StationOfRoute> listStationWithTurnGoOn;
    private ArrayList<StationOfRoute> listStationWithTurnGoBack;

    private ListView lvStationOfRoute;

    // Tên CSDL
    private String DATABASE_NAME = "buscity.sqlite";
    // Cho phép truy vấn hoặc tương tác với CSDL
    private SQLiteDatabase database = null;

    private boolean isWifi;
    private boolean isMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        /*BusTurnListTask busTurnListTask = new BusTurnListTask();
        busTurnListTask.execute();

        BusRouteListTask busRouteListTask = new BusRouteListTask();
        busRouteListTask.execute();

        StationOfRouteListTask stationOfRouteListTask = new StationOfRouteListTask();
        stationOfRouteListTask.execute();*/

        // Bản đồ sẵn sàng sử dụng, sẽ xuất hiện tại fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapRoute);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Thiết lập kiểu hiển thị bản đồ
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Cấp quyền truy cập vị trí
                if (ActivityCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RouteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);

                // Thiết đặt cho bản đồ hiểu được sự thay đổi vị trí của chúng ta
                mMap.setOnMyLocationChangeListener(myLocationChangeListener);

            }
        });

        addControls();
        addEvents();
}

    private void setStationOfRoute(ArrayList<StationOfRoute> stations){
        if(polyline != null){
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        for(StationOfRoute station : stations){
            LatLng latLng = new LatLng(station.getStation().getLatitude(), station.getStation().getLongtitude());
            if(stations.indexOf(station) == 0){
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
            else{
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
            }

            polylineOptions.add(latLng);
        }
        polylineOptions.width(10);
        polylineOptions.color(Color.GREEN);
        polylineOptions.geodesic(true);
        polyline = mMap.addPolyline(polylineOptions);
    }

    private void showBusRoute() {
        BusRoute busRoute = new BusRoute();

        // Bước 1: mở CSDL, có thì mở, không có thì tạo mới CSDL không có bảng nào
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from BusRoute", null);

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String startTime = cursor.getString(2);
            String endTime = cursor.getString(3);
            int routeLength = cursor.getInt(4);
            int runTime = cursor.getInt(5);
            int spacingTime = cursor.getInt(6);

            busRoute =
                    new BusRoute(id, name, startTime, endTime, routeLength, runTime, spacingTime);
        }

        // đóng kết nối
        cursor.close();

        txtTabInformationRouteId.setText(busRoute.getId().toString());
        txtTabInformationRouteName.setText(busRoute.getName());
        txtTabInformationActivityTime.setText(busRoute.getStartTime() + " - "
                + busRoute.getEndTime());
        txtTabInformationRouteLength.setText(busRoute.getRouteLength().toString() + " km");
        txtTabInformationRunTime.setText(busRoute.getRunTime().toString() + " minutes");
        txtTabInformationSpacingTime.setText(busRoute.getSpacingTime().toString() + " minutes");
    }

    private void showBusTurn(){
        // Bước 1: mở CSDL, có thì mở, không có thì tạo mới CSDL không có bảng nào
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select * from BusTurn", null);

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);

            BusTurn busTurn = new BusTurn(id, name);

            busTurnList.add(busTurn);
        }

        // đóng kết nối
        cursor.close();

        btnTurn.setText(busTurnList.get(0).getName());
    }

    private void showStationOfRouteList(){
        Map<String, ArrayList<StationOfRoute>> maps = new HashMap<String, ArrayList<StationOfRoute>>();

        // Bước 1: mở CSDL, có thì mở, không có thì tạo mới CSDL không có bảng nào
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        for(int i = 1; i <= 2; i++){
            ArrayList<StationOfRoute> stationOfRouteList = new ArrayList<StationOfRoute>();

            Cursor cursor = database.rawQuery("select * from StationOfRoute where routeid = ? and turnid = ?",
                    new String[]{String.valueOf(routeId), String.valueOf(i)});

            while (cursor.moveToNext()){
                int busRouteId = cursor.getInt(0);

                Cursor cursorRoute = database.rawQuery("select * from BusRoute where id = ?",
                        new String[]{String.valueOf(busRouteId)});
                BusRoute route = new BusRoute();

                while (cursorRoute.moveToNext()){
                    int id = cursorRoute.getInt(0);
                    String name = cursorRoute.getString(1);
                    String startTime = cursorRoute.getString(2);
                    String endTime = cursorRoute.getString(3);
                    int routeLength = cursorRoute.getInt(4);
                    int runTime = cursorRoute.getInt(5);
                    int spacingTime = cursorRoute.getInt(6);

                    route =
                            new BusRoute(id, name, startTime, endTime, routeLength, runTime, spacingTime);
                }

                int busStationId = cursor.getInt(1);

                Cursor cursorStation = database.rawQuery("select * from BusStation where id = ?",
                        new String[]{String.valueOf(busStationId)});
                BusStation station = new BusStation();

                while (cursorStation.moveToNext()){
                    int id = cursorStation.getInt(0);
                    String name = cursorStation.getString(1);
                    double latitude = cursorStation.getDouble(2);
                    double longtitude = cursorStation.getDouble(3);
                    String address = cursorStation.getString(4);

                    station =
                            new BusStation(id, name, latitude, longtitude, address);
                }

                int busTurnId = cursor.getInt(2);

                Cursor cursorTurn = database.rawQuery("select * from BusTurn where id = ?",
                        new String[]{String.valueOf(busTurnId)});
                BusTurn turn = new BusTurn();

                while (cursorTurn.moveToNext()){
                    int id = cursorTurn.getInt(0);
                    String name = cursorTurn.getString(1);

                    turn = new BusTurn(id, name);
                }

                int numericalOrder = cursor.getInt(3);
                int nextStationTime = cursor.getInt(4);

                cursorRoute.close();
                cursorStation.close();
                cursorTurn.close();

                StationOfRoute stationOfRoute = new StationOfRoute(route, station, turn, numericalOrder, nextStationTime);
                stationOfRouteList.add(stationOfRoute);
            }

            cursor.close();

            maps.put("go"+i, stationOfRouteList);
        }

        String goOn = "";
        String goBack = "";

        ArrayList<StationOfRoute> listGoOn = maps.get("go1");
        ArrayList<StationOfRoute> listGoBack = maps.get("go2");

        int m = 0;
        int n = 0;

        for(StationOfRoute stationOfRoute : listGoOn){
            m++;
            if(m == listGoOn.size()){
                goOn += stationOfRoute.getStation().getName();
            }
            else{
                goOn += stationOfRoute.getStation().getName() + "-";
            }
        }

        for(StationOfRoute stationOfRoute : listGoBack){
            n++;
            if(n == listGoBack.size()){
                goBack += stationOfRoute.getStation().getName();
            }
            else{
                goBack += stationOfRoute.getStation().getName() + "-";
            }
        }

        txtTabInformationGoOn.setText(goOn);
        txtTabInformationGoBack.setText(goBack);

        stationOfRouteAdapter.clear();
        stationOfRouteAdapter.addAll(listGoOn);

        listStationWithTurnGoOn.clear();
        listStationWithTurnGoOn.addAll(listGoOn);

        listStationWithTurnGoBack.clear();
        listStationWithTurnGoBack.addAll(listGoBack);

        MapOfBusTask mapOfBusTask = new MapOfBusTask();
        mapOfBusTask.execute(listGoOn);
    }

    private void addControls() {
        myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                // Vĩ tuyến và kinh tuyến tại vị trí lắng nghe
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Chi tiết của marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                /*
                    Cung cấp thông tin chi tiết cho marker
                    Khi bản đồ hiện ra sẽ nhảy tới marker đó và phóng to ra
                 */
                Marker marker = mMap.addMarker(markerOptions);
            }
        };

        txtRouteId = findViewById(R.id.txtRouteId);
        txtRouteName = findViewById(R.id.txtRouteName);

        // Lấy dữ liệu từ Search gửi qua
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        routeId = bundle.getInt("routeId", 0);
        routeName = bundle.getString("routeName");

        txtRouteId.setText("Route " +
                String.valueOf(routeId));
        txtRouteName.setText(routeName);


        // Lấy dữ liệu của turn truyền vào cho nút Turn
        busTurnList = new ArrayList<BusTurn>();
        btnTurn = findViewById(R.id.btnTurn);

        tabHost = findViewById(R.id.tabHost);
        // Cài đặt giao diện cho tabHost, nếu không chạy lên sẽ rỗng
        tabHost.setup();

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        // Thiết lập nội dung
        tab1.setContent(R.id.tab1);

        // Đọc xml layout tab_information và biến đổi các View trong nó thành java code
        LinearLayout tabStaionLinear = findViewById(R.id.tab1);
        tabStaionLinear.removeAllViews();

        LayoutInflater layoutStation = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabStation = layoutStation.inflate(R.layout.tab_station, tabStaionLinear, true);

        lvStationOfRoute = tabStation.findViewById(R.id.lvStationOfRoute);
        stationOfRouteAdapter =
        new StationOfRouteAdapter(RouteActivity.this, R.layout.item_station_of_route);

        listStationWithTurnGoOn = new ArrayList<StationOfRoute>();
        listStationWithTurnGoBack = new ArrayList<StationOfRoute>();

        lvStationOfRoute.setAdapter(stationOfRouteAdapter);

        // Thiết lập tiêu đề
        tab1.setIndicator("Station");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab1);

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        // Thiết lập nội dung
        tab2.setContent(R.id.tab2);

        // Đọc xml layout tab_information và biến đổi các View trong nó thành java code
        LinearLayout tabInformationLinear = findViewById(R.id.tab2);
        tabInformationLinear.removeAllViews();

        LayoutInflater layoutInformation = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabInformation = layoutInformation.inflate(R.layout.tab_information, tabInformationLinear, true);

        txtTabInformationRouteId = tabInformation.findViewById(R.id.txtRouteId);
        txtTabInformationRouteName = tabInformation.findViewById(R.id.txtRouteName);
        txtTabInformationActivityTime = tabInformation.findViewById(R.id.txtActivityTime);
        txtTabInformationGoOn = tabInformation.findViewById(R.id.txtGoOn);
        txtTabInformationGoBack = tabInformation.findViewById(R.id.txtGoBack);
        txtTabInformationRouteLength = tabInformation.findViewById(R.id.txtRouteLength);
        txtTabInformationRunTime = tabInformation.findViewById(R.id.txtRunTime);
        txtTabInformationSpacingTime = tabInformation.findViewById(R.id.txtSpacingTime);

        // Thiết lập tiêu đề
        tab2.setIndicator("Information");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab2);

        // Kiểm tra trạng thái kết nối mạng của điện thoại
        checkNetwork();

        if(isWifi == false && isMobile == false){
            Toast.makeText(RouteActivity.this, "YOU ARE OFFLINE", Toast.LENGTH_SHORT).show();
            showBusTurn();
            showBusRoute();
            showStationOfRouteList();
        }
        else{
            Toast.makeText(RouteActivity.this, "YOU ARE ONLINE", Toast.LENGTH_SHORT).show();

            // Triệu gọi tiểu trình cho nó chạy
            BusTurnListTask busTurnListTask = new BusTurnListTask();
            busTurnListTask.execute();

            BusRouteListTask busRouteListTask = new BusRouteListTask();
            busRouteListTask.execute();

            StationOfRouteListTask stationOfRouteListTask = new StationOfRouteListTask();
            stationOfRouteListTask.execute();
        }
    }

    private void addEvents() {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("t1")){
                    Toast.makeText(RouteActivity.this,
                            "Station selected",
                            Toast.LENGTH_SHORT).show();
                }
                if(tabId.equals("t2")){
                    Toast.makeText(RouteActivity.this,
                            "Information selected",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnTurn.getText().toString().equals(busTurnList.get(0).getName())){
                    btnTurn.setText(busTurnList.get(1).getName());

                    stationOfRouteAdapter.clear();
                    stationOfRouteAdapter.addAll(listStationWithTurnGoBack);


                    // Thiết đặt các trạm xe trên bản đồ
                    setStationOfRoute(listStationWithTurnGoBack);
                }
                else if(btnTurn.getText().toString().equals(busTurnList.get(1).getName())){
                    btnTurn.setText(busTurnList.get(0).getName());

                    stationOfRouteAdapter.clear();
                    stationOfRouteAdapter.addAll(listStationWithTurnGoOn);

                    // Thiết đặt các trạm xe trên bản đồ
                    setStationOfRoute(listStationWithTurnGoOn);
                }
            }
        });

        lvStationOfRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StationOfRoute stationOfRoute = (StationOfRoute) parent.getItemAtPosition(position);

                LatLng latLng = new LatLng(stationOfRoute.getStation().getLatitude(), stationOfRoute.getStation().getLongtitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
    }

    class BusTurnListTask extends AsyncTask<Void, Void, ArrayList<BusTurn>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<BusTurn> busTurns) {
            super.onPostExecute(busTurns);
            busTurnList.clear();
            busTurnList.addAll(busTurns);

            btnTurn.setText(busTurnList.get(0).getName());
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<BusTurn> doInBackground(Void... voids) {
            ArrayList<BusTurn> busTurnList = new ArrayList<BusTurn>();

            try {
                URL url = new URL("http://" + Host.ip + "/buscity/api/busturn");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                InputStreamReader inputStreamReader =
                        new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = jsonObject.getInt("Id");
                    String name = jsonObject.getString("Name");

                    BusTurn busTurn =
                            new BusTurn(id, name);

                    busTurnList.add(busTurn);
                }

                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (Exception e){
                Log.e("Error", e.toString());
            }

            return busTurnList;
        }
    }

    class BusRouteListTask extends AsyncTask<Void, Void,BusRoute>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Nhận kết quả trả về sau khi kết thúc 1 tiểu trình
        @Override
        protected void onPostExecute(BusRoute busRoute) {
            super.onPostExecute(busRoute);

            txtTabInformationRouteId.setText(busRoute.getId().toString());
            txtTabInformationRouteName.setText(busRoute.getName());
            txtTabInformationActivityTime.setText(busRoute.getStartTime() + " - "
                    + busRoute.getEndTime());
            txtTabInformationRouteLength.setText(busRoute.getRouteLength().toString() + " km");
            txtTabInformationRunTime.setText(busRoute.getRunTime().toString() + " minutes");
            txtTabInformationSpacingTime.setText(busRoute.getSpacingTime().toString() + " minutes");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        // Xử lý dữ liệu với Web Service
        @Override
        protected BusRoute doInBackground(Void... voids) {
            BusRoute busRoute = new BusRoute();
            try {
                URL url = new URL("http://" + Host.ip + "/buscity/api/busroute/" + routeId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                InputStreamReader inputStreamReader =
                        new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                int id = jsonObject.getInt("Id");
                String name = jsonObject.getString("Name");
                String startTime = jsonObject.getString("StartTime");
                String endTime = jsonObject.getString("EndTime");
                int routeLength = jsonObject.getInt("RouteLength");
                int runTime = jsonObject.getInt("RunTime");
                int spacingTime = jsonObject.getInt("SpacingTime");

                busRoute =
                new BusRoute(id, name, startTime, endTime, routeLength, runTime, spacingTime);

                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (Exception e){
                Log.e("Error", e.toString());
            }

            return busRoute;
        }
    }

    class StationOfRouteListTask extends AsyncTask<Void, Void, Map<String, ArrayList<StationOfRoute>>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Map<String, ArrayList<StationOfRoute>> maps) {
            super.onPostExecute(maps);
            String goOn = "";
            String goBack = "";

            ArrayList<StationOfRoute> listGoOn = maps.get("go1");
            ArrayList<StationOfRoute> listGoBack = maps.get("go2");

            int i = 0;
            int j = 0;

            for(StationOfRoute stationOfRoute : listGoOn){
                i++;
                if(i == listGoOn.size()){
                    goOn += stationOfRoute.getStation().getName();
                }
                else{
                    goOn += stationOfRoute.getStation().getName() + "-";
                }
            }

            for(StationOfRoute stationOfRoute : listGoBack){
                j++;
                if(j == listGoBack.size()){
                    goBack += stationOfRoute.getStation().getName();
                }
                else{
                    goBack += stationOfRoute.getStation().getName() + "-";
                }
            }

            setStationOfRoute(listGoOn);

            txtTabInformationGoOn.setText(goOn);
            txtTabInformationGoBack.setText(goBack);

            stationOfRouteAdapter.clear();
            stationOfRouteAdapter.addAll(listGoOn);

            listStationWithTurnGoOn.clear();
            listStationWithTurnGoOn.addAll(listGoOn);

            listStationWithTurnGoBack.clear();
            listStationWithTurnGoBack.addAll(listGoBack);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Map<String, ArrayList<StationOfRoute>> doInBackground(Void... voids) {
            Map<String, ArrayList<StationOfRoute>> maps = new HashMap<String, ArrayList<StationOfRoute>>();

            try{
                for(int i = 1; i <= 2; i++){
                    ArrayList<StationOfRoute> stationOfRouteList = new ArrayList<StationOfRoute>();
                    String params = "?routeid=" + routeId + "&turnid=" + i;

                    URL url = new URL("http://" + Host.ip + "/buscity/api/stationofroutedetail/" + params);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    connection.setConnectTimeout(5000);

                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    bufferedReader.close();
                    inputStreamReader.close();

                    JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                    for(int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);

                        JSONObject busRoute = jsonObject.getJSONObject("BusRoute");
                        JSONObject busStation = jsonObject.getJSONObject("BusStation");
                        JSONObject busTurn = jsonObject.getJSONObject("BusTurn");



                        BusRoute route = new BusRoute(busRoute.getInt("Id"),
                                busRoute.getString("Name"),
                                busRoute.getString("StartTime"),
                                busRoute.getString("EndTime"),
                                busRoute.getInt("RouteLength"),
                                busRoute.getInt("RunTime"),
                                busRoute.getInt("SpacingTime"));

                        BusStation station = new BusStation(busStation.getInt("Id"),
                                busStation.getString("Name"),
                                busStation.getDouble("Latitude"),
                                busStation.getDouble("Longtitude"),
                                busStation.getString("Address"));

                        BusTurn turn = new BusTurn(busTurn.getInt("Id"),
                                busTurn.getString("Name"));

                        int numericalOrder = jsonObject.getInt("NumericalOrder");
                        int nextStationTime = jsonObject.getInt("NextStationTime");

                        StationOfRoute stationOfRoute = new StationOfRoute(route, station, turn, numericalOrder, nextStationTime);
                        stationOfRouteList.add(stationOfRoute);
                    }

                    maps.put("go"+i, stationOfRouteList);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return maps;
        }
    }

    public void checkNetwork(){
        isWifi = false;
        isMobile = false;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo networkInfo : networkInfos){
            if(networkInfo.getTypeName().equalsIgnoreCase("WIFI")){
                if(networkInfo.isConnected()){
                    isWifi = true;
                }
            }
            if(networkInfo.getTypeName().equalsIgnoreCase("MOBILE")){
                if(networkInfo.isConnected()){
                    isMobile = true;
                }
            }
        }


        Toast.makeText(RouteActivity.this, "Wifi connected: " + isWifi, Toast.LENGTH_SHORT).show();
        Toast.makeText(RouteActivity.this, "Mobile connected: " + isMobile, Toast.LENGTH_SHORT).show();
    }

    class MapOfBusTask extends AsyncTask<ArrayList<StationOfRoute>, ArrayList<StationOfRoute>, Void>{

        @Override
        protected Void doInBackground(ArrayList<StationOfRoute>... arrayLists) {
            publishProgress(arrayLists[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(ArrayList<StationOfRoute>... values) {
            super.onProgressUpdate(values);
            ArrayList<StationOfRoute> list = values[0];

            setStationOfRoute(list);
        }
    }
}
