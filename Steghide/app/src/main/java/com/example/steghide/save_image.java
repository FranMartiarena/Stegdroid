package com.example.steghide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
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
    Bitmap bitmap;
    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        vista_imagen = findViewById(R.id.imageView3_id);
        byte[] byteArray = getIntent().getByteArrayExtra("BitmapImage");
        fname = getIntent().getStringExtra("fname");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        vista_imagen.setImageBitmap(bitmap);
        vista_imagen.buildDrawingCache();
        vista_imagen.setDrawingCacheEnabled(true);
        //int new_pixel = bitmap.getPixel(0,0);
        //int newRedValue = Color.red(new_pixel);
        //Toast.makeText(this, "nuevo: "+ newRedValue, Toast.LENGTH_LONG).show();


    }


    public void saveImage(View view){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/Steghide");
        myDir.mkdirs();

        File file = new File (myDir, fname+".png");
        try {

            FileOutputStream out = new FileOutputStream(file);
            //OutputStream outstream;
            //ContentValues values = new ContentValues();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            //values.put(MediaStore.Images.Media.DISPLAY_NAME, fname);
            values.put(MediaStore.Images.Media.TITLE, fname);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            MediaScannerConnection.scanFile(this,
                    new String[] {MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();

        }

        catch (Exception e) {
            Toast.makeText(this, "Error occured: "+ e, Toast.LENGTH_SHORT).show();
            Log.d("TAG", ""+e);
        }


    }


}
