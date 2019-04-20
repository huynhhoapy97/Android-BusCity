package truonghuynhhoa.ptit.buscity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import truonghuynhhoa.ptit.adapter.MenuAdapter;
import truonghuynhhoa.ptit.model.Menu;

public class MenuActivity extends AppCompatActivity {

    ListView lvMenu;
    MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        addControls();
        addEvents();
        addDatas();
    }

    private void addDatas() {
        menuAdapter.add(new Menu(R.drawable.cloud, "Update data"));
        menuAdapter.add(new Menu(R.drawable.language, "Choose language"));
        menuAdapter.add(new Menu(R.drawable.detail, "View information"));
    }

    private void addControls() {
        lvMenu = findViewById(R.id.lvMenu);
        menuAdapter = new MenuAdapter(MenuActivity.this, R.layout.item_menu);

        lvMenu.setAdapter(menuAdapter);
    }

    private void addEvents() {

    }
}
