package com.nvwa.hw3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView flowey = (ImageView)findViewById(R.id.flowey);
        flowey.setImageDrawable( getResources().getDrawable(R.drawable.sprite1) );
    }
}
