package com.companyname.timerapp.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ImprovedOnTouchListener implements OnTouchListener {

    private final double LONG_CLICK_TIME = 300d; // miliseconds
    private final float MOVE_TOLERANCE = 15f; // pixel

    private Vector2f downPos = new Vector2f();
    private Vector2f nowPos = new Vector2f();
    private double downTime;
    private boolean dragStarted = false;

    ImprovedOnTouchInterface listener;

    public ImprovedOnTouchListener(ImprovedOnTouchInterface listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dragStarted = false;
                downPos.x = event.getX();
                downPos.y = event.getY();
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!dragStarted){
                    nowPos.x = event.getX();
                    nowPos.y = event.getY();
                    if (Vector2f.sub(nowPos, downPos).length() > MOVE_TOLERANCE){
                        if (System.currentTimeMillis() - downTime < LONG_CLICK_TIME){
                            // start short drag
                            listener.onDrag();
                        }else{
                            //start long drag
                            listener.onLongDrag();
                        }
                        dragStarted = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                double deltaTime = System.currentTimeMillis() - downTime;
                if (!dragStarted){
                    if (deltaTime < LONG_CLICK_TIME){
                        listener.onClick();
                    }else{
                        // long click
                        listener.onLongClick();
                    }

                }
                break;
        }
        return false;
    }


}
