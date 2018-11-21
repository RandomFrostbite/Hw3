package com.nvwa.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static java.lang.Math.random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static int[] state = {12, 20, 30}; // 1-11 good, 12-19 neutral-good, 13-29 - neutral-bad, 29-41 bad
    private MediaPlayer mp;
    static public SensorManager mSensorManager;
    public List<Sensor> SensorList;
    boolean sToggled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorList = mSensorManager.getSensorList(Sensor.TYPE_LIGHT);
        //mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY); // add somehow

        ImageView flowey = findViewById(R.id.flowey);
        flowey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAnswer();
            }
        });
        flowey.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                generateAnswer();
                return false;
            }
        });

        /*
        flowey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN )
                    ((ImageView)v).setImageDrawable( getResources().getDrawable(R.drawable.sprite2) ); // wink animation
                else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    generateAnswer();
                    v.performClick();
                }
                return false;
            }
        });
        */

        flowey.setImageDrawable( getResources().getDrawable( R.drawable.sprite1, null ) );
    }

    public void generateAnswer() {
        TextView dialog = findViewById(R.id.dialog);
        ImageView flowey = findViewById(R.id.flowey);
        TextView response = findViewById(R.id.floweyResponse);

        // Logic to change dialog (text above)
        // Chance to change is 5%
        double rndDialogChange = random() * 100;
        if (rndDialogChange < 5 ||
                dialog.getText().toString().equals(getResources().getString(R.string.annoyingDogDialog))) {
            String[] dialogArray = getResources().getStringArray(R.array.floweyDialog);
            dialog.setText(dialogArray[(int) (random() * dialogArray.length)]);
        }

        // Logic to generate flowey's emotion and then to match with an answer
        // 5% there will be a dog
        int rndDog = (int) (random() * 100);
        if (rndDog < 5) {
            dialog.setText(R.string.annoyingDogDialog);
            flowey.setImageDrawable( getResources().getDrawable(R.drawable.sprite42, null) );
            response.setText(R.string.annoyingDogDialog);
        } else {
            int rndExpression = (int) (random() * 40) + 1; // 1-41
            int s = 0; // state
            for (int i = 0; i < state.length; i++) {
                if (rndExpression >= state[i])
                    s = i+1;
            }
            flowey.setImageDrawable( getResources().getDrawable( getResources().getIdentifier("sprite" + rndExpression, "drawable", getPackageName() ), null ) );

            String Response[] = new String[10];
            switch (s) {
                case 0:
                    Response = getResources().getStringArray(R.array.floweyAnswersPositive);
                    break;
                case 1:
                    Response = getResources().getStringArray(R.array.floweyAnswersNeutralPositive);
                    break;
                case 2:
                    Response = getResources().getStringArray(R.array.floweyAnswersNeutralNegative);
                    break;
                case 3:
                    Response = getResources().getStringArray(R.array.floweyAnswersNegative);
                    break;
            }
            response.setText(Response[(int) (random() * Response.length)]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
        for ( int i = 0; i < SensorList.size(); i++ )
            mSensorManager.unregisterListener(this, SensorList.get(i) );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mp = MediaPlayer.create( this, R.raw.theme );
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }});
        for ( int i = 0; i < SensorList.size(); i++ )
            mSensorManager.registerListener(this, SensorList.get(i), 500000 );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.release();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensVal = event.values[0];
        if ((event.sensor.getType() == Sensor.TYPE_LIGHT && sensVal < 100) ||
                (event.sensor.getType() == Sensor.TYPE_PROXIMITY && sensVal == 0)) {
            sToggled = true;
        } else {
            if (sToggled) {
                sToggled = false;
                generateAnswer();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
