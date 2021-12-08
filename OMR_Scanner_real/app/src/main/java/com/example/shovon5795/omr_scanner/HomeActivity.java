package com.example.shovon5795.omr_scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.Utils;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    Button capture, evaluate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        capture = (Button) findViewById(R.id.capture);
        evaluate = (Button) findViewById(R.id.evaluate);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("type","Main OMR");
                startActivity(intent);
            }

        });
        evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, resultActivity.class);
                startActivity(intent);
            }

        });
    }

}