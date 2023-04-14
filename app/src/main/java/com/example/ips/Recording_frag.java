package com.example.ips;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;



public class Recording_frag extends Fragment implements SensorEventListener {
    private GraphView Graph;
    private SensorDataCollector data_collector;
    private Viewport View;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private Sensor barometer;
    private Sensor ambientLightSensor;
    private Sensor proximitySensor;
    private Sensor gravitySensor;
    private FragmentManager fm=null ;
    private FragmentTransaction transaction =null ;

    private double lastTimestamp=0;
    private double mLastXvalue=0;
    float accelerometerValues = 0;
    final float alpha = (float) 0.8;
    private float gravity [] = new float[3];
    private long accTimestamp,magTimestamp,gyrTimestamp,barTimestamp,prxTimestamp,lightTimestamp,gravityTimestamp;
    private boolean timeRecord=false;
    // Constants for sampling
    private final int SENSOR_RATE = 100; // Hz
    private final int TARGET_RATE = 5; // Hz
    private final int SAMPLE_PERIOD_MS = 1000 / TARGET_RATE; // Milliseconds

    // Variables for sampling
    private float[] mSensorBuffer = new float[SENSOR_RATE / TARGET_RATE];
    private int mSensorBufferIndex = 0;

    LineGraphSeries<DataPoint> accSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });
    LineGraphSeries<DataPoint> accSeriesx = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });
    LineGraphSeries<DataPoint> accSeriesy = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });
    LineGraphSeries<DataPoint> accSeriesz = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor_graph, container, false);
        Graph = (GraphView) view.findViewById(R.id.accGraph);
        Graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
        Graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
        accSeries.setColor(Color.parseColor("#3F51B5")); // blue
        accSeriesx.setColor(Color.parseColor("#A02422")); // red
        accSeriesy.setColor(Color.parseColor("#63AB62")); // green
        accSeriesz.setColor(Color.parseColor("#DD7500")); // orange

        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        ambientLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        data_collector = MainActivity.data_collector_top;



        return view;
    }

    @Override
    public void onResume() {
        super. onResume();
        sensorManager.registerListener(this, accelerometer, 10000);
        sensorManager.registerListener(this, gyroscope, 10000);
        sensorManager.registerListener(this, magnetometer, 10000);
        sensorManager.registerListener(this, barometer, 10000);
        sensorManager.registerListener(this, ambientLightSensor, 10000);
        sensorManager.registerListener(this, proximitySensor, 10000);
        sensorManager.registerListener(this, gravitySensor, 10000);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //plot graph
            float[] accData = data_collector.getLastAccelerometer();
            // Add a high pass filter (Alpha contributed) to get the real acceleration.

            gravity[0] = alpha * gravity[0] + (1 - alpha) * accData[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * accData[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * accData[2];

            float x, y, z;
            x = accData[0]- gravity[0];
            y = accData[1] - gravity[1];
            z = accData[2] - gravity[2];
            accTimestamp = sensorEvent.timestamp;
            accelerometerValues = (float) Math.sqrt(x*x+y*y+z*z);//-----------sensor data required.
            if(timeRecord){
                lastTimestamp=accTimestamp/1000000;
                timeRecord = false;
            }
            Log.i("accelerometer in graph", String.valueOf(x));
            plotSensor(false, x, y, z, accelerometerValues, accTimestamp, "Accelerometer");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void plotSensor(boolean stop,float sensorValue, float xcoord, float ycoord, float zcoord, long timestamp,String sensorLegend) {

        //Time frame
        //lastTimestamp = data_collector.getLastAccelerometerTimestamp();
        double xValue = timestamp / 1000000; //ms
        double currentTimestamp = xValue - lastTimestamp;

        //Graph plot
        //legend.setText(sensorLegend);
        //Graph.setTitle(sensorLegend);
        View = Graph.getViewport();

        // Add data to buffer
        mSensorBuffer[mSensorBufferIndex] = sensorValue;
        mSensorBufferIndex++;

        if (mSensorBufferIndex == mSensorBuffer.length) {
            // Calculate average of buffer
            float average = 0, averagex = 0, averagey = 0, averagez = 0;
            for (float value : mSensorBuffer) {
                average += value;
                averagex += xcoord;
                averagey += ycoord;
                averagez += zcoord;
            }
            average /= mSensorBuffer.length;
            averagex /= mSensorBuffer.length;
            averagey /= mSensorBuffer.length;
            averagez /= mSensorBuffer.length;


            if (currentTimestamp - mLastXvalue >= SAMPLE_PERIOD_MS) {
                accSeries.appendData(new DataPoint(currentTimestamp, average), true, 100);
                accSeriesx.appendData(new DataPoint(currentTimestamp, averagex), true, 100);
                accSeriesy.appendData(new DataPoint(currentTimestamp, averagey), true, 100);
                accSeriesz.appendData(new DataPoint(currentTimestamp, averagez), true, 100);

                mLastXvalue = currentTimestamp;
                Graph.addSeries(accSeries);
                Graph.addSeries(accSeriesx);
                Graph.addSeries(accSeriesy);
                Graph.addSeries(accSeriesz);
//
//                AccX.setText(Math.round(averagex * 10000d) / 10000d + "");
//                AccY.setText(Math.round(averagey * 10000d) / 10000d + "");
//                AccZ.setText(Math.round(averagez * 10000d) / 10000d + "");

            }

            //Reset buffer index
            mSensorBufferIndex = 0;
        }
    }
}
