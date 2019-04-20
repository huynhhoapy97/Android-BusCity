package truonghuynhhoa.ptit.buscity;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScreenActivity extends AppCompatActivity {

    private ProgressBar progressScreen;
    private TextView txtLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        addControls();
        addEvents();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addControls() {
        progressScreen = findViewById(R.id.progressScreen);
        txtLoaded = findViewById(R.id.txtLoaded);

        ProgressScreenAnimation progressScreenAnimation =
                new ProgressScreenAnimation(
                        ScreenActivity.this,
                        progressScreen,
                        txtLoaded,
                        0f,
                        100f);

        progressScreenAnimation.setDuration(10000);

        progressScreen.setMax(100);
        progressScreen.setScaleY(3f);
        // Đổi màu áp dụng cho API 21+
        progressScreen.setProgressTintList(ColorStateList.valueOf(Color.WHITE));
        progressScreen.setAnimation(progressScreenAnimation);
    }

    private void addEvents() {
    }
}
