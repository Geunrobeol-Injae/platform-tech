package bob.geunrobeol.platform.tech.location;

import bob.geunrobeol.platform.tech.config.LocationConfig;

public class KalmanFilter {
    private final double processNoise;
    private final double measurementNoise;
    private double estimatedValue;
    private double estimationError;

    public KalmanFilter(double initialValue) {
        this.processNoise = LocationConfig.KALMAN_PROCESS_NOISE;
        this.measurementNoise = LocationConfig.KALMAN_MEASUREMENT_NOISE;
        this.estimatedValue = initialValue;
        this.estimationError = 1.0;
    }

    public void update(double measurement) {
        // Prediction
        double prediction = estimatedValue;
        double predictionError = estimationError + processNoise;

        // Update
        double kalmanGain = predictionError / (predictionError + measurementNoise);
        estimatedValue = prediction + kalmanGain * (measurement - prediction);
        estimationError = (1 - kalmanGain) * predictionError;
    }

    public double get() {
        return estimatedValue;
    }
}