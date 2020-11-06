package com.example.steghide;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private Button hide_button;
    private Button decode_button;
    String fname;
    int option;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

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
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void information(View view){
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.information, null);

        // popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void open_hide_image_selection_activity(View view){
        option = 0;
        cargarImagen();
    }
    public void open_decode_image_selection_activity(View view){
        option = 1;
        cargarImagen();
    }

    public void cargarImagen(){
        final int PICK_IMAGE = 1;
        Intent galeria = new Intent(Intent.ACTION_GET_CONTENT);
        //String [] mimeTypes = {"image/png", "image/jpg"};
        galeria.setType("image/*");
        //galeria.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(galeria,"Seleccione la Imagen"),PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            Uri path = data.getData();
            fname = path.getLastPathSegment();
            if (option == 0){
                Intent intent = new Intent(this,activity_image_selection2.class);
                intent.putExtra("path", path.toString());
                intent.putExtra("fname", fname);
                startActivity(intent);
            }else{
                if (option == 1){
                    Intent intent = new Intent(this,image_selection4.class);
                    intent.putExtra("path", path.toString());
                    intent.putExtra("fname", fname);
                    startActivity(intent);
                }
            }

        }
    }
}
