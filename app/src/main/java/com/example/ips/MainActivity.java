package com.example.ips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity{
    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;
    public static SensorDataCollector data_collector_top;
    public static Data_Manager data_manager_top;
    public static Data_Process data_process_top;
    private PDR pdr;
    private PDRView pdrView;
    public Button record_start;
    private TextView acc;
    //private View view = new View(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //View view = inflater.inflate(R.layout.activity_main, container, false);
            setContentView(R.layout.activity_main);     //set layout file
            record_start = findViewById(R.id.record_start);
            data_collector_top = new SensorDataCollector(this);
            data_manager_top = new Data_Manager(data_collector_top);
            data_process_top = new Data_Process(data_manager_top);
//            record_start.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //Intent intent = new Intent(this, RecordStart.class);
//                    startActivity(new Intent(getApplicationContext(),RecordStart.class));
//                }
//            });
    }

    public void start_record(View view){
        startActivity(new Intent(getApplicationContext(),RecordStart.class));
    }

    //@Override
       public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            pdr.processAccelerometerData(event.values);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            pdr.processGyroscopeData(event.values);
//        }
//
//        // Calculate the user's position and update trajectory
//        pdr.updatePosition();
//
//        // Display the trajectory on a map or custom view
//        pdrView.addPosition(pdr.getCurrentPosition());
    }

    //@Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }


    @Override
    protected void onPause() {
        super.onPause();
//        sensorManager.unregisterListener(this);
    };

    @Override
    protected void onResume() {
        super.onResume();
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    };
}
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//    public void startRecording(){
//    }
//}