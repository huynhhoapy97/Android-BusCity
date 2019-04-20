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
import truonghuynhhoa.ptit.model.Function;

public class FunctionAdapter extends ArrayAdapter<Function> {

    private Activity context;
    private int resource;

    public FunctionAdapter(Activity context, int resource) {
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
        View itemFunction = layoutInflater.inflate(this.resource, null);

        // controls nằm trong activity nào thì lấy activity đó gọi ra
        ImageView imgFunctionItem = itemFunction.findViewById(R.id.imgFunctionItem);
        TextView txtFunctionItem = itemFunction.findViewById(R.id.txtFunctionItem);

        // position là vị trí của một đối tượng(function) trong array adapter
        Function function = getItem(position);
        imgFunctionItem.setImageResource(function.getImage());
        txtFunctionItem.setText(function.getDescription());

        return itemFunction;
    }
}
