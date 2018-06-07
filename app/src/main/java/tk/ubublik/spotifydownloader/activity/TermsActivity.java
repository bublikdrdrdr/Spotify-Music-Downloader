package tk.ubublik.spotifydownloader.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tk.ubublik.spotifydownloader.R;
import tk.ubublik.spotifydownloader.util.AppPreferences;
import tk.ubublik.spotifydownloader.util.Appearance;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Appearance.setFullscreenFlags(this);
        Appearance.setLayoutUnderStatusBar(this, R.id.mainLayout);
        findViewById(R.id.acceptButton).setOnClickListener(v -> {
            new AppPreferences(TermsActivity.this).setTermsAccepted(true);
            setResult(RESULT_OK);
            finish();
        });
    }
}
