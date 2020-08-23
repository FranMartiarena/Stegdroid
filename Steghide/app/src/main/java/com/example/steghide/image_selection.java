package com.example.steghide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class image_selection extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection1);

    }

    public void selectImage(View view){
        cargarImagen();
    }


    public void cargarImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/");
        startActivityForResult(galeria.createChooser(galeria,"Seleccione la Imagen"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri path = data.getData();
            Intent intent = new Intent(this,activity_image_selection2.class);
            intent.putExtra("path", path.toString());
            startActivity(intent);
        }
    }
}