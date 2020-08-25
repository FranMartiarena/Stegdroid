package com.example.steghide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class save_image extends AppCompatActivity {

    ImageView vista_imagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        vista_imagen = findViewById(R.id.imageView3_id);
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("BitmapImage");
        vista_imagen.setImageBitmap(bitmap);
        vista_imagen.buildDrawingCache();
        vista_imagen.setDrawingCacheEnabled(true);
        //int new_pixel = bitmap.getPixel(0,0);
        //int newRedValue = Color.red(new_pixel);
        //Toast.makeText(this, "nuevo: "+ newRedValue, Toast.LENGTH_LONG).show();

    }


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void saveImage(View view){
        verifyStoragePermissions(this);
        OutputStream fOut = null;
        Bitmap bitmap = vista_imagen.getDrawingCache();//PROBLEMA

        try {
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "Hidden.png");
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            /*
            File file = new File(Environment.getExternalStorageDirectory() + File.separator +"Download" + File.separator + "Hidden.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();*/

        } catch (Exception e) {
            Toast.makeText(this, "Error occured: "+ e, Toast.LENGTH_SHORT).show();
        }

    }
}