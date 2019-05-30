package com.example.accelapplication;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //chart 요소
    ArrayList<Entry> EntrieX = new ArrayList<>();
    ArrayList<Entry> EntrieY = new ArrayList<>();
    ArrayList<Entry> EntrieZ = new ArrayList<>();
    int x=1;
    int x1=1;
    int x2=1;

    //이동거리 출력
    private TextView MoveX, MoveY;
    private float [] velocityx = {0, 0, 0};
    private float [] positionx = {0,0,0};
    private float [] velocityy = {0, 0, 0};
    private float [] positiony = {0,0,0};
    long lastime, curtime,deltaT;
    int CountSensorchange = 1;

    //적분 변수
    private double timestamp;
    private double dt;
    //1.0f/1000000000.0f;
    private static final float NS2S = 1.0f/1000000000.0f;
    float PreaccX;
    float PreaccY;
    float DiffAccelX;
    float DiffAccelY;
    float PreDiffAccelX;
    float PreDiffAccelY;
    //double PreaccY;
    double PreaccZ;
    float InitialAccX;
    float InitialAccY;
    double InitialAccZ;
    double MovementX;
    double MovementY;
    double MovementZ;


    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoveX = (TextView)findViewById(R.id.MoveX);
        MoveY = (TextView)findViewById(R.id.MoveY);


        //add LineChart
       // LineChart lineChart = (LineChart) findViewById(R.id.chart);
        //ArrayList<Entry> EntrieX = new ArrayList<>();
        /*
        EntrieX.add(new Entry(1,1));
        EntrieX.add(new Entry(2,2));
        EntrieX.add(new Entry(3,5));
        EntrieX.add(new Entry(4,1));*/

        /*Start*/
        /*LineDataSet lineDataSet = new LineDataSet(EntrieX, "속성명1");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        //lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();*/
        /*End*/

        //Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelometerListener();

        //Touch Listener for Accelometer
        findViewById(R.id.a_start).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);
                        CountSensorchange=1;
                        break;

                    case MotionEvent.ACTION_UP:
                        mSensorManager.unregisterListener(mAccLis);
                        CountSensorchange=1;
                        break;

                }
                return false;
            }
        });

    }

    float CalcIntegration(float base, float diff0, float diff1, float time)
    {
        //outputX.setText("x:" + Float.toString(diff1));
        /*low필터 : 오차값 배제*/
        float diff = (diff1 - diff0);
        if (Math.abs((diff1 - diff0))<0.1)
        {
           diff=0;
           //diff0=0;
        }
        float result=0;
        result = base + (diff0+(diff/2)) * (time / 1000);
        return result;
    }


    @Override
    public void onPause(){
        super.onPause();
        Log.e("LOG", "onPause()");
        mSensorManager.unregisterListener(mAccLis);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("LOG", "onDestroy()");
        mSensorManager.unregisterListener(mAccLis);
    }

    private class AccelometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {


            double accX = event.values[0];
            double accY = event.values[1];
            double accZ = event.values[2];
            CountSensorchange++;

            //chart
            float accelX = event.values[0];
            EntrieX.add(new Entry(x++,accelX));
            /*Start*/
            LineChart lineChart = (LineChart) findViewById(R.id.Xchart);
            LineDataSet lineDataSet = new LineDataSet(EntrieX, "X가속도계 값");
            lineDataSet.setLineWidth(2);
            lineDataSet.setCircleRadius(6);
            lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
            //lineDataSet.setCircleColorHole(Color.BLUE);
            lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);
            lineDataSet.setDrawHighlightIndicators(false);
            lineDataSet.setDrawValues(false);

            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.BLACK);
            xAxis.enableGridDashedLine(8, 24, 0);

             YAxis yLAxis = lineChart.getAxisLeft();
            yLAxis.setTextColor(Color.BLACK);

            YAxis yRAxis = lineChart.getAxisRight();
            yRAxis.setDrawLabels(false);
            yRAxis.setDrawAxisLine(false);
            yRAxis.setDrawGridLines(false);

            Description description = new Description();
            description.setText("");

            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setDrawGridBackground(false);
            lineChart.setDescription(description);
            //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
            lineChart.invalidate();
            /*End*/

            //chart
            float accelY = event.values[1];
            EntrieY.add(new Entry(x1++,accelY));
            /*Start*/
            LineChart lineChartY = (LineChart) findViewById(R.id.Ychart);
            LineDataSet lineDataSetY = new LineDataSet(EntrieY, "Y가속도계 값");
            lineDataSetY.setLineWidth(2);
            lineDataSetY.setCircleRadius(6);
            lineDataSetY.setCircleColor(Color.parseColor("#FFA1B4DC"));
            //lineDataSet.setCircleColorHole(Color.BLUE);
            lineDataSetY.setColor(Color.parseColor("#FFA1B4DC"));
            lineDataSetY.setDrawCircleHole(false);
            lineDataSetY.setDrawCircles(false);
            lineDataSetY.setDrawHorizontalHighlightIndicator(false);
            lineDataSetY.setDrawHighlightIndicators(false);
            lineDataSetY.setDrawValues(false);

            LineData lineDataY = new LineData(lineDataSetY);
            lineChartY.setData(lineDataY);

            XAxis xAxisY = lineChartY.getXAxis();
            xAxisY.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxisY.setTextColor(Color.BLACK);
            xAxisY.enableGridDashedLine(8, 24, 0);

            YAxis yLAxisY = lineChartY.getAxisLeft();
            yLAxisY.setTextColor(Color.BLACK);

            YAxis yRAxisY = lineChartY.getAxisRight();
            yRAxisY.setDrawLabels(false);
            yRAxisY.setDrawAxisLine(false);
            yRAxisY.setDrawGridLines(false);

            Description descriptionY = new Description();
            descriptionY.setText("");

            lineChartY.setDoubleTapToZoomEnabled(false);
            lineChartY.setDrawGridBackground(false);
            lineChartY.setDescription(descriptionY);
            //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
            lineChartY.invalidate();
            /*End*/

            //chart
            float accelZ = event.values[2];
            EntrieZ.add(new Entry(x2++,accelZ));
            /*Start*/
            LineChart lineChartZ = (LineChart) findViewById(R.id.Zchart);
            LineDataSet lineDataSetZ = new LineDataSet(EntrieZ, "Z가속도계 값");
            lineDataSetZ.setLineWidth(2);
            lineDataSetZ.setCircleRadius(6);
            lineDataSetZ.setCircleColor(Color.parseColor("#FFA1B4DC"));
            //lineDataSet.setCircleColorHole(Color.BLUE);
            lineDataSetZ.setColor(Color.parseColor("#FFA1B4DC"));
            lineDataSetZ.setDrawCircleHole(false);
            lineDataSetZ.setDrawCircles(false);
            lineDataSetZ.setDrawHorizontalHighlightIndicator(false);
            lineDataSetZ.setDrawHighlightIndicators(false);
            lineDataSetZ.setDrawValues(false);

            LineData lineDataZ = new LineData(lineDataSetZ);
            lineChartZ.setData(lineDataZ);

            XAxis xAxisZ = lineChartZ.getXAxis();
            xAxisZ.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxisZ.setTextColor(Color.BLACK);
            xAxisZ.enableGridDashedLine(8, 24, 0);

            YAxis yLAxisZ = lineChartZ.getAxisLeft();
            yLAxisZ.setTextColor(Color.BLACK);

            YAxis yRAxisZ = lineChartZ.getAxisRight();
            yRAxisZ.setDrawLabels(false);
            yRAxisZ.setDrawAxisLine(false);
            yRAxisZ.setDrawGridLines(false);

            Description descriptionZ = new Description();
            descriptionZ.setText("");

            lineChartZ.setDoubleTapToZoomEnabled(false);
            lineChartZ.setDrawGridBackground(false);
            lineChartZ.setDescription(descriptionZ);
            //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
            lineChartZ.invalidate();
            /*End*/

            //double angleXZ = Math.atan2(accX,  accZ) * 180/Math.PI;
            //double angleYZ = Math.atan2(accY,  accZ) * 180/Math.PI;

            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            if (CountSensorchange == 2)
            {
                Log.e("LOG", "초기값 입력");
                InitialAccX = event.values[0];
                InitialAccY = event.values[1];
                InitialAccZ = event.values[2];
                PreDiffAccelX = 0;
                PreDiffAccelY = 0;
                MovementX = 0;
                MovementY = 0;
                curtime = System.currentTimeMillis();
            }

            if (dt - timestamp*NS2S != 0) {

                lastime = curtime;
                curtime = System.currentTimeMillis();
                deltaT = curtime - lastime;
                float time = (float)deltaT/1000;

                /*
                DiffAccelX = -(PreaccX-accelX);
                DiffAccelY = -(PreaccY-accelY);
                if(Math.abs(DiffAccelX)<0.02) {
                    DiffAccelX=0;
                }
                if(Math.abs(DiffAccelY)<0.02) {
                    DiffAccelY=0;
                }
                */
                accelX = accelX - InitialAccX;
                //low필터
                if(Math.abs(PreaccX-accelX)<0.02 && Math.abs(accelX)<0.05) {
                    PreaccX=0;
                    accelX=0;
                }
                accelY =- InitialAccY;

                //velocityx[1]= CalcIntegration(velocityx[0],PreDiffAccelX,DiffAccelX,time);
                velocityx[1]= CalcIntegration(velocityx[0],PreaccX,accelX,time);
                positionx[1]= CalcIntegration(positionx[0], velocityx[0],velocityx[1],time);
                velocityy[1]= CalcIntegration(velocityy[0],
                        PreDiffAccelY,DiffAccelY,time);
                positiony[1]= CalcIntegration(positiony[0], velocityy[0],velocityy[1],time);

                //가속도변화가 없을경우 속도를 0으로 초기화
                //X
                if(Math.abs(PreaccX-accelX) < 0.05 && velocityx[2]==0) {
                    velocityx[2]=1;
                }
                else if(Math.abs(PreaccX-accelX) < 0.05 && velocityx[2]==1) {
                    velocityx[1] = 0;
                    velocityx[2] = 0;
                }
                if(Math.abs(PreaccX-accelX) > 0.1) {
                    velocityx[2] = 0;
                }
                //Y
                if(Math.abs(PreaccY-accelY) < 0.05 && velocityy[2]==0) {
                    velocityy[2]=1;
                }
                else if(Math.abs(PreaccY-accelY) < 0.05 && velocityy[2]==1) {
                    velocityy[1] = 0;
                    velocityy[2] = 0;
                }
                if(Math.abs(PreaccY-accelY) > 0.1) {
                    velocityy[2] = 0;
                }
                //accelerationx[0] = accelerationx[1];
                velocityx[0] = velocityx[1];
                positionx[0] = positionx[1];

                velocityy[0] = velocityy[1];
                positiony[0] = positiony[1];


                //MovementX = MovementX + ((event.values[0]-InitialAccX) * dt)/2;
                //MovementY = MovementY + (1/2) * (event.values[1]-InitialAccY) * dt * dt;
                //MovementZ = MovementZ + (1/2) * (event.values[2]-InitialAccZ) * dt * dt;

                Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
                        + "           [Y]:" + String.format("%.4f", event.values[1])
                        + "           [Z]:" + String.format("%.4f", event.values[2])
                        + "           [X]축 속력:" + String.format("%.4f", velocityx[1]*10000)
                        + "           [X]축 총 이동거리 " + String.format("%.4f", positionx[1]*100000)
                        + "           X가속도차 " + String.format("%.4f", PreaccX-accelX)
                        + "           X축에 합산된 이동거리:" + String.format("%.6f", CalcIntegration(positionx[0], velocityx[0],velocityx[1],time)*100)
                        + "           currentTime:" + String.format("%d", System.currentTimeMillis())
                        + "           x" + String.format("%d", x)
                        + "           Y가속도차 " + String.format("%.4f", Math.abs(PreaccY-accelY))
                        + "           [Y]축 이동거리 " + String.format("%.4f", CalcIntegration(positiony[0], velocityy[0],velocityy[1],time)*100)
                        + "           [Z]축 이동거리 " + String.format("%.4f", MovementZ)
                        + "           dt = " + String.format("%.4f ", dt )
                        + "           (event.values[0]-InitialAccX) = " + String.format("%.4f ", (event.values[0]-InitialAccX) )
                        + "           InitialAcc 초기값 " + String.format("%.4f %.4f %.4f", InitialAccX, InitialAccY, InitialAccZ));
            }

            PreaccX = accelX;
            PreaccY = accelY;
            PreaccZ = accZ;

            PreDiffAccelX = DiffAccelX;
            PreDiffAccelY = DiffAccelY;

            MoveX.setText("Move Distance X : "+positionx[1]*100000000);
            MoveY.setText("Move Distance Y : "+positiony[1]*100000000);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

}