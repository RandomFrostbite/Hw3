package com.nvwa.hw3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.floor;
import static java.lang.Math.random;

public class MainActivity extends AppCompatActivity {

    public static int[] state = {12, 20, 30}; // 1-11 good, 12-19 neutral-good, 13-29 - neutral-bad, 29-41 bad

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView flowey = (ImageView)findViewById(R.id.flowey);
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

        flowey.setImageDrawable(getResources().getDrawable(R.drawable.sprite1));
    }

    public void generateAnswer() {
        TextView dialog = (TextView) findViewById(R.id.dialog);
        ImageView flowey = (ImageView) findViewById(R.id.flowey);
        TextView response = (TextView) findViewById(R.id.floweyResponse);

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
            flowey.setImageDrawable(getResources().getDrawable(R.drawable.sprite42));
            response.setText(R.string.annoyingDogDialog);
        } else {
            int rndExpression = (int) (random() * 40) + 1; // 1-41
            int s = 0; // state
            for (int i = 0; i < state.length; i++) {
                if (rndExpression >= state[i])
                    s = i+1;
            }
            flowey.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("sprite" + rndExpression, "drawable", getPackageName())));

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
}