package com.example.steghide;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button hide_button;
    private Button decode_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void open_hide_image_selection_activity(View view){
        Intent intent = new Intent(this,image_selection.class);
        startActivity(intent);
    }
    public void open_decode_image_selection_activity(View view){
        Intent intent = new Intent(this,image_selection3.class);
        startActivity(intent);
    }
}
