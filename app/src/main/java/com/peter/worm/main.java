package com.peter.worm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class main extends AppCompatActivity {

    private SensorManager mManager = null;
    private Sensor mSensor = null;
    private Worm worm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOverflowShowingAlways();
        //通过xml加载界面
        setContentView(R.layout.main);
        worm = (Worm) findViewById(R.id.worm);


        //重力监听相关代码，有的机器不支持重力监听
        mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorEventListener mListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                if (worm.gravityCnotrol && worm.canGo) {
                    float x = event.values[SensorManager.DATA_X];
                    float y = event.values[SensorManager.DATA_Y];
                    float z = event.values[SensorManager.DATA_Z];
                    int dir = worm.direction;
                    if (z > 0) {
                        if (x > 0 && y > 0) {
                            if (x > y && dir != worm.DIR_RIGHT)
                                worm.direction = worm.DIR_LEFT;
                            if (x < y && dir != worm.DIR_UP)
                                worm.direction = worm.DIR_DOWN;
                        }
                        if (x > 0 && y < 0) {
                            if (x > -y && dir != worm.DIR_RIGHT)
                                worm.direction = worm.DIR_LEFT;
                            if (x < -y && dir != worm.DIR_DOWN)
                                worm.direction = worm.DIR_UP;

                        }
                        if (x < 0 && y > 0) {
                            if (y > -x && dir != worm.DIR_UP)
                                worm.direction = worm.DIR_DOWN;
                            if (y < -x && dir != worm.DIR_LEFT)
                                worm.direction = worm.DIR_RIGHT;
                        }
                        if (x < 0 && y < 0) {
                            if (x > y && dir != worm.DIR_DOWN)
                                worm.direction = worm.DIR_UP;
                            if (x < y && dir != worm.DIR_LEFT)
                                worm.direction = worm.DIR_RIGHT;
                        }
                    }
                    worm.canGo = false;
                }
            }
        };
        mManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle).setTitle(R.string.about_title)
                        .setMessage(R.string.about_text).show();
                break;
            case R.id.action_replay:
                worm.initinate();
                break;
            case R.id.action_exit:
                finish();
                break;
            case R.id.action_mode:
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle).setTitle(R.string.mode_text).
                        setNegativeButton(R.string.mode_gravity, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                worm.gravityCnotrol = true;
                            }
                        }).setPositiveButton(R.string.mode_touch, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        worm.gravityCnotrol = false;
                    }
                }).show();
                break;
            case R.id.action_score:
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle).setTitle(R.string.score_title)
                        .setMessage(worm.getWormScore()).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("", "");
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            worm.go = true;
        } else {
            worm.go = false;
        }
    }
}