package dartmouth.cs.qiyaozuo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import static dartmouth.cs.qiyaozuo.WekaClassifier.classify;

public class SensorsService extends Service implements SensorEventListener {
    private static final int ACC_BUFFER_CAPACITY = 2048;
    private static final int ACC_BLOCK_CAPCITY = 64;
    private static final String FFT_COEF_LABEL = "fft_coef_";
    private static final String MAX_LABEL = "max";
    private static final String LABEL_STANDING = "standing";
    private static final String LABEL_WALKING = "walking";
    private static final String LABEL_RUNNING = "running";
    private static final String CLASS_LABEL_KEY = "label";
    private static final String FEAT_SET_NAME = "accelerometer_features";
    private static final int FEAT_SET_CAPACITY = 10000;
    private static final String TAG = "SensorsService";
    private static ArrayBlockingQueue<Double> mAccBuffer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    DecimalFormat df = new DecimalFormat("0000");
    private Attribute mClassAttribute;
    private Instances mDataset;
    private OnSensorChangedTask mAsyncTask;
    public static double sum, count = 0;

    public SensorsService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mAccBuffer = new ArrayBlockingQueue<Double>(ACC_BUFFER_CAPACITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        //create array list for attributes
        ArrayList<Attribute> allAttr = new ArrayList<>();

        //add fft coefficient attributes
        for (int i = 0; i < ACC_BLOCK_CAPCITY; i++) {
            allAttr.add(new Attribute(FFT_COEF_LABEL + df.format(i)));
        }
        allAttr.add(new Attribute(MAX_LABEL));//add max feature

        //add labels attribute
        ArrayList<String> labelItems = new ArrayList<>(3);
        labelItems.add(LABEL_STANDING);
        labelItems.add(LABEL_WALKING);
        labelItems.add(LABEL_RUNNING);
        mClassAttribute = new Attribute(CLASS_LABEL_KEY, labelItems);
        allAttr.add(mClassAttribute);

        //construct dataset with all attributes
        mDataset = new Instances(FEAT_SET_NAME, allAttr, FEAT_SET_CAPACITY);
        mDataset.setClassIndex(mDataset.numAttributes() - 1);

        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();

        return START_NOT_STICKY;

    }

    private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");

            Instance inst = new DenseInstance(ACC_BLOCK_CAPCITY + 2);
            inst.setDataset(mDataset);
            int blockSize = 0;

            FFT fft = new FFT(ACC_BLOCK_CAPCITY);
            double[] accBlock = new double[ACC_BLOCK_CAPCITY];
            double[] re = accBlock;
            double[] im = new double[ACC_BLOCK_CAPCITY];
            double max = .0;

            while (true) {
                try {
                    //if task is cancelled
                    if (isCancelled() == true) {
                        return null;
                    }
                    //dump buffer into accelerometer data block
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == ACC_BLOCK_CAPCITY) {
                        blockSize = 0;
                        Double[] feature = new Double[ACC_BLOCK_CAPCITY + 1];

                        //get max value of the 64 magnitudes
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }


                        //compute fft coefficient of magnitude
                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                            //inst.setValue(i, mag);
                            feature[i] = mag;
                            im[i] = .0;
                        }

                        //add max value
                        //inst.setValue(ACC_BLOCK_CAPCITY, max);
                        feature[ACC_BLOCK_CAPCITY] = max;
                        Double c = classify(feature);
                        //Log.d(TAG, "doInBackground: " + c);
                        updateActivityType(c);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAG, "onCancelled: ");
        }
    }

    //update activity type in map activity
    private void updateActivityType(Double c) {
        sum += c;
        count += 1;
        if (c == 0.0) {
            MapActivity.mType = "type: standing";
        } else if (c == 1.0) {
            MapActivity.mType = "type: walking";
        } else if (c == 2.0) {
            MapActivity.mType = "type: running";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: ");
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //compute magnitude
            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1]
                    + event.values[2] * event.values[2]);
            try {
                mAccBuffer.add(new Double(m));

            } catch (Exception e) {
                ArrayBlockingQueue<Double> newBuf =
                        new ArrayBlockingQueue<>(mAccBuffer.size() * 2);
                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        mAsyncTask.cancel(true);
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }
}
