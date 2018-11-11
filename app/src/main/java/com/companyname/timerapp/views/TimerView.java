package com.companyname.timerapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

import com.companyname.timerapp.MainActivity;
import com.companyname.timerapp.R;
import com.companyname.timerapp.modesAndStates.TimerState;
import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;
import com.companyname.timerapp.linking.LinkManager;
import com.companyname.timerapp.util.Start;
import com.companyname.timerapp.modesAndStates.UserMode;
import com.companyname.timerapp.util.Vector2f;

public class TimerView extends View {

    private Timer owner;
    private LinkManager linkManager = LinkManager.getInstance();
    private LinkIndicator linkIndicator;

    private final int backgroundColor = getResources().getColor(R.color.timerBackground),
            progressColor = getResources().getColor(R.color.timerProgress),
            finishedColor = getResources().getColor(R.color.timerFinished),
            finishedRunningColor = getResources().getColor(R.color.timerFinishedRunning),
            writingColor = getResources().getColor(R.color.timerWriting);

    private final Typeface font = Typeface.createFromAsset(Start.getContext().getAssets(), "fonts/Lato-Regular.ttf");

    private Paint backGroundPaint, progressPaint, textPaintName, textPaintTime;

    private int idX = 0;
    private int idY = 0;
    private int height = 0;
    private int width = 0;
    private boolean isActive = false;
    private int textSizeTime = 26;
    private int textSize = 26;

    private Vector2f pos;

    RectF progressRect = new RectF();
    Rect textBounds = new Rect();

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

    private void init(){
        // prepare paint
        linkIndicator = new LinkIndicator(textSizeTime*3/4);

        backGroundPaint = new Paint();
        backGroundPaint.setStyle(Paint.Style.FILL);
        backGroundPaint.setAntiAlias(true);
        progressPaint = new Paint(backGroundPaint);
        backGroundPaint.setColor(backgroundColor);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);
        // text
        textPaintTime = new Paint();
        textPaintTime.setColor(writingColor);
        textPaintTime.setTextAlign(Paint.Align.CENTER);
        textPaintTime.setTextSize(textSizeTime);
        textPaintTime.setTypeface(font);
        textPaintTime.setAntiAlias(true);
        textPaintName = new Paint(textPaintTime);

        final TimerView view = this;
        this.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int dragEvent = event.getAction();
                switch (dragEvent){
                    case DragEvent.ACTION_DRAG_LOCATION:
                        if (TimerManager.getUserMode() == UserMode.LINK) {
                            linkManager.getLinkLine().drawLine(getTouchPos(event));
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        if (TimerManager.getUserMode() == UserMode.EDIT) {
                            if (isActive) {
                                if (event.getLocalState() != view) {
                                    TimerManager.swappSlot((TimerView) event.getLocalState(), view);
                                }
                            } else {
                                TimerManager.dropIntoSlot((TimerView) event.getLocalState(), view);
                            }
                        }else if (TimerManager.getUserMode() == UserMode.LINK){
                            if (owner != null) {
                                linkManager.linkTimer(owner);
                            }
                            linkManager.getLinkLine().stopDrawLine();
                        }
                        break;
                }

                return true;
            }
        });
    }

    private Vector2f getTouchPos(DragEvent event){
        return new Vector2f(pos.x + event.getX(), pos.y + event.getY());
    }

    public void setOwner(Timer owner){
        this.owner = owner;
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (pos == null) {
            int[] out = new int[2];
            int[] offsetTop = new int[2];
            ((View)getParent()).getLocationInWindow(offsetTop);
            getLocationInWindow(out);
            pos = new Vector2f(out[0], out[1] - offsetTop[1]);
        }
        width = this.getMeasuredWidth();
        height = this.getMeasuredHeight();
        canvas.drawRoundRect(new RectF(0, 0, (float)width, height), 30, 30, backGroundPaint);
        if(isActive && owner != null){

            float progress = owner.getProgress();
            String name = owner.getName();
            int nameLength = name.length();
            refreshBasicTextSize(width);

            if (progress >= 1 && owner.getTimerState() != TimerState.FINISHED_PAUSE){
                progressPaint.setColor(finishedRunningColor);
            }

            if (progress < 0.35f){
                progressRect.set(new RectF((0.35f-progress)*80, (int) (height * clamp(1 - progress, 0, 1)), width-(0.35f-progress)*80, height));
            }else {
                progressRect.set(new RectF(0, (int) (height * clamp(1 - progress, 0, 1)), width, height));
            }
            canvas.drawRoundRect(progressRect,30,30, progressPaint);

            textPaintName.getTextBounds(name, 0,nameLength, textBounds);

            adjustText(textBounds, width, name, canvas, height);
            canvas.drawText(owner.getTimeString(), width / 2, 3* height / 4 + textBounds.height()/2, textPaintTime);
        }

        if (linkIndicator.shouldDraw()){
            canvas.drawCircle((width / 10), (height / 10) * 8, linkIndicator.getRadius(), linkIndicator.getPaint());
            canvas.drawText(Integer.toString(linkIndicator.getLink()), (width / 10), (height / 10) * 8 + textBounds.height()/2, textPaintTime);
        }
    }

    public void requestDraw(){
        invalidate();
    }

    public void setColorForFinishedPause(){
        progressPaint.setColor(finishedColor);
        invalidate();
    }

    private void adjustText(Rect textBounds, int width, String name, Canvas canvas, int height){
        // break text if exeeds certain length
        final int charsPerLine = 19;
        textSize = (textSizeTime+(int)(textSizeTime*0.2f)) * 2 / 3;
        textPaintName.setTextSize(textSize);
        if (name.length() > charsPerLine) {
            String[] tmp = {name.substring(0, charsPerLine),name.substring(charsPerLine)};
            canvas.drawText(tmp[0], width / 2f, height / 4 + textBounds.height() / 2 - textSize/2, textPaintName);
            canvas.drawText(tmp[1], width / 2f, height / 4 + textBounds.height() / 2 + textSize/2, textPaintName);
        }else{
            canvas.drawText(name, width / 2f, height / 4 + textBounds.height() / 2, textPaintName);
        }
    }

    private void refreshBasicTextSize(int width){
        if (textSizeTime < (width * 3 / 20)*0.9f){
            textSizeTime = (int)((width * 3 / 20)*0.9f);
            textPaintTime.setTextSize(textSizeTime);
            textSize = textSizeTime;
            textPaintName.setTextSize(textSizeTime);
        }
    }

    public void reset(){
        progressPaint.setColor(progressColor);
        invalidate();
    }

    public void setActive(boolean active){
        isActive = active;
        if (!isActive){
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimerManager.addTimer(new Timer(null), true, getIndex());
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

    public Timer getOwner(){
        return owner;
    }

    public int getIndex(){
        return idY * MainActivity.gridLayout.getColumnCount() + idX;
    }

    public LinkIndicator getLinkIndicator() {
        return linkIndicator;
    }

    public void setViewToEmpty(){
        owner = null;
        isActive = false;
        linkIndicator.setLink(-1);
        invalidate();
    }
}
