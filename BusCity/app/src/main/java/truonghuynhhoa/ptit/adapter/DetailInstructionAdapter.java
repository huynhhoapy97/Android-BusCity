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
import truonghuynhhoa.ptit.model.DetailInstruction;

public class DetailInstructionAdapter extends ArrayAdapter<DetailInstruction> {

    private Activity context;
    private int resource;

    public DetailInstructionAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        View itemDetailInstruction = layoutInflater.inflate(this.resource, null);

        ImageView imgWalkOrBus = itemDetailInstruction.findViewById(R.id.imgWalkOrBus);
        TextView txtWalkOrBus = itemDetailInstruction.findViewById(R.id.txtWalkOrBus);
        TextView txtFromTo = itemDetailInstruction.findViewById(R.id.txtFromTo);

        DetailInstruction detailInstruction = getItem(position);
        imgWalkOrBus.setImageResource(detailInstruction.getImage());
        txtWalkOrBus.setText(detailInstruction.getWalkOrBus());
        txtFromTo.setText(detailInstruction.getFromTo());

        return itemDetailInstruction;
    }
}
