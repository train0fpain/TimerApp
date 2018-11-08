package com.companyname.timerapp.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class LinkLine extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Float startX = null;
    private Float startY = null;
    private float endX;
    private float endY;
    private boolean drawLine = false;

    public LinkLine(Context context) {
        super(context);
    }

    public LinkLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawLine) {
            // make offset from circle center
            canvas.drawLine(startX, startY, endX, endY, paint);
            canvas.drawCircle(startX, startY, 8, paint);
        }
    }

    public void drawLine(Vector2f end){
        drawLine = true;
        if (startX == null){
            startX = end.x;
            startY = end.y;
        }
        this.endX = end.x;
        this.endY = end.y;
        System.out.println(String.format("Draw at start(%f, %f) to end(%f, %f)", startX, startY, endX, endY));
        invalidate();
    }

    public void drawLine(Vector2f start, Vector2f end){
        this.startX = start.x;
        this.startY = start.y;
        this.endX = end.x;
        this.endY = end.y;
        invalidate();
    }

    public void drawLine(float startX, float startY, float endX, float endY){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        invalidate();
    }

    public void drawLine(float endX, float endY){
        this.endX = endX;
        this.endY = endY;
        invalidate();
    }

    public void stopDrawLine(){
        drawLine = false;
        startX = null;
        startY = null;
        invalidate();
    }

    public void setDrawLine(boolean drawLine) {
        this.drawLine = drawLine;
    }
}
