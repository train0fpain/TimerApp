package com.companyname.timerapp.views;

import android.graphics.Color;
import android.graphics.Paint;

public class LinkIndicator {
    private int[] colors = {Color.parseColor("#800000"),
            Color.parseColor("#42d4f4"),
            Color.parseColor("#4363d8"),
            Color.parseColor("#911eb4"),
            Color.parseColor("#f032e6"),
            Color.parseColor("#e6beff"),
            Color.parseColor("#808000"),
            Color.parseColor("#9A6324"),
            Color.parseColor("#469990"),
            Color.parseColor("#000075")};

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
