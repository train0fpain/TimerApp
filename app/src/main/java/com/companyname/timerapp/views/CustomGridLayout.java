package com.companyname.timerapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.GridLayout;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;
import com.companyname.timerapp.linking.LinkManager;

public class CustomGridLayout extends GridLayout {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean drawLine = false;
    private boolean moved = false;

    private LinkManager linkManager = LinkManager.getInstance();

    public CustomGridLayout(Context context) {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6);
    }

    public CustomGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(6);
    }

    public CustomGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        // return true to pass on to onTouchEvent
        // else handle in child
        if (TimerManager.getUserMode() == UserMode.LINK) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    Timer startTimer = calculateChild(startX, startY);
                    if (startTimer != null){
                        linkManager.setStartTimer(startTimer);
                        linkManager.getLinkLine().setDrawLine(true);
                        linkManager.getLinkLine().drawLine(startX, startY, startX, startY);
                    }
                    return false;
                case MotionEvent.ACTION_MOVE:
                    endX = event.getX();
                    endY = event.getY();
                    linkManager.getLinkLine().drawLine(startX, startY, endX, endY);
                    moved = true;
                    return false;
                case MotionEvent.ACTION_UP:
                    endX = event.getX();
                    endY = event.getY();
                    Timer timer = calculateChild(endX, endY);
                    if (moved) {

                        linkManager.linkTimer(timer);
                    }else{
                        if (timer.getLinkId() >= 0) {
                            linkManager.removeFromLink(timer);
                        }
                    }
                    linkManager.getLinkLine().stopDrawLine();
                    moved = false;
                    return false;
            }
            return false;
        }else {
            return false;
        }
    }*/

    private Timer calculateChild(float x, float y){
        float cellWidth = getChildAt(0).getWidth() + MainActivity.MARGIN*2;
        float cellHeight = getChildAt(0).getHeight() + MainActivity.MARGIN*2;
        int xIndex =(int) (x / cellWidth);
        int yIndex =(int) (y / cellHeight);
        return TimerManager.getTimer(yIndex * getColumnCount() + xIndex);
    }


}
