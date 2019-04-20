package truonghuynhhoa.ptit.buscity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InformationActivity extends AppCompatActivity {

    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        this.setTitle("");
        this.setFinishOnTouchOutside(false);
        addControls();
        addEvents();
    }

    private void addControls() {
        btnClose = findViewById(R.id.btnClose);

    }

    private void addEvents() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformationActivity.this.finish();
            }
        });
    }
}
