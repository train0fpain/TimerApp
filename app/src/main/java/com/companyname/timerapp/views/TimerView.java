package com.companyname.timerapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;
import com.companyname.timerapp.util.Start;

public class TimerView extends View {

    private Timer owner;

    private final int backgroundColor = Color.parseColor("#f0f0f0"),
            progressColor = Color.parseColor("#87c854"),
            finishedColor = Color.parseColor("#ff1806"),
            writingColor = Color.BLACK;
    private final Typeface font = Typeface.createFromAsset(Start.getContext().getAssets(), "fonts/Lato-Thin.ttf");

    private Paint backGroundPaint, progressPaint, textPaintName, textPaintTime;

    private int idX = 0;
    private int idY = 0;
    private boolean isActive = false;
    private int textSizeTime = 30;
    private int textSize = 30;
    private int textNameWidth=0;
    private String lastTimerName = "";



    public TimerView(Context context, int idX, int idY) {
        super(context);
        this.idX = idX;
        this.idY = idY;

        init();
    }

    public TimerView(Context context) {
        super(context);
        init();
    }

    public TimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){// prepare paint
        backGroundPaint = new Paint();
        backGroundPaint.setStyle(Paint.Style.FILL);
        backGroundPaint.setAntiAlias(false);
        progressPaint = new Paint(backGroundPaint);
        backGroundPaint.setColor(backgroundColor);
        progressPaint.setColor(progressColor);
        // text
        textPaintTime = new Paint();
        textPaintTime.setColor(writingColor);
        textPaintTime.setTextAlign(Paint.Align.CENTER);
        textPaintTime.setTextSize(textSizeTime);
        textPaintTime.setTypeface(font);
        textPaintName = new Paint(textPaintTime);
    }

    public void setOwner(Timer owner){
        this.owner = owner;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        // needed?
    }


    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.GRAY);
        System.out.println("draw call" + owner);
        if(isActive && owner != null){
            System.out.println("active and has owner" + owner.getName());
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            float progress = owner.getProgress();
            String name = owner.getName();
            int nameLength = name.length();
            refreshBasicTextSize(width);

            if (progress >= 1){
                progressPaint.setColor(finishedColor);
            }

            Rect progressRect = new Rect(0, (int) (height * clamp(1 - progress, 0, 1)), width, height);
            canvas.drawRect(progressRect, progressPaint);

            Rect textBounds = new Rect();
            textPaintName.getTextBounds(name, 0,nameLength, textBounds);

            adjustText(textBounds, width, name, canvas, height);
            canvas.drawText(owner.getTimeString(), width / 2, 3* height / 4 + textBounds.height()/2, textPaintTime);

            lastTimerName = name;
        }
    }

    public void requestDraw(){
        invalidate();
    }

    private void adjustText(Rect textBounds, int width, String name, Canvas canvas, int height){
        // break text if exeeds certain length
        final int charsPerLine = 19;
        if (name.length() > charsPerLine) {
            textSize = textSizeTime * 2 / 3;
            String[] tmp = {name.substring(0, charsPerLine),name.substring(charsPerLine)};
            textPaintName.setTextSize(textSize);
            System.out.println("part one: "+tmp[0]);
            System.out.println("part two: "+tmp[1]);
            canvas.drawText(tmp[0], width / 2f, height / 4 + textBounds.height() / 2 - textSize/2, textPaintName);
            canvas.drawText(tmp[1], width / 2f, height / 4 + textBounds.height() / 2 + textSize/2, textPaintName);
        }else{
            if (lastTimerName != name) {
                boolean adjustTextSize = true;
                while (adjustTextSize) {
                    if (textBounds.width() > (float) width * 0.96f && textSize >= textSizeTime /2) {
                        textSize--;
                        System.out.println("make smaller");
                    } else if (textBounds.width() < ((float) width) * 0.85f && textPaintName.getTextSize() < textSizeTime) {
                        textSize++;
                        System.out.println("make bigger");
                    } else {
                        adjustTextSize = false;
                        System.out.println("exit while");
                    }
                    textPaintName.setTextSize(textSize);
                    textPaintName.getTextBounds(name, 0, name.length(), textBounds);
                    textNameWidth = textBounds.width();
                }
            }
            canvas.drawText(name, width / 2f, height / 4 + textBounds.height() / 2, textPaintName);
        }
    }

    private void refreshBasicTextSize(int width){
        if (textSizeTime < width * 3 / 20){
            textSizeTime = width * 3 / 20;
            textPaintTime.setTextSize(textSizeTime);
            textSize = textSizeTime;
            textPaintName.setTextSize(textSizeTime);
        }
    }

    public void reset(){
        progressPaint.setColor(progressColor);
    }

    public void setActive(boolean active){
        isActive = active;
        if (!isActive){
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimerManager.addTimer(new Timer(null), true, idY * MainActivity.gridLayout.getColumnCount() + idX);
                }
            });
            this.setOnLongClickListener(null);


        }
    }

    public boolean isActive() {
        return isActive;
    }

    private float clamp(float in, float min, float max){
        if (in<min){
            return min;
        }else if (in > max) {
            return max;
        }else {
            return in;
        }
    }
}
