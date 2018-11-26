package com.nvwa.hw3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static java.lang.Math.random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static int[] state = {12, 20, 30}; // flowey's expression: 1-11 good, 12-19 neutral-good, 20-29 - neutral-bad, 30-41 bad
    private MediaPlayer mp;
    private int counter = 0;
    static public SensorManager mSensorManager;
    public List<Sensor> SensorList;
    boolean sToggledL = false, sToggledP = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        final TextView dialog = findViewById(R.id.dialog);
        ImageView flowey = findViewById(R.id.flowey);
        final TextView response = findViewById(R.id.floweyResponse);

        flowey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
            }
        });
        flowey.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                counter++;
                return false;
            }
        });
        flowey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    String touchResponse[];
                    if (counter < 10) {
                        ( (ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.sprite13, null) );
                        touchResponse = getResources().getStringArray(R.array.floweyTouchResponseGood);
                    } else {
                        ( (ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.sprite28, null) );
                        touchResponse = getResources().getStringArray(R.array.floweyTouchResponseBad);
                        dialog.setText("");
                        if ( counter == 10 ) {
                            mp.release();
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.themebad);
                            mp.setLooping(true);
                            mp.start();
                        }
                    }
                    response.setText( touchResponse[ (int)(random() * touchResponse.length) ] );
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    if (counter < 10)
                        ( (ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.sprite1, null) );
                    else {
                        dialog.setText("");
                        ( (ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.sprite29, null) );
                    }
                    response.setText("");
                }
                return false;
            }
        });
        flowey.setImageDrawable( getResources().getDrawable( R.drawable.sprite1, null ) );
    }

    public void generateAnswer() {
        TextView dialog = findViewById(R.id.dialog);
        ImageView flowey = findViewById(R.id.flowey);
        TextView response = findViewById(R.id.floweyResponse);

        // Logic to change dialog (text above) if flowey is not angry
        // Chance to change is 5%
        if ( counter < 10 ) {
            double rndDialogChange = random() * 100;
            if (rndDialogChange < 5 ||
                    dialog.getText().toString().equals(getResources().getString(R.string.annoyingDogDialog))) {
                String[] dialogArray = getResources().getStringArray(R.array.floweyDialog);
                dialog.setText(dialogArray[(int) (random() * dialogArray.length)]);
            }
        }

        // Logic to generate flowey's emotion and then to match with an answer
        // 5% there will be a dog, if flowey is not angry
        int rndDog = (int) (random() * 100);
        if ( rndDog < 5 && counter < 10 ) {
            dialog.setText(R.string.annoyingDogDialog);
            flowey.setImageDrawable( getResources().getDrawable(R.drawable.sprite42, null) );
            response.setText(R.string.annoyingDogDialog);
        } else {
            int rndExpression;
            if ( counter < 10 )
                rndExpression = (int) (random() * 29) + 1; // 1-29 flowey is neutral, will not give very bad answers
            else
                rndExpression = (int) (random() * 12) + 30; // 30-41 flowey is angry, always negative answers
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
        if ( counter < 10 )
            mp = MediaPlayer.create( this, R.raw.theme );
        else
             mp = MediaPlayer.create( this, R.raw.themebad );
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }});
        for ( int i = 0; i < SensorList.size(); i++ )
            if ( SensorList.get(i).getType() == Sensor.TYPE_LIGHT || SensorList.get(i).getType() == Sensor.TYPE_PROXIMITY )
                mSensorManager.registerListener(this, SensorList.get(i), 500000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mp.release();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float sensVal = event.values[0];
        if ( event.sensor.getType() == Sensor.TYPE_LIGHT && sensVal < 100 && !sToggledL ) {
            floweyListenState();
            sToggledL = true;
        } else if ( event.sensor.getType() == Sensor.TYPE_PROXIMITY && sensVal < 1 && !sToggledP ) {
            floweyListenState();
            sToggledP = true;
        } else if ( event.sensor.getType() == Sensor.TYPE_LIGHT && sensVal > 1000 && sToggledL ) {
            sToggledL = false;
            if ( !sToggledP )
                generateAnswer();
        } else if ( event.sensor.getType() == Sensor.TYPE_PROXIMITY && sensVal > 3 && sToggledP ) {
            sToggledP = false;
            if ( !sToggledL )
                generateAnswer();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void floweyListenState() {
        TextView dialog = findViewById(R.id.dialog);
        ImageView flowey = findViewById(R.id.flowey);
        TextView response = findViewById(R.id.floweyResponse);

        if (counter < 10)
            flowey.setImageDrawable( getResources().getDrawable(R.drawable.sprite2, null) ); // wink animation
        else {
            dialog.setText("");
            flowey.setImageDrawable(getResources().getDrawable(R.drawable.sprite24, null));
        }

        response.setText(R.string.listening);
    }
}
