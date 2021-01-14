package com.example.blinkplay;

import android.content.Context;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class EyesTracker extends Tracker<Face> {
    private final float THRESHOLD = 0.75f;
    private Context context;

    public EyesTracker(Context context) {
        this.context = context;
    }

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
            ((MainActivity)context).updateMainView(Condition.USER_EYES_OPEN);
        } else {
            ((MainActivity)context).updateMainView(Condition.USER_EYES_CLOSED);
        }
    }

    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);

        ((MainActivity)context).updateMainView(Condition.FACE_NOT_FOUND);
    }

    @Override
    public void onDone() {
        super.onDone();
    }
}
