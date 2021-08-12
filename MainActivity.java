package com.example.accelerometerapplicationcemit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.Properties;







public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xValAcc, yValAcc, zValAcc, xValGyr, yValGyr, zValGyr, currentTime, currentMilliTime;
    private Sensor accelerometer, gyroscope;
    private SensorManager sensorManager1, sensorManager2;

    Switch swHertz;
    int samplingPeriodSlow = 1000000; // microseconds // 1Hz
    int samplingPeriodFast = 20000; // microseconds // 50Hz
    int samplingPeriod = samplingPeriodSlow;
    int sleepyTime = 1000;

    public static final int DEFAULT_UPDATE_INTERVAL = 20;
    public static final int FAST_UPDATE_INTERVAL = 10;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    TextView tvLat, tvLon, tvAccuracy, tvSpeed, tvAltitude, tvUpdates;
    Switch swLocationUpdates, swGPS;
    boolean updateOn = false;

    private TextView xText, yText, zText;
    //private Sensor mySensor;
    //private SensorManager sensorManager;
    private TextView tText;
    //private Location myLocation;
    //private LocationManager locationManager;
    //private LocationListener locationListener;

    // Google API for location
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    // Timer t = new java.util.Timer();

    String[] lineForPrint;
    String lineForPrintLine;
    String FILE_NAME = "TestFiles";
    //List<String> dataLines= new ArrayList<String>();
    String dataLines = "";
    Integer fileCounter = 1;
    long minutes = 0;
    long currentMinutes = 0;

    String gyrValueString, accValueString, timeStamp, timeStampMs;
     //Mail Details
    final String emailReciever = "CemitTesterKonto@gmail.com";
    final String emailSender = "CemitTesterKontoo@gmail.com";
    final String subject = "Data transfer of file: ";
    final String password = "CTK2021!";



    // char delay = SENSOR_DELAY_FASTEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Date currentDateTime;// = Calendar.getInstance().getTime();
        System.out.println("Feil2");
        // Create sensorManager
        sensorManager1 = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager2 = (SensorManager) getSystemService(SENSOR_SERVICE);
        System.out.println("Feil3");
        // Accelerometer Sensor
        accelerometer = sensorManager1.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager2.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        System.out.println("Feil4");
        // Register sensor listener.
        sensorManager1.registerListener(this, accelerometer, samplingPeriod);
        sensorManager2.registerListener(this, gyroscope, samplingPeriod);
        //sensorManager1.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        System.out.println("Feil5");
        // Assign TextView
        xValAcc = (TextView) findViewById(R.id.xValAcc);
        yValAcc = (TextView) findViewById(R.id.yValAcc);
        zValAcc = (TextView) findViewById(R.id.zValAcc);
        System.out.println("Feil6");
        xValGyr = (TextView) findViewById(R.id.xValGyr);
        yValGyr = (TextView) findViewById(R.id.yValGyr);
        zValGyr = (TextView) findViewById(R.id.zValGyr);
        System.out.println("Feil7");
        currentTime = (TextView) findViewById(R.id.tvCurrentTime);
        currentMilliTime = (TextView) findViewById(R.id.tvCurrentMilliTime);
        swHertz = (Switch) findViewById(R.id.swHertz);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        System.out.println("Feil8");
        swHertz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swHertz.isChecked()) {
                    sleepyTime = 200; // 5 Hz
                    //System.out.println(Arrays.toString(lineForPrint));
                } else {
                    sleepyTime = 1000; // 1 Hz
                }
            }
        });
        System.out.println("Feil9");

        //GPS - txt comp
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);
        tvAccuracy = (TextView) findViewById(R.id.tvAccuracy);
        tvAltitude = (TextView) findViewById(R.id.tvAltitude);
        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
        tvUpdates = findViewById(R.id.tvUpdates);
        swGPS = findViewById(R.id.swGPS);
        swLocationUpdates = findViewById(R.id.swLocationUpdates);
        System.out.println("Feil10");
        // LocationStuff
        locationRequest = LocationRequest.create()
                .setInterval(DEFAULT_UPDATE_INTERVAL)
                .setFastestInterval(FAST_UPDATE_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_UPDATE_INTERVAL);
        System.out.println("Feil11");
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // Save the location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);

                // updateUIValues(locationResult.getLastLocation()); // Alt.
            }
        };
        System.out.println("Feil12");
        /*
        swGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swGPS.isChecked()) {
                    // most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tvSensor.setText("using GPS sensors");
                }
            }
        });
        */
        System.out.println("Feil13");
        swLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swLocationUpdates.isChecked()) {
                    // Turn on location tracking
                    startLocationUpdates();
                } else {
                    // Turn off tracking
                    stopLocationUpdates();
                }
            }
        });
        System.out.println("Feil14");

        /*
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setMaxWaitTime(100);
        */

        updateGPS();

    }

    private void startLocationUpdates() {
        tvUpdates.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    private void stopLocationUpdates() {
        tvUpdates.setText("Location is not being tracked");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

        }
    }
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // User provided permission
            //fusedLocationProviderClient.getCurrentLocation().addOnSuccessListener(this::updateUIValues, location -> );
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // We got permission put in stuff.
                    // location = fusedLocationProviderClient.getCurrentLocation(this, CancellationTokenSource);
                    updateUIValues(location);

                }
            });
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        if (location == null){
            System.out.println("Nulleverdi lokasjon");
            return;
        }

        // Update all of the text view objects with a new location.
        tvLat.setText(String.valueOf(location.getLatitude()));
        tvLon.setText(String.valueOf(location.getLongitude()));
        tvAccuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()) {
            tvAltitude.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tvAltitude.setText("Not available!");
        }
        if(location.hasSpeed()) {
            tvSpeed.setText(String.valueOf(location.getSpeed()));
        }
        else{
            tvSpeed.setText("Not available!");
        }

        String locationData = ";;;;;;" + String.valueOf(location.getLatitude()) + ";" + String.valueOf(location.getLongitude()) + ";" + timeStampMs;
        writeStringToFile(locationData, true);
    }
    public synchronized void updateTime() {
        long detailTime = Calendar.getInstance().getTimeInMillis();
        minutes = detailTime/10000;
        System.out.println(detailTime);
        System.out.println("Minutes: " + minutes);
        System.out.println("CurrentMinutes: " + currentMinutes);
        if (currentMinutes == 0){
            currentMinutes = minutes;
        }

        Date currentDateTime = Calendar.getInstance().getTime();

        currentTime.setText(currentDateTime.toString());
        currentMilliTime.setText(String.valueOf(detailTime));

        timeStamp = currentDateTime.toString();
        timeStampMs = String.valueOf(detailTime);
    }

    @Override
    public synchronized void onSensorChanged(SensorEvent event) {
        String dataLineForFile = "";

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xValAcc.setText(String.valueOf(event.values[0]));
            yValAcc.setText(String.valueOf(event.values[1]));
            zValAcc.setText(String.valueOf(event.values[2]));
            double[] accValues = {event.values[0], event.values[1], event.values[2]};
            accValueString = Arrays.toString(accValues);
            String accDataString = String.valueOf(event.values[0]) + ";" + String.valueOf(event.values[1])
                    + ";" + String.valueOf(event.values[2]) + ";";
            dataLineForFile += accDataString;

        }
        else{
            dataLineForFile += ";;;";
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            xValGyr.setText(String.valueOf(event.values[0]));
            yValGyr.setText(String.valueOf(event.values[1]));
            zValGyr.setText(String.valueOf(event.values[2]));

            double[] gyrValues = {event.values[0], event.values[1], event.values[2]};
            gyrValueString = Arrays.toString(gyrValues);
            String gyroDataString = gyrValues[0] + ";" + String.valueOf(event.values[1])
                    + ";" + String.valueOf(event.values[2]) + ";";
            //System.out.println("Henta ut Gyro data: " + gyroDataString);
            dataLineForFile += gyroDataString;

        }
        else {
            dataLineForFile += ";;;";
        }

        updateTime();
        String[] lineForPrint = {gyrValueString, accValueString, timeStamp, timeStampMs};
        System.out.println(Arrays.toString(lineForPrint));
        dataLineForFile += ";;" + timeStampMs;
        writeStringToFile(dataLineForFile, false);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use atm.
    }

    public void writeStringToFile(String dataLine,boolean isLocation){
        if (minutes != currentMinutes){
            System.out.print(minutes-currentMinutes);
            writeArrayToFile(dataLines);
            dataLines = "";
            currentMinutes = minutes;
        }
        dataLines += dataLine + "\n";
    }

    public void writeArrayToFile(String dataLines){
        System.out.println("WriteArray kjører");
        FileOutputStream fos = null;
        String fileName = FILE_NAME + fileCounter + ".csv";
        fileCounter += 1;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            fos.write(dataLines.getBytes());
            //for (int i = 0; i < dataLines.size(); i++) {
                //String dataLine = (String) dataLines.get(i);
                //fos.write(dataLine.getBytes());
                //System.out.println(dataLines.get(i));
            //Toast.makeText(this, "Saved to: " + getFilesDir() + "/" + fileName, Toast.LENGTH_SHORT).show();
            String filePath = getFilesDir() + "/" + fileName;
            System.out.println("Saved to: " + filePath);
            sendEmail(filePath);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //System.out.println(cvsFormatString);

    }
    //email sender function
    public void sendEmail(String filePath){
        Properties props = new Properties();
        Session session = Session.getInstance(props, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthetication(){
                return new PasswordAuthentication(emailSender, password);
            }
        });
        //mail Auth
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.transport.protocl", "smtp");


        try{
            System.out.println("File from : " + filePath + " is being sent to: " + emailReciever);
            Message message = new MimeMessage(session);
            message.setSubject(subject);
            Address adressTo = new InternetAddress(emailReciever);

            message.setRecipient(Message.RecipientType.TO, adressTo);

            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart attachment = new MimeBodyPart();
            attachment.attachFile(filePath);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("<h1>Email from Cemit Tester</h1>", "text/html");
            multipart.addBodyPart(messageBodyPart);
            //multipart.addBodyPart(attachment);

            message.setContent(multipart);

            //Transport.send(message);
        }
        catch (MessagingException | IOException e){
            throw new RuntimeException(e);
        }
    }






}