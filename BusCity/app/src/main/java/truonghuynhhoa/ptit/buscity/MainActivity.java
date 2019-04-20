package truonghuynhhoa.ptit.buscity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import truonghuynhhoa.ptit.adapter.FunctionAdapter;
import truonghuynhhoa.ptit.model.BusStation;
import truonghuynhhoa.ptit.model.Function;
import truonghuynhhoa.ptit.model.Host;

public class MainActivity extends AppCompatActivity {

    // Toolbar và Menu
    private Toolbar toolbar;
    private ImageView imgMenu;

    // Bản đồ
    private GoogleMap mMap;

    // Thuộc tính lắng nghe thay đổi vị trí trên bản đồ
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener;

    // Hai fuction chính
    private ListView lvFunction;
    private FunctionAdapter functionAdapter;

    // Nút điều khiển function
    private ImageView imgFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("");

        // Bản đồ sẵn sàng sử dụng, sẽ xuất hiện tại fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Thiết lập kiểu hiển thị bản đồ
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Cấp quyền truy cập vị trí
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        addDatas();
    }

    private void addDatas() {
        functionAdapter.add(new Function(R.drawable.find, "Find"));
        functionAdapter.add(new Function(R.drawable.search, "Search"));
    }

    private void addControls() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgMenu = findViewById(R.id.imgMenu);

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                /*LatLng latLng12 = new LatLng(10.848322, 106.774995);
                mMap.addMarker(new MarkerOptions().position(latLng12).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(latLng);
                polylineOptions.add(latLng12);
                mMap.addPolyline(polylineOptions.width(5).color(Color.GREEN));*/
            }
        };

        lvFunction = findViewById(R.id.lvFunction);
        functionAdapter = new FunctionAdapter(MainActivity.this, R.layout.item_function);

        lvFunction.setAdapter(functionAdapter);

        imgFunction = findViewById(R.id.imgPlus);

        BusStationListTask busStationListTask = new BusStationListTask();
        busStationListTask.execute();

        /*try {
            InetAddress addr = InetAddress.getByName("HOA");
            String ip = addr.getHostAddress();
            Log.e("IPNE", ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }*/
    }

    private void addEvents() {
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);*/

                MenuBottomSheetDialog menuBottomSheetDialog = new MenuBottomSheetDialog();
                menuBottomSheetDialog.setActivity(MainActivity.this);
                menuBottomSheetDialog.show(getSupportFragmentManager(), "BusCity");
            }
        });

        // Nhấn nút điều khiển function, nếu listview chứa function đang đóng thì mở ra và ngược lại
        imgFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lvFunction.isShown()){
                    imgFunction.setImageResource(R.drawable.plus);
                    imgFunction.animate().rotation(imgFunction.getRotation() - 360).start();
                    lvFunction.setVisibility( View.GONE);
                }
                else{
                    imgFunction.setImageResource(R.drawable.multiply);
                    imgFunction.animate().rotation(imgFunction.getRotation() - 360).start();
                    lvFunction.setVisibility( View.VISIBLE);
                }
                //lvFunction.setVisibility(lvFunction.isShown() ? View.GONE : View.VISIBLE);
            }
        });

        lvFunction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent intent =
                            new Intent(MainActivity.this, FindActivity.class);
                    startActivity(intent);
                }
                else if(position == 1){
                    Intent intent =
                            new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setStations(ArrayList<BusStation> busStations){

        for(BusStation busStation : busStations){
            LatLng latLng = new LatLng(busStation.getLatitude(), busStation.getLongtitude());
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop)));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Thiết lập tiêu đề
        builder.setTitle("Bus City");
        // Thiết lập nội dung cho hộp thoại
        builder.setMessage("Do you want to exit?");
        // Thiết lập các nút lệnh tương tác
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // finish: Xóa Activity hiện tại và quay ra Activity trước đó
                // finishAffinity: Xóa hết tất cả các Activity trong Task
                // Task là tập hợp các Activities mà người dùng tương tác khi thực hiện một công việc nhất định
                // .Một Task giữ các Activities, được sắp xếp trong một ngăn xếp(Stack) gọi là ngăn xếp ngược(Back Stack)
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ẩn hộp thoại đi
                dialog.dismiss();
            }
        });

        // Tạo hộp thoại
        AlertDialog dialog = builder.create();
        // Không đóng hộp thoại khi nhấn ở ngoài
        dialog.setCanceledOnTouchOutside(false);
        // Hiển thị hộp thoại lên
        dialog.show();
    }

    class BusStationListTask extends AsyncTask<Void, Void, ArrayList<BusStation>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<BusStation> busStations) {
            super.onPostExecute(busStations);
            Toast.makeText(MainActivity.this, "SỐ LƯỢNG TRẠM: " + busStations.size(), Toast.LENGTH_SHORT).show();
            setStations(busStations);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<BusStation> doInBackground(Void... voids) {
            ArrayList<BusStation> busStationList = new ArrayList<BusStation>();

            try {
                URL url = new URL("http://" + Host.ip + "/buscity/api/busstation");
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
                    Double latitude = jsonObject.getDouble("Latitude");
                    Double longtitude = jsonObject.getDouble("Longtitude");
                    String address = jsonObject.getString("Address");

                    BusStation busStation =
                            new BusStation(id, name, latitude, longtitude, address);

                    busStationList.add(busStation);
                }

                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (Exception e){
                Log.e("Error", e.toString());
            }

            return busStationList;
        }
    }
}
