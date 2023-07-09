package com.example.notificationsound;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private Button btnSelectSound;
    private EditText etTime;
    private Button btnPlayNotification;
    private Uri selectedRingtone;
    private ActivityResultLauncher<Intent> ringtoneLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing widgets
        btnSelectSound = findViewById(R.id.btnSelectSound);
        etTime = findViewById(R.id.etTime);
        btnPlayNotification = findViewById(R.id.btnPlayNotification);
        ringtoneLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedRingtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        }

                    }
                });

        btnSelectSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRingtone();
            }
        });

        btnPlayNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNotification();
            }
        });

    }

    private void selectRingtone() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtone);
        ringtoneLauncher.launch(intent);
    }

    private void playNotification() {
        String time = etTime.getText().toString();
        if (selectedRingtone != null) {
            if (time.isEmpty()){
                Toast.makeText(this, "Please enter the duration time", Toast.LENGTH_SHORT).show();
            }
            else {
                int duration = Integer.parseInt(time) * 1000; // convert to milliseconds
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setLooping(true);

                try {
                    mediaPlayer.setDataSource(MainActivity.this, selectedRingtone);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    // Stop the sound after the specified duration
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                    }, duration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        else {
            Toast.makeText(this, "Please select a ringtone", Toast.LENGTH_SHORT).show();
        }
    }

}