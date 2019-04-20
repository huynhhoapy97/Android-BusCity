package truonghuynhhoa.ptit.buscity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import com.google.android.gms.common.api.GoogleApiClient;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import truonghuynhhoa.ptit.adapter.DetailInstructionAdapter;
import truonghuynhhoa.ptit.adapter.StationInstructionAdapter;
import truonghuynhhoa.ptit.adapter.StationOfRouteAdapter;
import truonghuynhhoa.ptit.model.BusStation;
import truonghuynhhoa.ptit.model.DetailInstruction;
import truonghuynhhoa.ptit.model.Host;
import truonghuynhhoa.ptit.model.StationInstruction;
import truonghuynhhoa.ptit.model.StationOfRoute;

public class DetailInstructionActivity extends AppCompatActivity {

    // Bản đồ
    private GoogleMap mMap;
    private Polyline polyline;

    // Thuộc tính lắng nghe thay đổi vị trí trên bản đồ
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;

    // Nội dung cho các Tab
    private TabHost tabHost;

    private TextView txtOrigin, txtDestination, txtWalkDistance, txtBusDistance;
    private Button btnRouteId;

    private ListView lvStationInstruction, lvDetailInstruction;

    private String origin, destination, stationOrigin, stationDestination;
    private int busRoute, numericalOrderOrigin, numericalOrderDestination, turn;

    private StationInstructionAdapter stationInstructionAdapter;
    private DetailInstructionAdapter detailInstructionAdapter;

    private ArrayList<BusStation> busStations;
    private ArrayList<StationInstruction> stationInstructionList;
    private ArrayList<DetailInstruction> detailInstructionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_instruction);

        // Bản đồ sẵn sàng sử dụng, sẽ xuất hiện tại fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Thiết lập kiểu hiển thị bản đồ
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Cấp quyền truy cập vị trí
                if (ActivityCompat.checkSelfPermission(DetailInstructionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DetailInstructionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        txtOrigin = findViewById(R.id.txtOrigin);
        txtDestination = findViewById(R.id.txtDestination);
        txtWalkDistance = findViewById(R.id.txtWalkDistance);
        txtBusDistance = findViewById(R.id.txtBusDistance);
        btnRouteId = findViewById(R.id.btnRouteId);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        origin = bundle.getString("origin");
        txtOrigin.setText(origin);

        destination = bundle.getString("destination");
        txtDestination.setText(destination);

        busRoute = bundle.getInt("busRoute");
        btnRouteId.setText(String.valueOf(busRoute));

        if(bundle.getInt("walkDistance") > 1000){
            double walkDistance = bundle.getInt("walkDistance") / 1000;
            txtWalkDistance.setText(String.valueOf(walkDistance) + " km");
        }
        else{
            txtWalkDistance.setText(String.valueOf(bundle.getInt("walkDistance")) + " m");
        }

        if(bundle.getInt("busDistance") > 1000){
            double busDistance = bundle.getInt("busDistance") / 1000;
            txtBusDistance.setText(String.valueOf(busDistance) + " km");
        }
        else{
            txtBusDistance.setText(String.valueOf(bundle.getInt("busDistance")) + " m");
        }

        numericalOrderOrigin = bundle.getInt("numericalOrderOrigin");
        numericalOrderDestination = bundle.getInt("numericalOrderDestination");
        Toast.makeText(DetailInstructionActivity.this, "numericalOrderOrigin " + numericalOrderOrigin, Toast.LENGTH_SHORT).show();
        Toast.makeText(DetailInstructionActivity.this, "numericalOrderDestination " + numericalOrderDestination, Toast.LENGTH_SHORT).show();
        turn = bundle.getInt("turn");
        stationOrigin = bundle.getString("stationOrigin");
        stationDestination = bundle.getString("stationDestination");

        tabHost = findViewById(R.id.tabHost);
        // Cài đặt giao diện cho tabHost, nếu không chạy lên sẽ rỗng
        tabHost.setup();

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        // Thiết lập nội dung
        tab1.setContent(R.id.tab1);

        // Đọc xml layout tab_detail_instruction và biến đổi các View trong nó thành java code
        LinearLayout tabDetailLinear = findViewById(R.id.tab1);
        tabDetailLinear.removeAllViews();

        LayoutInflater layoutDetail = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabDetail = layoutDetail.inflate(R.layout.tab_detail_instruction, tabDetailLinear, true);

        lvDetailInstruction = tabDetail.findViewById(R.id.lvDetailInstruction);
        detailInstructionAdapter =
                new DetailInstructionAdapter(DetailInstructionActivity.this, R.layout.item_detail_instruction);

        int imageOrigin = R.drawable.walk;
        String walkOrBusOrigin = "Take " + busRoute;
        String fromToOrigin = "Start at: " + origin + "\n\nTake route " + busRoute + " at station: " + stationOrigin;
        DetailInstruction detailInstructionOrigin = new DetailInstruction(imageOrigin, walkOrBusOrigin, fromToOrigin);

        int imageCenter = R.drawable.bus;
        String walkOrBusCenter = "Go " + busRoute;
        String fromToCenter = stationOrigin + " -> " + stationDestination;
        DetailInstruction detailInstructionCenter = new DetailInstruction(imageCenter, walkOrBusCenter, fromToCenter);

        int imageDestination = R.drawable.walk;
        String walkOrBusDestination = "Walk to destination";
        String fromToDestination = "Get off at station: " + stationDestination + "\n\nWalk to: " + destination;
        DetailInstruction detailInstructionDestination = new DetailInstruction(imageDestination, walkOrBusDestination, fromToDestination);

        detailInstructionList = new ArrayList<DetailInstruction>();

        detailInstructionList.add(detailInstructionOrigin);
        detailInstructionList.add(detailInstructionCenter);
        detailInstructionList.add(detailInstructionDestination);

        detailInstructionAdapter.clear();
        detailInstructionAdapter.addAll(detailInstructionList);

        lvDetailInstruction.setAdapter(detailInstructionAdapter);

        // Thiết lập tiêu đề
        tab1.setIndicator("Detail");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab1);

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        // Thiết lập nội dung
        tab2.setContent(R.id.tab2);

        LinearLayout tabStaionLinear = findViewById(R.id.tab2);
        tabStaionLinear.removeAllViews();

        LayoutInflater layoutStation = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabStation = layoutStation.inflate(R.layout.tab_station_instruction, tabStaionLinear, true);

        lvStationInstruction = tabStation.findViewById(R.id.lvStationInstruction);
        stationInstructionAdapter =
                new StationInstructionAdapter(DetailInstructionActivity.this, R.layout.item_station_instruction);

        BusStationListTask busStationListTask = new BusStationListTask();
        busStationListTask.execute(busRoute, turn, numericalOrderOrigin, numericalOrderDestination);

        stationInstructionList = new ArrayList<StationInstruction>();

        StationInstruction stationInstructionOrigin = new StationInstruction(R.drawable.point_black, origin);
        stationInstructionList.add(stationInstructionOrigin);

        try {
            busStations = busStationListTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(BusStation busStation : busStations){
            StationInstruction stationInstruction = new StationInstruction(R.drawable.point_green, busStation.getName());
            stationInstructionList.add(stationInstruction);
        }

        StationInstruction stationInstructionDestination = new StationInstruction(R.drawable.point_black, destination);
        stationInstructionList.add(stationInstructionDestination);

        stationInstructionAdapter.clear();
        stationInstructionAdapter.addAll(stationInstructionList);

        lvStationInstruction.setAdapter(stationInstructionAdapter);

        // Thiết lập tiêu đề
        tab2.setIndicator("Station");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab2);
    }

    private void addEvents() {
        // Chọn Station nào thì nhảy tới Station đó
        lvStationInstruction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0 && position != (stationInstructionAdapter.getCount() - 1)){
                    BusStation busStation = (BusStation) busStations.get(position - 1);

                    LatLng latLng = new LatLng(busStation.getLatitude(), busStation.getLongtitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


                }
            }
        });
    }

    private void setStationOfRoute(ArrayList<BusStation> stations){
        if(polyline != null){
            polyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions();
        for(BusStation station : stations){
            LatLng latLng = new LatLng(station.getLatitude(), station.getLongtitude());
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

    class BusStationListTask extends AsyncTask<Integer, Void, ArrayList<BusStation>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<BusStation> busStations) {
            super.onPostExecute(busStations);
            setStationOfRoute(busStations);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<BusStation> doInBackground(Integer... integers) {

            ArrayList<BusStation> busStationList = new ArrayList<BusStation>();

            String params = "?routeid=" + integers[0] +
                    "&turnid=" + integers[1] +
                    "&numericalorderorigin=" + integers[2] +
                    "&numericalorderdestination=" + integers[3];

            try{
                URL url = new URL("http://" + Host.ip + "/buscity/api/stationofroute/" + params);
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

                JSONArray jsonArray = new JSONArray(stringBuilder.toString());
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    int id = jsonObject.getInt("Id");
                    String name = jsonObject.getString("Name");
                    double latitude = jsonObject.getDouble("Latitude");
                    double longtitude = jsonObject.getDouble("Longtitude");
                    String address = jsonObject.getString("Address");

                    BusStation busStation =
                            new BusStation(id, name, latitude, longtitude, address);

                    busStationList.add(busStation);
                }

                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return busStationList;
        }
    }
}
