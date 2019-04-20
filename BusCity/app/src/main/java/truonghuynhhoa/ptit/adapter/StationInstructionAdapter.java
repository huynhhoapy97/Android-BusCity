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
import truonghuynhhoa.ptit.model.StationInstruction;

public class StationInstructionAdapter extends ArrayAdapter<StationInstruction> {

    private Activity context;
    private int resource;

    public StationInstructionAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View itemStaionInstruction = layoutInflater.inflate(this.resource, null);

        ImageView imgPoint = itemStaionInstruction.findViewById(R.id.imgPoint);
        TextView txtStationInstruction = itemStaionInstruction.findViewById(R.id.txtStationInstruction);

        StationInstruction stationInstruction = getItem(position);
        imgPoint.setImageResource(stationInstruction.getImage());
        txtStationInstruction.setText(stationInstruction.getName());

        return itemStaionInstruction;
    }
}
