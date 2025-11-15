package com.example.test_pro.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.test_pro.R;
import com.example.test_pro.ultis.SizeUtils;

public class FaceDetectionOverlay extends View {
    private final Paint paint;
    private final RectF faceRect;

    public FaceDetectionOverlay(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.disableButtonPrimaryRed));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setStrokeCap(Paint.Cap.ROUND);
        int left = SizeUtils.getWidthPercent(0.1f);
        int top = SizeUtils.getHeightPercent(0.2f);
        int right = SizeUtils.getScreenWidth() - 50;
        int bottom = SizeUtils.getScreenHeight() - 200;
        faceRect = new RectF(left, top, right, bottom);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (faceRect != null) {
            float left = faceRect.left;
            float top = faceRect.top;
            float right = faceRect.right;
            float bottom = faceRect.bottom;
            float cornerLength = 80f;
            drawCorner(canvas, left, top, left + cornerLength, top, left, top + cornerLength);

            drawCorner(canvas, right, top, right - cornerLength, top, right, top + cornerLength);

            drawCorner(canvas, left, bottom, left + cornerLength, bottom, left, bottom - cornerLength);

            drawCorner(canvas, right, bottom, right - cornerLength, bottom, right, bottom - cornerLength);
        }
    }
    private void drawCorner(@NonNull Canvas canvas, float x1, float y1, float x2, float y2, float x3, float y3) {
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.moveTo(x1, y1);
        path.lineTo(x3, y3);
        canvas.drawPath(path, paint);
    }
}
