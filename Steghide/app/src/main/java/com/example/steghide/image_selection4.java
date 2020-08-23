package com.example.steghide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class image_selection4 extends AppCompatActivity {

    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection4);
        imagen = (ImageView) findViewById(R.id.imageView3_id);

        Intent intent = getIntent();
        String path= intent.getStringExtra("path");
        Uri fileUri = Uri.parse(path);
        imagen.setImageURI(fileUri);
    }
}