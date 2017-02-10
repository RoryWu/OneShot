package me.next.oneshot.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeXT on 17/2/10.
 */
public class PaletteView extends View {

    private Paint.Cap lineCap      = Paint.Cap.ROUND;
    private Paint.Style paintStyle = Paint.Style.STROKE;
    private float paintStrokeWidth = 8F;
    private int paintStrokeColor   = Color.BLACK;

    private float startX   = 0F;
    private float startY   = 0F;

    private int historyPointer = 0;
    private boolean isDown;

    private List<Path> pathList = new ArrayList<>();
    private List<Paint> paintList = new ArrayList<>();

    public PaletteView(Context context) {
        this(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pathList.add(new Path());
        paintList.add(createPaint());
        historyPointer++;
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(paintStyle);
        paint.setStrokeWidth(paintStrokeWidth);
        paint.setStrokeCap(lineCap);
        paint.setStrokeJoin(Paint.Join.MITER);  // fixed
        paint.setColor(paintStrokeColor);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.historyPointer; i++) {
            Path path   = this.pathList.get(i);
            Paint paint = this.paintList.get(i);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                updateHistory(createPath(event));
                isDown = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                Path path = getCurrentPath();
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (isDown) {
                    startX = 0F;
                    startY = 0F;
                    isDown = false;
                }
                break;
        }
        invalidate();
        return true;
    }

    private void updateHistory(Path path) {
        pathList.add(path);
        paintList.add(createPaint());
        historyPointer++;
    }

    private Path createPath(MotionEvent event) {
        Path path = new Path();
        startX = event.getX();
        startY = event.getY();
        path.moveTo(startX, startY);
        return path;
    }

    private Path getCurrentPath() {
        return pathList.get(historyPointer - 1);
    }

}