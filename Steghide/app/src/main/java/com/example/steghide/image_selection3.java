package com.example.steghide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

public class image_selection3 extends AppCompatActivity {

    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection3);
    }

    public void selectImage(View view){
        cargarImagen();
    }

    final int PICK_IMAGE = 1;

    public void cargarImagen(){

        Intent galeria = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galeria.addCategory(Intent.CATEGORY_OPENABLE);
        galeria.setType("image/*");
        startActivityForResult(Intent.createChooser(galeria,"Seleccione la Imagen"),PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri path = data.getData();
            File f = new File(""+path);
            fname = f.getName();
            Intent intent = new Intent(this,image_selection4.class);
            intent.putExtra("path", path.toString());
            intent.putExtra("fname", fname);
            startActivity(intent);
            finish();
        }
    }
}