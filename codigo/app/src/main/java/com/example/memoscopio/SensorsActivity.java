package com.example.memoscopio;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener  {
    private SensorManager sensorManager;

    private TextView acelerometro;
    private TextView proximidad;

    private String x;
    private String y;
    private String z;
    private String p;

    private int index;

    private SharedPreferences preferences;
    private DecimalFormat format;

    private final ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ListView listView = findViewById(R.id.listView);
        acelerometro = findViewById(R.id.acelerometro);
        proximidad = findViewById(R.id.proximidad);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(saveHandler);

        // formato para numeros flotantes
        format = new DecimalFormat("#.##");

        // lee el objeto preferencias
        preferences = getSharedPreferences("sensors", MODE_PRIVATE);
        // lee el indice o lo inicializa en 0
        index = preferences.getInt(Constants.INDEX_PREFERENCE, 0);

        // recorre los valores de sensores almacenados y los guarda en una lista
        for(int i=0; i<=index; i++){
            String str = preferences.getString(Constants.STORE_PREFERENCE + i, "");
            list.add(str);
        }

        // vincula esa lista de datos con un ListView, para mostrarlos en pantalla
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.sensors_listview, list);
        listView.setAdapter(adapter);
    }

    // metodo para guardar una instantanea de los valores del sensor
    private final View.OnClickListener saveHandler = (_v) -> {
        String str = "SNAPSHOT -> " + x + ", " + y + ", " + z + ", " + p;
        list.add(str);
        index++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.STORE_PREFERENCE + index, str);
        editor.putInt(Constants.INDEX_PREFERENCE, index);
        editor.apply();
    };

    // lee y sincroniza los eventos de los sensores
    // muestra los valores de los sensores en pantalla
    @Override
    public void onSensorChanged(SensorEvent event) {
        String txt = "";

        synchronized (this) {
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    txt += "acelerometro:\n";
                    x = "x: " + format.format(event.values[0]);
                    y = "y: " + format.format(event.values[1]);
                    z = "z: " + format.format(event.values[2]);
                    txt += x + "\n" + y + "\n" + z + "\n";
                    acelerometro.setText(txt);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    txt += "proximidad:\n";
                    p = "p: " + format.format(event.values[0]);
                    txt += p + "\n";
                    proximidad.setText(txt);
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSensors();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startSensors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSensors();
    }

    protected void startSensors(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void stopSensors(){
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }

}