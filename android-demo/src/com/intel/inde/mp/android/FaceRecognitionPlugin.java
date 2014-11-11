package com.intel.inde.mp.android;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;

import com.intel.inde.mp.IRecognitionPlugin;

public class FaceRecognitionPlugin
        implements IRecognitionPlugin
{
    private static final int MAX_FACES = 10;
    private FaceDetector faceDetector;
    private int lastBitmapWidth;
    private int lastBitmapHeight;

    public void start()
    {
    }

    public void stop()
    {
    }

    public IRecognitionPlugin.RecognitionOutput recognize(
            IRecognitionPlugin.RecognitionInput input)
    {
        FaceRecognitionInput recognitioInput = null;

        if ((input instanceof FaceRecognitionInput))
            recognitioInput = (FaceRecognitionInput) input;
        else {
            throw new IllegalArgumentException("Invalid arguments.");
        }

        Bitmap bitmap = recognitioInput.getBitmap();

        if ((this.lastBitmapWidth != bitmap.getWidth())
                || (this.lastBitmapHeight != bitmap.getHeight())) {
            this.lastBitmapWidth = bitmap.getWidth();
            this.lastBitmapHeight = bitmap.getHeight();

            this.faceDetector = new FaceDetector(this.lastBitmapWidth,
                    this.lastBitmapHeight,
                    10);
        }

        FaceRecognitionOutput result = new FaceRecognitionOutput();

        FaceDetector.Face[] faces = new FaceDetector.Face[10];

        int facesDetected = this.faceDetector.findFaces(bitmap, faces);

        PointF midPoint = new PointF();

        for (int i = 0; i < facesDetected; i++) {
            Face face = new Face();

            face.mId = 0;

            face.mConfidence = faces[i].confidence();
            face.mEyeDistance = faces[i].eyesDistance();

            faces[i].getMidPoint(midPoint);

            face.mMidPointX = midPoint.x;
            face.mMidPointY = midPoint.y;

            result.add(face);
        }

        return result;
    }

    public static class FaceRecognitionOutput extends
            IRecognitionPlugin.RecognitionOutput
    {
        private List<FaceRecognitionPlugin.Face> facesList;

        public FaceRecognitionOutput()
        {
            this.facesList = new ArrayList();
        }

        public void add(FaceRecognitionPlugin.Face face) {
            this.facesList.add(face);
        }

        public int size() {
            return this.facesList.size();
        }

        public FaceRecognitionPlugin.Face get(int index) {
            return (FaceRecognitionPlugin.Face) this.facesList.get(index);
        }
    }

    public static class FaceRecognitionInput extends
            IRecognitionPlugin.RecognitionInput
    {
        private Bitmap bitmap;

        public FaceRecognitionInput(Bitmap bitmap)
        {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }
    }

    public class Face
    {
        private int mId;
        private float mConfidence;
        private float mMidPointX;
        private float mMidPointY;
        private float mEyeDistance;

        private Face()
        {
        }

        public int getId()
        {
            return this.mId;
        }

        public void getBounds(RectF bounds) {
            bounds.set(this.mMidPointX - 20.0F,
                    this.mMidPointY - 20.0F,
                    this.mMidPointX + 20.0F,
                    this.mMidPointY + 20.0F);
        }

        public float getConfidence() {
            return this.mConfidence;
        }

        public void getMidPoint(PointF midPoint) {
            midPoint.set(this.mMidPointX, this.mMidPointY);
        }

        public float getEyeDistance() {
            return this.mEyeDistance;
        }
    }
}

/*
 * Location: E:\SouceCode\recordGame\gdxDemo\libs\android-1.2.2415.jar
 * Qualified Name: com.intel.inde.mp.android.FaceRecognitionPlugin
 * JD-Core Version: 0.6.1
 */
