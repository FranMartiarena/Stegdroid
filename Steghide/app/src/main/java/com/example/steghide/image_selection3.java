package com.example.steghide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class image_selection3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection3);
    }

    public void selectImage(View view){
        cargarImagen();
    }


    public void cargarImagen(){
        Intent galeria=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/");
        startActivityForResult(galeria.createChooser(galeria,"Seleccione la aplicacion"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri path = data.getData();
            Intent intent = new Intent(this,image_selection4.class);
            intent.putExtra("path", path.toString());
            startActivity(intent);
        }
    }
}