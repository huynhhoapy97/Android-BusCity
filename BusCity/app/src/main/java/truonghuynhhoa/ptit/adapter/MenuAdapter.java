package truonghuynhhoa.ptit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import truonghuynhhoa.ptit.buscity.R;
import truonghuynhhoa.ptit.model.Menu;

public class MenuAdapter extends ArrayAdapter<Menu> {
    private Activity context;
    private int resource;

    public MenuAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
            Công việc của LayoutInflater là đọc xml layout file
            và chuyển đổi các thuộc tính của nó thành 1 View trong Java code
          */
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View itemMenu = layoutInflater.inflate(this.resource, null);

        // controls nằm trong activity nào thì lấy activity đó gọi ra
        ImageView imgMenuItem = itemMenu.findViewById(R.id.imgMenuItem);
        TextView txtMenuItem = itemMenu.findViewById(R.id.txtMenuItem);

        // position là vị trí của một đối tượng(menu) trong array adapter
        Menu menu = getItem(position);
        imgMenuItem.setImageResource(menu.getImage());
        txtMenuItem.setText(menu.getDescription());

        return itemMenu;
    }
}
