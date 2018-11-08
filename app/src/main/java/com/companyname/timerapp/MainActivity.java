package com.companyname.timerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;
import com.companyname.timerapp.util.LinkLine;
import com.companyname.timerapp.util.LinkManager;
import com.companyname.timerapp.util.UserMode;
import com.companyname.timerapp.util.Vector2f;
import com.companyname.timerapp.views.TimerView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ConstraintLayout mainLayout;
    public static GridLayout gridLayout;
    private static TimerView[] timerViews;
    private LinkManager linkManager = LinkManager.getInstance();

    TextView title;
    ImageView editIcon;
    AlertDialog dialog;
    EditText editText;

    public static final int MARGIN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = this.getPreferences(this.MODE_PRIVATE);
        final SharedPreferences.Editor prefEditor = sharedPref.edit();

        editIcon = findViewById(R.id.icon_edit);
        title = findViewById(R.id.title);
        title.setText(sharedPref.getString("titleKey", "Der Goldene Koch 2019 Halbfinal"));
        dialog = new AlertDialog.Builder(this).create();
        editText = new EditText(this);

        dialog.setTitle(R.string.editTitle);
        dialog.setView(editText);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.saveTitle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                title.setText(editText.getText());
                prefEditor.putString("titleKey", editText.getText().toString());
                prefEditor.commit();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerManager.getUserMode() == UserMode.EDIT) {
                    editText.setText(title.getText());
                    dialog.show();
                }
            }
        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                linkManager.getLinkLine().stopDrawLine();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
//        toggle.setHomeAsUpIndicator(R.mipmap.ale_icon_round); //set your own
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridLayout = findViewById(R.id.androidGrid);
        createGridLayout();
        mainLayout = findViewById(R.id.constraintLayout);

//        TimerManager.setLinkLine((LinkLine) findViewById(R.id.linkLine));
        linkManager.setLinkLine((LinkLine) findViewById(R.id.linkLine));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // get switch for modes
        NavigationView navigationView = findViewById(R.id.nav_view);
        final SwitchCompat editModeSwitch = ((SwitchCompat)((LinearLayout)navigationView.getMenu().findItem(R.id.nav_switch).getActionView()).getChildAt(0));
        final SwitchCompat linkModeSwitch = ((SwitchCompat)((LinearLayout)navigationView.getMenu().findItem(R.id.nav_link_switch).getActionView()).getChildAt(0));

        // create edit switch
        editModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchMode(b, UserMode.EDIT, View.VISIBLE, linkModeSwitch);
            }
        });

        // create link switch
        linkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchMode(b, UserMode.LINK, View.INVISIBLE, editModeSwitch);
            }
        });

        editModeSwitch.setChecked(TimerManager.getUserMode() == UserMode.EDIT);
        linkModeSwitch.setChecked(TimerManager.getUserMode() == UserMode.LINK);
    }

    private void switchMode(boolean switchState, UserMode mode, int iconVisible, SwitchCompat other){
        if (switchState){
            other.setChecked(false);
            TimerManager.setUserMode(mode);
            gridLayout.setBackgroundColor(getResources().getColor(R.color.colorCustomEdit));
            mainLayout.setBackgroundColor(getResources().getColor(R.color.colorCustomEdit));
            editIcon.setVisibility(iconVisible);
        }else{
            TimerManager.setUserMode(UserMode.NORMAL);
            gridLayout.setBackgroundColor(getResources().getColor(R.color.colorCustom));
            mainLayout.setBackgroundColor(getResources().getColor(R.color.colorCustom));
            editIcon.setVisibility(View.INVISIBLE);
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_deleteAll) {
            TimerManager.deleteAllTimers();
        } else if (id == R.id.nav_resetAll) {
            TimerManager.resetAllTimers();
        } else if (id == R.id.nav_resetAlarm){
            TimerManager.resetRingtone();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openEditPage(Timer timer){
        openEditPage(timer.getIndex());
    }

    private void openEditPage(int index){
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
                final TimerView tView = new TimerView(this, xPos, yPos);
                timerViews[yPos*numOfCol + xPos] = tView;



//                System.out.println(xPos+yPos+" at x: " +pos.getX()+ " y: "+pos.getY());

                final int finalYPos = yPos;
                final int finalXPos = xPos;
                tView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timer timer = TimerManager.addTimer(new Timer(null), true, finalYPos *numOfCol + finalXPos);
                        if (timer != null) {
                            openEditPage(timer);
                        }
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
