package com.companyname.timerapp.linking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.companyname.timerapp.util.Vector2f;

public class LinkLine extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Vector2f start;
    Vector2f end = new Vector2f();
    private boolean drawLine = false;

    public LinkLine(Context context) {
        super(context);
        init();
    }

    public LinkLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawLine) {
            canvas.drawCircle(start.x, start.y, 8, paint);
            canvas.drawCircle(end.x, end.y, 8, paint);
            // make offset from circle center
            Vector2f offset = Vector2f.sub(end, start);
            offset.normalize().scale(8);
            end.sub(offset);
            offset.add(start);
            canvas.drawLine(offset.x, offset.y, end.x, end.y, paint);
        }
    }

    public void drawLine(Vector2f end){
        drawLine = true;
        if (start == null){
            start = new Vector2f(end.x, end.y);
        }
        this.end.x = end.x;
        this.end.y = end.y;
        invalidate();
    }

    public void stopDrawLine(){
        drawLine = false;
        start = null;
        invalidate();
    }
}
