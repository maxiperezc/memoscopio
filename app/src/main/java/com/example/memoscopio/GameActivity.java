package com.example.memoscopio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private final static String[] eventNames = new String[] {"STARTING", "PLAYING", "PAUSED", "FINISHED"};

    private SensorManager manager;
    private GameView gameView;

    public IntentFilter filter;
    private Callback callback = new Callback();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        gameView = new GameView(GameActivity.this, width, height);
        setContentView(gameView);

        configureReceiver();
    }

    public void sendEvent(Enum event){
        JSONObject data = new JSONObject();
        try {
            data.put("type_events", "GAME_STATE");
            data.put("description", event.toString());
        } catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(GameActivity.this, UnlamService.class);
        intent.putExtra("uri", Constants.EVENT_URI);
        intent.putExtra("action", UnlamService.ACTION_EVENT);
        intent.putExtra("data", data.toString());
        startService(intent);
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    gameView.move(event.values[0], event.values[1]);
                    break;
                case Sensor.TYPE_PROXIMITY:
                    gameView.help(event.values[0] == 0);
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
        unregisterReceiver(callback);
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
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, manager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_GAME);
    }

    protected void stopSensors(){
        manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        manager.unregisterListener(this, manager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
    }


    private void configureReceiver(){
        filter = new IntentFilter(UnlamService.ACTION_EVENT);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(callback, filter);
    }


    public class Callback extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String data = intent.getStringExtra("data");
                JSONObject json = new JSONObject(data);

                Log.i("LOGUEO EVENTO", "Datos: " + data );

                String success = json.getString("success");

                if(success == "true"){
                    Log.i("LOGUEO EVENTO OK", "Datos: " + data );
                } else {
                    Log.i("LOGUEO EVENTO FAIL", "Datos: " + data );
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}

