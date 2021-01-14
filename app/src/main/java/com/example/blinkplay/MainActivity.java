package com.example.blinkplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.KeyEventDispatcher;

import android.Manifest;
import android.app.Instrumentation;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView main_text;
    ConstraintLayout background;

    CameraSource cameraSource;
    boolean flag = false;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n", Toast.LENGTH_SHORT).show();
        } else {
            init();
        }
    }

    private void init() {
        main_text = findViewById(R.id.blink);
        background = findViewById(R.id.background);

        initCameraSource();
    }

    private void initCameraSource() {
        FaceDetector faceDetector = new FaceDetector.Builder(this).
                setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        faceDetector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(MainActivity.this)).build());

        cameraSource = new CameraSource.Builder(this, faceDetector)
                .setRequestedPreviewSize(1600, 1200)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraSource.start();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public void updateMainView(Condition condition){
        switch (condition){
            case USER_EYES_OPEN:
                setMainText("OPEN");
                if (flag) {
                    new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                    flag = false;
                }
                break;
            case USER_EYES_CLOSED:
                setMainText("closed");
                flag = true;

                break;
            case FACE_NOT_FOUND:
                flag = false;
                setMainText("Face not found");
                break;
            default:
                flag = false;
                setMainText("Hello World");
        }
    }

    //set background Orange
    private void setMainText(String text) {
        if(background != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    main_text.setText(text);
                }
            });
        }
    }
}