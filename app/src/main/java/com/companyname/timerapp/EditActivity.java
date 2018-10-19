package com.companyname.timerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.companyname.timerapp.timerClasses.TimeFormat;
import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;

public class EditActivity extends AppCompatActivity {

    private int index;
    private Timer timer;

    //inputs
    private EditText nameEdit;
    private NumberPicker h;
    private NumberPicker m;
    private NumberPicker s;
    private Button save;
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);

        if (index < TimerManager.getTimers().length){
            timer = TimerManager.getTimer(index);
        }

        // get input elements
        nameEdit = findViewById(R.id.edit_name);
        h = findViewById(R.id.edit_h);
        m = findViewById(R.id.edit_m);
        s = findViewById(R.id.edit_s);
        save = findViewById(R.id.edit_save);
        delete = findViewById(R.id.edit_delete);

        // set range from number pickers
        h.setMaxValue(23);
        m.setMaxValue(59);
        s.setMaxValue(59);

        // set previous values
        nameEdit.setText(timer.getName());
        h.setValue(timer.getHour());
        m.setValue(timer.getMinute());
        s.setValue(timer.getSecond());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setName(nameEdit.getText().toString());
                timer.setTime(new TimeFormat(h.getValue(), m.getValue(), s.getValue()));
                // return to main activity
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerManager.deleteTimer(index);
                finish();
                MainActivity.gridLayout.invalidate();
            }
        });
    }
}
