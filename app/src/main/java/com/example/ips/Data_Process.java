package com.example.ips;
/*
    Use the data from data collector to do processing
 */

import static android.hardware.SensorManager.getAltitude;

import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

public class Data_Process extends MainActivity{
    private Data_Manager data_manager;

    private float pressure;

    //constant used for calculation
    private static final float ALPHA = 0.8f;  // 用于低通滤波器的系数
    private static final double GRAVITY = 9.81; // 重力加速度
    private static final float PEAK_THRESHOLD = 9.5f; // 峰值检测阈值
    private static final float STEP_LENGTH_FACTOR = 0.7f; // 步长因子，可以根据实际需求
    private static final float FLOOR_CHANGE_THRESHOLD_MAG = 15.0f; // 磁场强度楼层变化阈值
    private static final float FLOOR_CHANGE_THRESHOLD_PRESSURE = 2.0f; // 气压楼层变化阈值，单位：百帕（hPa）

    //processed data
    private List<float[]> trajectory = new ArrayList<>();
    private float stepLengthEstimation;
    private int currentFloor;
    private float[] lastPosition = new float[]{0, 0}; // 上一个位置
    private float[] curPosition = new float[]{0, 0};  // 当前位置

    public Data_Process(Data_Manager data_manager){
        this.data_manager = data_manager;
        pressure = data_manager.sensorDataCollector.getLastBarometer();
    }

    //data processing--Trajectory calculation
    private void calculateTrajectory(List<float[]> accelerometerDataList) {

        float[] filteredAccData = new float[3];
        float[] velocity = new float[3];
        float[] position = new float[3];

        for (float[] accData : accelerometerDataList) {
            // 对加速度计数据进行低通滤波
            filteredAccData[0] = ALPHA * filteredAccData[0] + (1 - ALPHA) * accData[0];
            filteredAccData[1] = ALPHA * filteredAccData[1] + (1 - ALPHA) * accData[1];
            filteredAccData[2] = ALPHA * filteredAccData[2] + (1 - ALPHA) * accData[2];

            // 去除重力影响
            float[] linearAcc = new float[3];
            linearAcc[0] = accData[0] - filteredAccData[0];
            linearAcc[1] = accData[1] - filteredAccData[1];
            linearAcc[2] = accData[2] - filteredAccData[2];

            // 对加速度数据进行积分以得到速度
            float deltaTime = 1; // 假设恒定的时间间隔，您需要根据实际情况计算
            velocity[0] += linearAcc[0] * deltaTime;
            velocity[1] += linearAcc[1] * deltaTime;
            velocity[2] += linearAcc[2] * deltaTime;

            // 对速度数据进行积分以得到位置
            position[0] += velocity[0] * deltaTime;
            position[1] += velocity[1] * deltaTime;
            position[2] += velocity[2] * deltaTime;

            // 将计算出的位置添加到轨迹中
            trajectory.add(position.clone());
        }
        return;
    }

    //data processing--step length estimation calculation
    private void estimateStepLength(List<float[]> accelerometerDataList) {
        int stepCount = 0;
        boolean isPeakDetected = false;
        for (float[] accData : accelerometerDataList) {
            // 计算加速度计数据的幅度
            float magnitude = (float) Math.sqrt(Math.pow(accData[0], 2) + Math.pow(accData[1], 2) + Math.pow(accData[2], 2));
            // 检测峰值
            if (magnitude > PEAK_THRESHOLD) {
                if (!isPeakDetected) {
                    stepCount++;
                    isPeakDetected = true;
                }
            } else {
                isPeakDetected = false;
            }
        }
        // 估计步长
        stepLengthEstimation = stepCount * STEP_LENGTH_FACTOR;
        return;
    }

    //data processing--floor change estimation calculation
    private long previousTime = System.currentTimeMillis();
    private List<Float> altitudeHistory = new ArrayList<>();        // collected altitude values in history
    private void determineFloor(float[] rawPressureValues) {
        // Firstly add the values to the value filter. This is used
        // to smooth them. 20 values are being smoothed.
        final float alpha = 0.9f;

            pressure = alpha * pressure + (1 - alpha) * rawPressureValues[0];

        // get the current time and check that half of a second passed since the last time the sensor was updated.
        long currTime = System.currentTimeMillis();
        if (previousTime + 500 < currTime){
            // Get the averaged value fromthe filter list
//            float millibars_of_pressure = calculateAverage(pressureFilter);
            // Use Google provided formula for finding the altitude from pressure values
            float altitude = getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

            // Collect 30 values in the altitude history list, which is equivalent to 15 seconds of capturing
            altitudeHistory.add(altitude);
            if (altitudeHistory.size() > 30){
                altitudeHistory.remove(0);
            }

            // first is the value at the moment, last is value 15 seconds before now.
            float first = altitude;
            float last = altitudeHistory.get(0);

            // Check that if the value difference is 2m, then determine if user went up or down.
            // Call the listener method.
            if (first - last > 2) {
                currentFloor++;
                // remove all of the values because floor was updated!
                altitudeHistory.clear();
            }else if (first - last < -2) {
                currentFloor--;
                altitudeHistory.clear();
            }
            // update the previous time
            previousTime = currTime;
        }
    }

    //data processing--position calculation
    public void calculatePosition(List<float[]> accelerometerDataList, List<float[]> orientationDataList) {
        List<float[]> trajectory = this.trajectory;
        float stepLength = stepLengthEstimation;
        // 将当前位置设置为上一个位置，以便进行下一次迭代
        lastPosition = curPosition.clone();

        for (int i = 0; i < trajectory.size() && i < orientationDataList.size(); i++) {
            float[] displacement = trajectory.get(i);
            float[] orientation = orientationDataList.get(i);

            // 计算相对于上一个位置的位移
            float dx = displacement[0] * stepLength;
            float dy = displacement[1] * stepLength;

            // 根据方向数据更新当前位置
            curPosition[0] = lastPosition[0] + (float) (dx * Math.cos(Math.toRadians(orientation[0])) - dy * Math.sin(Math.toRadians(orientation[0])));
            curPosition[1] = lastPosition[1] + (float) (dx * Math.sin(Math.toRadians(orientation[0])) + dy * Math.cos(Math.toRadians(orientation[0])));
        }
    }

    public List<float[]> getTrajectoryDataList() {
        return trajectory;
    }
}
