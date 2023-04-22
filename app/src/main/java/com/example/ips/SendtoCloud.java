package com.example.ips;

import static com.example.ips.Switch.data_collector_top;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendtoCloud extends AppCompatActivity  {
    byte[] bytes;
    boolean flag = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendto_cloud);
        getSupportActionBar().hide();
        // Set Home selected
         bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.cloud);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId())
                {
                    case R.id.PDR:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.Sensor:
                        startActivity(new Intent(getApplicationContext(),Switch.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.cloud:

                        return true;
                }

                return false;
            }
        });
    }

    public void stop_record(View view){
        data_collector_top.stopCollecting();
        bytes = data_collector_top.TrajectoryTop.build().toByteArray();
        String fileName;
        String timeStamp = new SimpleDateFormat("_ddMMyy_HHmmss").format(new Date());
        fileName = "Trajectory_" + timeStamp;
        FileProcess.saveBytesToFile(getApplicationContext(), bytes, fileName);
        flag = true;
    }

    public void submit_API(View view) throws IOException {
        if(flag == true){
            APIServer.submitAPI(bytes);
        }
        else{
            Toast.makeText(this,"Please stop recording first!",Toast.LENGTH_LONG).show();
        }
    }

    public void submitAPIAsync(View view) {
        executor.submit(() -> {
            try {
                APIServer.submitAPI(bytes);
            } catch (IOException e) {
                System.err.println("Handle network error.");
                e.printStackTrace();
            }
        });
    }

    public void onResume(){
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.cloud);
    }
}