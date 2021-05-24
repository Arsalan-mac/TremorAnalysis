package com.example.accelerometerapp1;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private SensorManager sensorManager;
    Sensor accelerometer;
    float x;
    ProgressBar pb;
    private Handler mHandler = new Handler();
    Button b1, b2;

    //Connection declarations
    private static String Classs = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "micdb";
    private static String username = "micadmin";
    private static String password = "Mic@Admin159";
    private static String url = "jdbc:jtds:sqlserver://micazsql.database.windows.net:1433/micdb";
    private Connection connection = null;
    String date;
    String time;
    String deviceId;
    ArrayList<String> mylist = null;
    private int status = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb  = findViewById(R.id.progressBar);
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button3);
        //Creating list and adding values of x in it.
        mylist = new ArrayList<>();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // get Current DateTime
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = simpleDateFormat.format(calendar.getTime());
        // time
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss a");
        time = simpleTimeFormat.format(calendar.getTime());

        b2.setText(date + " " + time );
        // Get Mobile Unique Android ID
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);



    // Start FIRST Test -------------------------------------------------------------------------------------
        b1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            // ProgressBar Update
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (status < 100){
                        status++;
                        android.os.SystemClock.sleep(290);
                        mHandler.post(new Runnable(){
                            @Override
                            public void run() {
                                pb.setProgress(status);
                            }
                        });
                    }
                }
            }).start();


            //Countdown for 30 seconds
            CountDownTimer countDowntimer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    Toast.makeText(MainActivity.this, "Stop recording Automatically ", Toast.LENGTH_LONG).show();
                    //Insert into SQL
                    try {
                        sqlInsert();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Toast.makeText(MainActivity.this,"Data insertion Failed",Toast.LENGTH_SHORT).show();
                    }

                    // I HAVE TO INSERT X DATA HERE!!!!!!!!

                    startActivity(new Intent(getApplicationContext(),MainActivity2.class));
                    finish();
                }};
            countDowntimer.start();


            //SQL Connection Establishment!
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                Class.forName(Classs);
                connection = DriverManager.getConnection(url,username,password);
                Statement sttete = connection.createStatement();
                // tv4.setText("Connection Success");
                Toast.makeText(MainActivity.this,"Connection Successful",Toast.LENGTH_SHORT).show();
            }

            catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"Connection NOT Successful",Toast.LENGTH_SHORT).show();
            }

        }
        });
    // --------------------------------------------------------------------------------------------------------------
    }

        //ON CREATE METHOD ENDS HERE-------------------------------------------------------------------------------


    //Insert Queries
    public void sqlInsert() throws SQLException {
        if (connection != null){
            Statement statement = null;
            statement = connection.createStatement();
            int resultSet = statement.executeUpdate("INSERT INTO dbo.tremor (devId, valDate ,valTime, valTrem) VALUES ('" + deviceId.toString() + "','"+ date + "','" +time+ "','" + mylist+"');");
            Toast.makeText(MainActivity.this,"Data Insertion Successful",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Sensor data
        x = sensorEvent.values[0];
        mylist.add(Float.toString(x)); //this adds an element to the list.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}