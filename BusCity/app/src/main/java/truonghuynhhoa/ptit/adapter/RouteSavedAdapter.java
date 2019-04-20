package truonghuynhhoa.ptit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import truonghuynhhoa.ptit.buscity.R;
import truonghuynhhoa.ptit.model.BusRoute;

public class RouteSavedAdapter extends ArrayAdapter<BusRoute> {

    private Activity context;
    private int resource;

    public RouteSavedAdapter(Activity context, int resource) {
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
        View itemRouteSaved = layoutInflater.inflate(this.resource, null);

        // controls nằm trong activity nào thì lấy activity đó gọi ra
        Button btnRouteSavedId = itemRouteSaved.findViewById(R.id.btnRouteSavedId);
        TextView txtRouteSavedId = itemRouteSaved.findViewById(R.id.txtRouteSavedId);
        TextView txtRouteSavedName = itemRouteSaved.findViewById(R.id.txtRouteSavedName);

        // position là vị trí của một đối tượng(route) trong array adapter
        BusRoute busRoute = getItem(position);
        btnRouteSavedId.setText(busRoute.getId().toString());
        txtRouteSavedId.setText("Route " + busRoute.getId().toString());
        txtRouteSavedName.setText(busRoute.getName());

        return itemRouteSaved;
    }
}
