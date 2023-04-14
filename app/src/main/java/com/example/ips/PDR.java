package com.example.ips;
import android.hardware.SensorManager;

    public class PDR {
        private static final float STEP_THRESHOLD = 10.0f;
        private static final long MIN_TIME_BETWEEN_STEPS_MS = 250;

        private float[] gravity = new float[3];
        private float[] rotationVector = new float[3];
        private long lastStepTime = 0;

        private float stepLength = 0.7f; // Assume an average step length in meters
        private float[] currentPosition = new float[]{0, 0};

        public void processAccelerometerData(float[] values) {
            final float alpha = 0.8f;

            // Low-pass filter to separate gravity from linear acceleration
            gravity[0] = alpha * gravity[0] + (1 - alpha) * values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * values[2];

            // Calculate linear acceleration
            float[] linearAcceleration = new float[3];
            linearAcceleration[0] = values[0] - gravity[0];
            linearAcceleration[1] = values[1] - gravity[1];
            linearAcceleration[2] = values[2] - gravity[2];

            // Detect step using a simple threshold-based method
            float magnitude = (float) Math.sqrt(linearAcceleration[0] * linearAcceleration[0] +
                    linearAcceleration[1] * linearAcceleration[1] +
                    linearAcceleration[2] * linearAcceleration[2]);

            if (magnitude > STEP_THRESHOLD && (System.currentTimeMillis() - lastStepTime) > MIN_TIME_BETWEEN_STEPS_MS) {
                lastStepTime = System.currentTimeMillis();
                updatePosition();
            }
        }

        public void processGyroscopeData(float[] values) {
            // Simple integration of gyroscope data to estimate rotation
            rotationVector[0] += values[0] * SensorManager.GRAVITY_EARTH;
            rotationVector[1] += values[1] * SensorManager.GRAVITY_EARTH;
            rotationVector[2] += values[2] * SensorManager.GRAVITY_EARTH;
        }

        public void updatePosition() {
            // Calculate displacement using step length and orientation
            float deltaX = stepLength * (float) Math.sin(rotationVector[2]);
            float deltaY = stepLength * (float) Math.cos(rotationVector[2]);

            // Update the current position
            currentPosition[0] += deltaX;
            currentPosition[1] += deltaY;

            // Notify the UI or other components to update the trajectory
            // ...
        }

        public float[] getCurrentPosition() {
            return currentPosition;
        }
    }

