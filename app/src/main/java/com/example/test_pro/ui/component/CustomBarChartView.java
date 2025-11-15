package com.example.test_pro.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.test_pro.R;

public class CustomBarChartView extends View {
    private Paint paintBar, paintCircle;
    private float[] values = {3.5f, 4.0f, 4.5f, 5.0f, 4.8f, 3.9f};
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};
    private Bitmap[] icons;

    public CustomBarChartView(Context context) {
        super(context);
        init(context);
    }

    public CustomBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        paintBar = new Paint();
        paintBar.setStyle(Paint.Style.FILL);

        paintCircle = new Paint();
        paintCircle.setColor(Color.BLACK);
        paintCircle.setStyle(Paint.Style.FILL);

        // Load icon từ drawable (hoặc có thể để null nếu không muốn dùng icon)
//        icons = new Bitmap[]{
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon1),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon2),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon3),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon4),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon5),
//                BitmapFactory.decodeResource(context.getResources(), R.drawable.icon6)
//        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int barHeight = height / (values.length * 2);
        int startX = 200;
        float maxValue = 5.0f;

        for (int i = 0; i < values.length; i++) {
            float barWidth = (values[i] / maxValue) * (width - startX - 100);
            int y = (i + 1) * 2 * barHeight;

            paintCircle.setColor(colors[i]);
            canvas.drawCircle(100, y - barHeight / 2, 40, paintCircle);

            if (icons[i] != null) {
                canvas.drawBitmap(icons[i], 70, y - barHeight, null);
            }

            paintBar.setColor(colors[i]);
            canvas.drawRect(startX, y - barHeight, startX + barWidth, y, paintBar);
        }
    }
}
