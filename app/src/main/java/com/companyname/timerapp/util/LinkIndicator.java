package com.companyname.timerapp.util;

import android.graphics.Color;
import android.graphics.Paint;

public class LinkIndicator {
    private int[] colors = {Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#0000ff"),
                            Color.parseColor("#000055")};

    private Paint paint = new Paint();

    private int link = -1;

    private int radius;

    public LinkIndicator(int radius) {
        this.radius = radius;
        paint.setAntiAlias(true);
        paint.setStrokeWidth(radius);
    }

    public boolean shouldDraw(){
        return link >= 0 && link <10;
    }

    public int getColor(){
        if (shouldDraw()) {
            return colors[link];
        }else{
            return -1;
        }
    }

    public int getLink() {
        return link;
    }

    public void setLink(int link) {
        this.link = link;
        if (link >= 0 && link < 10){
            paint.setColor(colors[link]);
        }
    }

    public int getRadius() {
        return radius;
    }

    public Paint getPaint() {
        return paint;
    }
}
