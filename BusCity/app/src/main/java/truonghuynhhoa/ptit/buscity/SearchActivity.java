package truonghuynhhoa.ptit.buscity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import truonghuynhhoa.ptit.adapter.RouteAdapter;
import truonghuynhhoa.ptit.adapter.RouteSavedAdapter;
import truonghuynhhoa.ptit.model.BusRoute;
import truonghuynhhoa.ptit.model.BusTurn;
import truonghuynhhoa.ptit.model.Host;

public class SearchActivity extends AppCompatActivity {

    private ListView lvRoutes, lvRouteSaved;
    private RouteAdapter routeAdapter;
    private RouteSavedAdapter routeSavedAdapter;
    private EditText edtSearch;

    private ArrayList<BusRoute> busRouteList;
    private ArrayList<BusRoute> busRouteSavedList;

    private TabHost tabHost;

    private SharedPreferences sharedPreferences;

    private boolean isWifi;
    private boolean isMobile;

    // Tên CSDL
    private String DATABASE_NAME = "buscity.sqlite";
    // Cho phép truy vấn hoặc tương tác với CSDL
    private SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Không hiển thị title trên Action Bar
        this.setTitle("");

        addControls();
        addEvents();
    }

    private void showBusRouteList() {
        // Bước 1: mở CSDL, có thì mở, không có thì tạo mới CSDL không có bảng nào
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        // Cursor cursor = database.query("BusRoute", null, null,null,null,null, null);
        Cursor cursor = database.rawQuery("select * from BusRoute", null);

        busRouteList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);

            BusRoute busRoute =
                    new BusRoute(id, name);

            busRouteList.add(busRoute);
        }

        // đóng kết nối
        cursor.close();

        routeAdapter.clear();
        routeAdapter.addAll(busRouteList);
    }

    private void addControls() {
        busRouteSavedList = new ArrayList<BusRoute>();
        busRouteList = new ArrayList<BusRoute>();

        edtSearch = findViewById(R.id.edtSearch);

        tabHost = findViewById(R.id.tabHost);
        // Cài đặt giao diện cho tabHost, nếu không chạy lên sẽ rỗng
        tabHost.setup();

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab1 = tabHost.newTabSpec("t1");
        // Thiết lập nội dung
        tab1.setContent(R.id.tab1);

        // Đọc xml layout tab_information và biến đổi các View trong nó thành java code
        LinearLayout tabRouteAllLinear = findViewById(R.id.tab1);
        tabRouteAllLinear.removeAllViews();

        LayoutInflater layoutRouteAll = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabRouteAll = layoutRouteAll.inflate(R.layout.tab_route_all, tabRouteAllLinear, true);

        lvRoutes = tabRouteAll.findViewById(R.id.lvRoutes);
        routeAdapter = new RouteAdapter(SearchActivity.this, R.layout.item_route);

        lvRoutes.setAdapter(routeAdapter);

        // Thiết lập tiêu đề
        tab1.setIndicator("All");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab1);

        // Một tab sẽ là 1 tabSpec có id tà t?
        TabHost.TabSpec tab2 = tabHost.newTabSpec("t2");
        // Thiết lập nội dung
        tab2.setContent(R.id.tab2);

        // Đọc xml layout tab_information và biến đổi các View trong nó thành java code
        LinearLayout tabRouteSavedLinear = findViewById(R.id.tab2);
        tabRouteSavedLinear.removeAllViews();

        LayoutInflater layoutRouteSaved = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabRouteSaved = layoutRouteSaved.inflate(R.layout.tab_route_saved, tabRouteSavedLinear, true);

        sharedPreferences = getSharedPreferences("routes", MODE_PRIVATE);

        lvRouteSaved = tabRouteSaved.findViewById(R.id.lvRouteSaved);
        routeSavedAdapter = new RouteSavedAdapter(SearchActivity.this, R.layout.item_route_saved);

        lvRouteSaved.setAdapter(routeSavedAdapter);

        // Thiết lập tiêu đề
        tab2.setIndicator("Saved");

        // Đưa tab1 vào tabHost
        tabHost.addTab(tab2);

        // Kiểm tra trạng thái kết nối mạng của điện thoại
        checkNetwork();

        if(isWifi == false && isMobile == false){
            Toast.makeText(SearchActivity.this, "YOU ARE OFFLINE", Toast.LENGTH_SHORT).show();
            showBusRouteList();
        }
        else{
            Toast.makeText(SearchActivity.this, "YOU ARE ONLINE", Toast.LENGTH_SHORT).show();

            // Triệu gọi tiểu trình cho nó chạy
            BusRouteListTask busRouteListTask = new BusRouteListTask();
            busRouteListTask.execute();
            try {
                busRouteList = busRouteListTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addEvents() {
        lvRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent =
                        new Intent(SearchActivity.this, RouteActivity.class);

                BusRoute busRoute = (BusRoute) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("routeId", busRoute.getId());
                bundle.putString("routeName", busRoute.getName());

                intent.putExtras(bundle);

                startActivity(intent);

            }
        });

        lvRouteSaved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =
                        new Intent(SearchActivity.this, RouteActivity.class);

                BusRoute busRoute = (BusRoute) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("routeId", busRoute.getId());
                bundle.putString("routeName", busRoute.getName());

                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<BusRoute> busRoutes = new ArrayList<BusRoute>();
                ArrayList<BusRoute> busRouteSaved = new ArrayList<BusRoute>();

                if(s != null && s.length() > 0){
                    for(int i = 0; i < busRouteList.size(); i++){
                        if(busRouteList.get(i).getId().toString().contains(s.toString())){
                            busRoutes.add(busRouteList.get(i));
                        }
                    }

                    for(int j = 0; j < busRouteSavedList.size(); j++){
                        if(busRouteSavedList.get(j).getId().toString().contains(s.toString())){
                            busRouteSaved.add(busRouteSavedList.get(j));
                        }
                    }

                    routeAdapter.clear();
                    routeAdapter.addAll(busRoutes);

                    routeSavedAdapter.clear();
                    routeSavedAdapter.addAll(busRouteSaved);
                }
                else{
                    routeAdapter.clear();
                    routeAdapter.addAll(busRouteList);

                    routeSavedAdapter.clear();
                    routeSavedAdapter.addAll(busRouteSavedList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("t1")){
                    Toast.makeText(SearchActivity.this,
                            "All selected",
                            Toast.LENGTH_SHORT).show();
                }
                if(tabId.equals("t2")){
                    Toast.makeText(SearchActivity.this,
                            "Saved selected",
                            Toast.LENGTH_SHORT).show();

                    ArrayList<BusRoute> busRouteSaved = new ArrayList<BusRoute>();

                    busRouteSavedList.clear();

                    for(BusRoute busRoute : busRouteList){
                        int routeId = sharedPreferences.getInt(busRoute.getId().toString(), -1);
                        if(routeId != -1){
                            busRouteSavedList.add(busRoute);
                        }
                    }

                    String filter = edtSearch.getText().toString();
                    if(filter.equals("")){
                        routeSavedAdapter.clear();
                        routeSavedAdapter.addAll(busRouteSavedList);
                    }
                    else{
                        for(int i = 0; i < busRouteSavedList.size(); i++){
                            if(busRouteSavedList.get(i).getId().toString().contains(filter.toString())){
                                busRouteSaved.add(busRouteSavedList.get(i));
                            }
                        }

                        routeSavedAdapter.clear();
                        routeSavedAdapter.addAll(busRouteSaved);
                    }
                }
            }
        });
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


        Toast.makeText(SearchActivity.this, "Wifi connected: " + isWifi, Toast.LENGTH_SHORT).show();
        Toast.makeText(SearchActivity.this, "Mobile connected: " + isMobile, Toast.LENGTH_SHORT).show();
    }

    class BusRouteListTask extends AsyncTask<Void, Void, ArrayList<BusRoute>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // Nhận kết quả trả về sau khi kết thúc 1 tiểu trình
        @Override
        protected void onPostExecute(ArrayList<BusRoute> busRoutes) {
            super.onPostExecute(busRoutes);
            routeAdapter.clear();
            routeAdapter.addAll(busRoutes);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        // Xử lý dữ liệu với Web Service
        @Override
        protected ArrayList<BusRoute> doInBackground(Void... voids) {
            ArrayList<BusRoute> busRouteList = new ArrayList<BusRoute>();

            try {
                URL url = new URL("http://" + Host.ip + "/buscity/api/busroute");
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

                    BusRoute busRoute =
                    new BusRoute(id, name);

                    busRouteList.add(busRoute);
                }

                bufferedReader.close();
                inputStreamReader.close();
            }
            catch (Exception e){
                Log.e("Error", e.toString());
            }

            return busRouteList;
        }
    }
}
