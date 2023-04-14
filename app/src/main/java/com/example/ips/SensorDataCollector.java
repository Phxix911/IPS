package com.example.ips;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.ips.Data_Manager;

import java.util.ArrayList;
import java.util.List;

public class SensorDataCollector extends AppCompatActivity implements SensorEventListener, LocationListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer, barometer;
    private Sensor lightSensor, proximitySensor;
    private LocationManager locationManager;
    private WifiManager wifiManager;
    private Context context ;
    private float[] lastAccelerometer = new float[3];
    private float[] lastGyroscope = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float lastLight;
    private float lastProximity;
    private float lastBarometer;
    //private float lastAltitude;
    private Location lastLocation;
    private List<ScanResult> wifiScanResults = new ArrayList<>();
    private long lastAccelerometerTimestamp = 0;
    private long lastGyroscopeTimestamp = 0;
    private long lastMagnetometerTimestamp = 0;
    private long lastLightTimestamp = 0;
    private long lastProximityTimestamp = 0;
    private long lastBarometerTimestamp = 0;
    //private long lastAltitudeTimestamp = 0;
    private long lastWifiScanTimestamp = 0;
    private PDR pdr;
  //  private Data_Manager Data_manager = new Data_Manager(this);

    public SensorDataCollector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        pdr = new PDR();
    }

    public SensorDataCollector() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void startCollecting() {
        sensorManager.registerListener(this, accelerometer, 10000);
        sensorManager.registerListener(this, gyroscope, 10000);
        sensorManager.registerListener(this, magnetometer, 10000);
        sensorManager.registerListener(this, barometer, 10000);
        //sensorManager.registerListener(this, ambientLightSensor, 10000);
        sensorManager.registerListener(this, proximitySensor, 10000);
        //sensorManager.registerListener(this,gravitySensor,10000);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e("SensorDataCollector", "Location permission not granted");
        }

        wifiManager.startScan();
    }

    public void stopCollecting() {
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
        wifiManager.disconnect();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTimeStamp = System.currentTimeMillis();
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                lastAccelerometer = event.values.clone();
                lastAccelerometerTimestamp = currentTimeStamp;
                pdr.processAccelerometerData(event.values);
                Log.i("accelerometer in collector", String.valueOf(lastAccelerometer));
                break;
            case Sensor.TYPE_GYROSCOPE:
                lastGyroscope = event.values.clone();
                lastGyroscopeTimestamp = currentTimeStamp;
                pdr.processGyroscopeData(event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                lastMagnetometer = event.values.clone();
                lastMagnetometerTimestamp = currentTimeStamp;
                break;
            case Sensor.TYPE_LIGHT:
                lastLight = event.values[0];
                lastLightTimestamp = currentTimeStamp;
                break;
            case Sensor.TYPE_PROXIMITY:
                lastProximity = event.values[0];
                lastProximityTimestamp = currentTimeStamp;
                break;
            case Sensor.TYPE_PRESSURE:
                lastBarometer = event.values[0];
                lastBarometerTimestamp = currentTimeStamp;
                break;
        }
      //  Data_manager.collectData();
        //TODO where to add the position
        pdr.updatePosition();
        //pdrView.addPosition(pdr.getCurrentPosition());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    public void onLocationChanged(Location location) {
        // Location data obtained here
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not used
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not used
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not used
    }

    public float[] getLastAccelerometer() {
        return lastAccelerometer;
    }

    public long getLastAccelerometerTimestamp() {
        return lastAccelerometerTimestamp;
    }

    public float getLastBarometer() {
        return lastBarometer;
    }

    public long getLastBarometerTimestamp() {
        return lastBarometerTimestamp;
    }

    public float[] getLastGyroscope() {
        return lastGyroscope;
    }

    public long getLastGyroscopeTimestamp() {
        return lastGyroscopeTimestamp;
    }

    public float[] getLastMagnetometer() {
        return lastMagnetometer;
    }

    public long getLastMagnetometerTimestamp() {
        return lastMagnetometerTimestamp;
    }

    public float getLastLight() {
        return lastLight;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public long getLastLightTimestamp() {
        return lastLightTimestamp;
    }

    public float getLastProximity() {
        return lastProximity;
    }

    public long getLastProximityTimestamp() {
        return lastProximityTimestamp;
    }

//    public List<ScanResult> getWifiScanResults() {
//        long currentTimeStamp = System.currentTimeMillis();
//
//        if (currentTimeStamp - lastWifiScanTimestamp > 2000) {// Wait at least 2 seconds between scans
//            if(ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
//                wifiScanResults = wifiManager.getScanResults();
//                lastWifiScanTimestamp = currentTimeStamp;
//                wifiManager.startScan(); // Request another scan
//            }
//        }
//        return wifiScanResults;
//    }

    public boolean isWifiScanThrottled() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastScan = currentTime - lastWifiScanTimestamp;
        return timeSinceLastScan < 1000; // Throttle Wi-Fi scans to once per second
    }

    public void clearWifiScanResults() {
        wifiScanResults.clear();
    }
}




