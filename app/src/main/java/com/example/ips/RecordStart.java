package com.example.ips;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.ips.SensorDataCollector;
import com.example.ips.Data_Manager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;



public class RecordStart extends MainActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private Sensor barometer;
    private Sensor ambientLightSensor;
    private Sensor proximitySensor;
    private Sensor gravitySensor;
    private TextView tvSensorData;
    private Button btnVisualize;
    private GraphView Graph;
    private Viewport View;
    private TextView AccX, AccY, AccZ;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Recording_frag record_frag;
    private FragmentManager fm=null ;
    private FragmentTransaction transaction =null ;

    private double lastTimestamp=0;
    private double mLastXvalue=0;
    float accelerometerValues = 0;
    final float alpha = (float) 0.8;
    private float gravity [] = new float[3];
    private long accTimestamp,magTimestamp,gyrTimestamp,barTimestamp,prxTimestamp,lightTimestamp,gravityTimestamp;
    private float[] accData;

    //private Data_Manager data_manager;
    //private SensorDataCollector data_collector ;

    //private Data_Manager data_manager;
    //private SensorDataCollector data_collector ;
    //@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View view = inflater.inflate(R.layout.activity_main, container, false);
        setContentView(R.layout.record_start);     //set layout file
        tvSensorData = findViewById(R.id.tvSensorData);
        btnVisualize = findViewById(R.id.btnVisualize);
        fm = getSupportFragmentManager();
        //data_collector = new SensorDataCollector(this);
        //data_manager = new Data_Manager(data_collector);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        ambientLightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        // Defining
//        Graph = (GraphView) findViewById(R.id.accGraph);
//        Graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.BLACK);
//        Graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLACK);
//        accSeries.setColor(Color.parseColor("#3F51B5")); // blue
//        accSeriesx.setColor(Color.parseColor("#A02422")); // red
//        accSeriesy.setColor(Color.parseColor("#63AB62")); // green
//        accSeriesz.setColor(Color.parseColor("#DD7500")); // orange
//
//        AccX = (TextView)view.findViewById(R.id.accX);
//        AccY = (TextView)view.findViewById(R.id.accY);
//        AccZ = (TextView)view.findViewById(R.id.accZ);



        data_collector_top.startCollecting();

        setDefaultFragment();
        btnVisualize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction = fm.beginTransaction();
                record_frag=new Recording_frag();
                transaction.replace(R.id.fragment,record_frag);
                transaction.commit();
            }
        });
    }

    private void setDefaultFragment()
    {
        transaction = fm.beginTransaction();
        record_frag=new Recording_frag();
        //hide visualized graph
        transaction.hide(record_frag).commit();
        //transaction.replace(R.id.fragment,record_frag);
        //transaction.commit();
    }

    @Override
    public void onResume(){
        super. onResume();
        //sensorManager.registerListener(this, accelerometer, 60000); // 10000-100 samples/sec; 60000 - 20 samples/sec
        sensorManager.registerListener(this, accelerometer, 10000);
        sensorManager.registerListener(this, gyroscope, 10000);
        sensorManager.registerListener(this, magnetometer, 10000);
        sensorManager.registerListener(this, barometer, 10000);
        sensorManager.registerListener(this, ambientLightSensor, 10000);
        sensorManager.registerListener(this, proximitySensor, 10000);
        sensorManager.registerListener(this, gravitySensor,10000);
    }

    public void onSensorChanged(SensorEvent event) {
        updateSensorData();

    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    private void updateSensorData() {
        accData = data_collector_top.getLastAccelerometer();
//        float[] gyroData = data_collector.getLastGyroscope();
//        float[] magData = data_collector.getLastMagnetometer();
//        float lightData = data_collector.getLastLight();
//        float proximityData = data_collector.getLastProximity();
//        float barometerData = data_collector.getLastBarometer();

        String sensorDataText = String.format(Locale.getDefault(),
                "Accelerometer: x=%.2f, y=%.2f, z=%.2f\n",accData[0], accData[1], accData[2]);
//        +
//                        "Gyroscope: x=%.2f, y=%.2f, z=%.2f\n" +
//                        "Magnetometer: x=%.2f, y=%.2f, z=%.2f\n" +
//                        "Light: %.2f\n" +
//                        "Proximity: %.2f\n" +
//                        "Barometer: %.2f",
//                accData[0], accData[1], accData[2],
//                gyroData[0], gyroData[1], gyroData[2],
//                magData[0], magData[1], magData[2],
//                lightData, proximityData, barometerData);
        //String acc_x = getaccData[0];
        Log.i("accelerometer in start", String.valueOf(accData[0]));

        tvSensorData.setText(String.valueOf(accData[0]));
    }

//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        data_collector_top.stopCollecting();
//    }

    //PLOT SENSOR METHOD
    // Constants for sampling
    private final int SENSOR_RATE = 100; // Hz
    private final int TARGET_RATE = 5; // Hz
    private final int SAMPLE_PERIOD_MS = 1000 / TARGET_RATE; // Milliseconds
    // Variables for sampling
    private float[] mSensorBuffer = new float[SENSOR_RATE / TARGET_RATE];
    private int mSensorBufferIndex = 0;

}
