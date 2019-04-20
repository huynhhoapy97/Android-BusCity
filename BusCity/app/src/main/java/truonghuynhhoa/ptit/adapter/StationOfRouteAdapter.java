package truonghuynhhoa.ptit.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import truonghuynhhoa.ptit.buscity.R;
import truonghuynhhoa.ptit.model.StationOfRoute;

public class StationOfRouteAdapter extends ArrayAdapter<StationOfRoute> {

    private Activity context;
    private int resource;

    public StationOfRouteAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View itemStaionOfRoute = layoutInflater.inflate(this.resource, null);

        TextView txtStationName = itemStaionOfRoute.findViewById(R.id.txtStationName);
        TextView txtNextStationTime = itemStaionOfRoute.findViewById(R.id.txtNextStationTime);

        StationOfRoute stationOfRoute = getItem(position);
        txtStationName.setText(stationOfRoute.getStation().getName());
        txtNextStationTime.setText("+" + stationOfRoute.getNextStationTime().toString() + " minutes");

        return itemStaionOfRoute;
    }
}
