package com.companyname.timerapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;
import com.companyname.timerapp.views.TimerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static GridLayout gridLayout;
    private static TimerView[] timerViews;
    private SwitchCompat editSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        if (android.os.Build.VERSION.SDK_INT > 21) {
            this.getWindow().setStatusBarColor(Color.parseColor("#356bba"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridLayout = findViewById(R.id.androidGrid);
        createGridLayout();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try{
            NavigationView navigationView = findViewById(R.id.nav_view);
            SwitchCompat switchCompat = ((SwitchCompat)((LinearLayout)navigationView.getMenu().findItem(R.id.nav_switch).getActionView()).getChildAt(0));
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TimerManager.setEditMode(b);
                }
            });

            switchCompat.setChecked(TimerManager.isEditMode());

        }catch (Exception e){
            System.out.println(e+"shit switch");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_timerButton) {
            TimerManager.addTimer(new Timer(null));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_deleteAll) {
            TimerManager.deleteAllTimers();
        } else if (id == R.id.nav_resetAll) {
            TimerManager.resetAllTimers();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openEditPage(Timer timer){
        openEditPage(timer.getIndex());
    }

    private void openEditPage(int index){
        //TimerManager.setEditMode(true);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("index", index);
        this.startActivity(intent);
    }

    public void createGridLayout(){

        final int numOfCol = gridLayout.getColumnCount();
        int numOfRow = gridLayout.getRowCount();
        timerViews = new TimerView[numOfCol*numOfRow];
        for(int yPos=0; yPos<numOfRow; yPos++){
            for(int xPos=0; xPos<numOfCol; xPos++){
                TimerView tView = new TimerView(this, xPos, yPos);
                timerViews[yPos*numOfCol + xPos] = tView;
                final int finalYPos = yPos;
                final int finalXPos = xPos;
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimerManager.addTimer(new Timer(null), true, finalYPos *numOfCol + finalXPos);
                    }
                });

                gridLayout.addView(tView);
            }
        }

        for (int i=0; i<TimerManager.getTimers().length; i++){
            if (TimerManager.getTimer(i) != null){
                TimerManager.getTimer(i).setView(timerViews[i]);
            }
        }

        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){

                    boolean updateLayout = true;

                    @Override
                    public void onGlobalLayout() {

                        if (updateLayout){
                        final int MARGIN = 5;

                        int pWidth = gridLayout.getWidth();
                        int pHeight = gridLayout.getHeight();
                        int numOfCol = gridLayout.getColumnCount();
                        int numOfRow = gridLayout.getRowCount();
                        int w = pWidth/numOfCol;
                        int h = pHeight/numOfRow;

                        for(int yPos=0; yPos<numOfRow; yPos++){
                            for(int xPos=0; xPos<numOfCol; xPos++){
                                GridLayout.LayoutParams params =
                                        (GridLayout.LayoutParams) timerViews[yPos*numOfCol + xPos].getLayoutParams();
                                params.width = w - 2*MARGIN;
                                params.height = h - 2*MARGIN;
                                params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                                timerViews[yPos*numOfCol + xPos].setLayoutParams(params);
                            }
                        }
                        updateLayout = false;
                    }}});
    }

    public static TimerView getViewAt(int index){
        if (timerViews != null && timerViews.length > index) {
            return timerViews[index];
        }else{
            return null;
        }
    }
}
