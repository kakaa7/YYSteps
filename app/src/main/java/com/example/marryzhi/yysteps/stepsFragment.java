package com.example.marryzhi.yysteps;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;


public class stepsFragment  extends Fragment {

    private TextView stepCounterText;
    private Button recording;
    private SensorManager sensorManager;
    private Sensor stepCounter;//步伐总数传感器
    private SensorEventListener stepCounterListener;//步伐总数传感器事件监听器
    private static SimpleDBHelper dbHelper;
    SQLiteDatabase db;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_steps, container, false);
        recording = (Button) view.findViewById(R.id.record);
        dbHelper = new SimpleDBHelper(getActivity(), 3);
        db=getDB();
        recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Recording.class);
                startActivity(intent);
            }
        });

        sensorManager= (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);//获取传感器系统服务
        stepCounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器

        return view;
    }

    public static SQLiteDatabase getDB() {
        return dbHelper.getWritableDatabase();
    }


}
