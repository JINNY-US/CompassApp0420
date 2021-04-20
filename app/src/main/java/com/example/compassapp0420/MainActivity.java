package com.example.compassapp0420;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    CompassView compassView;

    SensorManager sensorManager;
    SensorEventListener listener;
    Sensor magneticSensor, accelSensor;

    float[] accelValues, magneticValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        compassView = (CompassView) findViewById(R.id.compassView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        accelValues = event.values.clone();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magneticValues = event.values.clone();
                        break;
                    default:
                        break;
                }
                if(magneticValues != null && accelValues != null) {
                    float[] R = new float[16];
                    float[] I = new float[16];
                    SensorManager.getRotationMatrix(R, I, accelValues, magneticValues);
                    float[] values = new float[3];
                    SensorManager.getOrientation(R, values); //나침반의 방향을 계산

                    compassView.azimuth = (int) radian2degree(values[0]);
                    compassView.invalidate();
                    // compassView에 있는 그림을 무효화시키고 다시 그려라
                    // = onDraw 호출
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(listener, magneticSensor, sensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, accelSensor, sensorManager.SENSOR_DELAY_UI);
    }

    private float radian2degree(float radian) {
        return radian * 180 / (float)Math.PI;
    }
}