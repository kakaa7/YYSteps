package com.example.marryzhi.yysteps;
import com.example.marryzhi.yysteps.BottomTab.OnTabChangeListener;

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


public class MainActivity extends AppCompatActivity {
   // private Button recording;
    private SensorManager sensorManager;
    private Sensor stepCounter;//步伐总数传感器
    private SensorEventListener stepCounterListener;//步伐总数传感器事件监听器

    private static SimpleDBHelper dbHelper;
    //当前日期
    private String CURRENT_DATE;
    //当前weekdays
    private String CURRENT_WEEK;
    //当前步数
    private String CURRENT_STEP;
    //数据库
    SQLiteDatabase db;
    //3秒进行一次存储
    private static int saveDuration = 3000;
    //自定义简易计时器
    private TimeCount timeCount;
    BottomTab bottomTab = new BottomTab();
    List<Fragment> mFragments = new ArrayList<>();
    String[] bottomTitle={"Steps","Achieve"};
    LinearLayout bottom;
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragments.add(new stepsFragment());
        mFragments.add(new achieveFragment());
        mViewPager = findViewById(R.id.viewpager);
        bottom = findViewById(R.id.bottomContain);
        dbHelper = new SimpleDBHelper(this, 3);
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器系统服务
        stepCounter=sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器

        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        bottomTab.addBottomTab(bottom,bottomTitle);
        bottomTab.changeColor(0); //初始化 默认页面

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            public void onPageSelected(int position) {
                bottomTab.changeColor(position);
                mViewPager.setCurrentItem(position);
                replaceFragment(mFragments.get(position));
            }
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            public void onPageScrollStateChanged(int arg0) {}
        });

        OnTabChangeListener onTabChangeListener=new OnTabChangeListener() {
            @Override
            public void onTabChange(int position) {
                //切换对应的fragment
                mViewPager.setCurrentItem(position);
                replaceFragment(mFragments.get(position));
            }
        };
        bottomTab.setOnTabChangeListener(onTabChangeListener) ;

        //initListener();
        /*ContentValues values = new ContentValues();
            values.put("num", "999");
            values.put("week",CURRENT_WEEK);
            values.put("date",CURRENT_DATE);
            db.insert("notes", null, values);*/
        //startTimeCount();
       //initTodayData();
        //stepCounterText.setText(CURRENT_STEP);
        //saveStepData();

    }

    public static SQLiteDatabase getDB() {
        return dbHelper.getWritableDatabase();
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.viewpager, fragment);
        transaction.commit();
    }

    protected void initListener() {
        stepCounterListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.e("Counter-SensorChanged",event.values[0]+"---"+event.accuracy+"---"+event.timestamp);
                CURRENT_STEP=""+event.values[0];
               // stepCounterText.setText(CURRENT_STEP);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.e("Counter-Accuracy",sensor.getName()+"---"+accuracy);

            }
        };
    }

    private void registerSensor(){
        //注册传感器事件监听器
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)&&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)){
            sensorManager.registerListener(stepCounterListener,stepCounter,SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void unregisterSensor(){
        //解注册传感器事件监听器
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)&&
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)){
            sensorManager.unregisterListener(stepCounterListener);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterSensor();
    }

    @Override
    public void onResume(){
        super.onResume();
        registerSensor();
    }

    /**
     * 初始化当天数据
     */
    private void initTodayData() {
        //获取当前时间
        CURRENT_DATE = TimeUtils.getCurrentDate();
        CURRENT_WEEK = TimeUtils.getWeek();
        //获取数据库
        db = getDB();
        //获取当天的数据，用于展示
        String entity = getCurDataByDate(CURRENT_DATE);
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        if (entity == null) {
            CURRENT_STEP = "0";
        } else {
            CURRENT_STEP = entity;

        }
    }
    /**
     * 监听晚上0点变化初始化数据
     */
    private void isNewDay() {
        String time = "00:00";
        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) ||
                !CURRENT_DATE.equals(TimeUtils.getCurrentDate())) {
            initTodayData();
        }
    }

    /**
     * 开始倒计时，去存储步数到数据库中
     */
    private void startTimeCount() {
        timeCount = new TimeCount(saveDuration, 1000);
        timeCount.start();
    }


    private class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onTick(long millisUntilFinished) {

        }


        public void onFinish() {
            // 如果计时器正常结束，则每隔三秒存储步数到数据库
            timeCount.cancel();
            saveStepData();
            startTimeCount();
        }
    }

    public String getCurDataByDate(String Date){
        String steps;
        Cursor cursor = db.query("notes", new String[]{"num"}, "date=?", new String[]{Date},
                null, null, null);
            steps = cursor.getString(cursor.getColumnIndex("num"));
            cursor.close();
            return steps;
        }

    /**
     * 保存当天的数据到数据库中，并去刷新通知栏
     */
    private void saveStepData() {
        //查询数据库中的数据
        String entity =getCurDataByDate(CURRENT_DATE);
        //为空则说明还没有该天的数据，有则说明已经开始当天的计步了
        ContentValues values = new ContentValues();
        if (entity == null) {
            //没有则新建一条数据
            values.put("num", CURRENT_STEP);
            values.put("week",CURRENT_WEEK);
            values.put("date",CURRENT_DATE);
            db.insert("notes", null, values);
        } else {
            //有则更新当前的数据
            values.put("num",CURRENT_STEP);
            db.update("notes", values, "date = ?", new String[] {CURRENT_DATE });
        }

    }
    class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
