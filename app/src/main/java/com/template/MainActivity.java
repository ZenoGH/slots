package com.template;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ProgressBar loadingBar;
    private TextView percentageText;
    private ImageButton playButton;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        loadingBar = findViewById(R.id.loadingBar);
        percentageText = findViewById(R.id.percentageTextView);
        playButton = findViewById(R.id.playButton);
        load();
        playButton.setOnClickListener(v -> {
            startSlotsActivity();
        });
        Utils.addButtonResponsiveness(playButton);
    }

    public void load() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                loadingBar.setProgress(loadingBar.getProgress() + 1);
                runOnUiThread(() -> percentageText.setText(String.valueOf(loadingBar.getProgress()) + "%"));
                if (loadingBar.getProgress() == loadingBar.getMax()) {
                    runOnUiThread(() -> {
                        loadingBar.setVisibility(View.INVISIBLE);
                        percentageText.setVisibility(View.INVISIBLE);
                        playButton.setVisibility(View.VISIBLE);
                        timer.cancel();
                    });
                }
            }
        };
        timer.schedule(timerTask, 0, 20);
    }

    public void startSlotsActivity() {
        Intent newActivity = new Intent(this, SlotsActivity.class);
        newActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(newActivity);
    }
}