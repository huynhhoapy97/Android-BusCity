package truonghuynhhoa.ptit.buscity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class LanguageActivity extends AppCompatActivity {

    private RadioButton radVietnamese, radEnglish;
    private Button btnClose, btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        this.setTitle("");
        addControls();
        addEvents();
    }

    private void addControls() {
        radVietnamese = findViewById(R.id.radVietnamese);
        radEnglish = findViewById(R.id.radEnglish);
        btnClose = findViewById(R.id.btnClose);
        btnOK = findViewById(R.id.btnOK);
    }

    private void addEvents() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LanguageActivity.this.finish();
            }
        });
    }
}
