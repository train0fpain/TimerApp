package com.companyname.timerapp.util;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

import com.companyname.timerapp.timerClasses.TimeFormat;
import com.companyname.timerapp.timerClasses.Timer;
import com.companyname.timerapp.timerClasses.TimerManager;

import java.util.ArrayList;

public class Start extends Application {

    private static Context context;
    private static DatabaseHelper dbHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        dbHelper = new DatabaseHelper(this);
        getData();
        TimerManager.init();
    }

    private void getData(){
        Cursor data = dbHelper.getData();

        // get data and append to list
        ArrayList<String> listDataNames = new ArrayList<>();
        ArrayList<Integer> listDataTimes = new ArrayList<>();
        ArrayList<Integer> listDataIds = new ArrayList<>();
        while (data.moveToNext()){
            listDataNames.add(data.getString(1));
            listDataTimes.add(data.getInt(2));
            listDataIds.add(data.getInt(0));
        }

        for (int i=0; i< listDataNames.size(); i++){
            Timer tmp = new Timer(null);
            TimerManager.addTimer(tmp, false, listDataIds.get(i));
            tmp.setName(listDataNames.get(i));
            tmp.setTime(new TimeFormat(listDataTimes.get(i)));
            tmp.setIndex(listDataIds.get(i));
        }
    }

    public static Context getContext(){
        return context;
    }

    public static DatabaseHelper getDbHelper() {
        return dbHelper;
    }


}