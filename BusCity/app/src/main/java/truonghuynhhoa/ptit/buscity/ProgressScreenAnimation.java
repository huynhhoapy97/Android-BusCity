package truonghuynhhoa.ptit.buscity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressScreenAnimation extends Animation {
    private Activity context;
    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float to;

    public ProgressScreenAnimation(Activity context, ProgressBar progressBar, TextView textView, float from, float to) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    // Aps dụng biến đổi
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int)value);
        textView.setText((int)value + " %");

        if(value == to){
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }
}
