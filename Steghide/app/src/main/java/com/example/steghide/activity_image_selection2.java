package com.example.steghide;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.view.View;


public class activity_image_selection2 extends AppCompatActivity {

    ImageView vista_imagen;
    String message;
    EditText editTextTextMultiLines;
    String path;
    String fname;
    ProgressDialog progressDialog;
    Button button;
    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection2);
        button = findViewById(R.id.done_button_id);
        vista_imagen = findViewById(R.id.imageView2_id);
        vista_imagen.setDrawingCacheEnabled(true);
        vista_imagen.buildDrawingCache(true);
        editTextTextMultiLines = findViewById(R.id.editTextTextMultiLineId);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        fname = intent.getStringExtra("fname");
        Uri fileUri = Uri.parse(path);

        vista_imagen.setImageURI(fileUri);

        try {
            String message= intent.getStringExtra("message");
            editTextTextMultiLines.setText(message);
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_LONG).show();
        }

        button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    progressDialog = new ProgressDialog(activity_image_selection2.this);
                    progressDialog.setContentView(R.layout.activity_loading);
                    progressDialog.setMessage("Saving ..."); // Setting Message
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    progressDialog.setCancelable(false);

                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                encode();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    });


                }

        }

        );

    }


   /* public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }*/





    public void encode(){

        vista_imagen.invalidate();
        BitmapDrawable drawable = ((BitmapDrawable) vista_imagen.getDrawable());
        Bitmap bitmap = drawable.getBitmap();
        bitmap = bitmap.copy(bitmap.getConfig() , true);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (bitmap.getWidth() > 2500 || bitmap.getHeight() > 2500){
            //bitmap = getResizedBitmap(bitmap, width/2, height/2);
            //Toast.makeText(this, "Nuevo tamaño:"+bitmap.getWidth()+"x"+bitmap.getHeight(), Toast.LENGTH_LONG).show();
            //cantidad_bytes_img = cantidad_bytes_img /2;
            //width = width/2;
            //height = height/2;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Error: Select a smaller image", Toast.LENGTH_LONG).show();
            return;
        }



        message = "#%#%#"+editTextTextMultiLines.getText().toString()+"#%#&-&%#%&&/%#";
        if (message.matches("#%#%##%#&-&%#%&&/%#")) {
            Toast.makeText(this, "¡No secret message to hide!", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] message_in_bits = message.getBytes(); //mensaje a byte array
        StringBuilder binary = new StringBuilder();
        Toast.makeText(this, "getbytes = "+message_in_bits, Toast.LENGTH_SHORT).show();

        for (byte b : message_in_bits){  //Sacamos cantidad de bytes en el mensaje
            int val = b;
            for (int i = 0; i < 8; i++){
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }

        }





        int cantidad_bits_msg = binary.length();
        int cantidad_bytes_img = (width*height)*3;

        int count = 0;
        //bitmap = getResizedBitmap(bitmap, width, height);

        //Toast.makeText(this, "Viejo tamaño:"+bitmap.getWidth()+"x"+bitmap.getHeight(), Toast.LENGTH_LONG).show();

        /*if (bitmap.getWidth() > 2500 || bitmap.getHeight() > 2500){
            bitmap = getResizedBitmap(bitmap, width/2, height/2);
            Toast.makeText(this, "Nuevo tamaño:"+bitmap.getWidth()+"x"+bitmap.getHeight(), Toast.LENGTH_LONG).show();
            cantidad_bytes_img = cantidad_bytes_img /2;
            width = width/2;
            height = height/2;

        }*/


        if(cantidad_bytes_img < cantidad_bits_msg){
            Toast.makeText(this, "Error: The image is not big enough for the message to encode", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else {
            //Log.d("TAG", "Este es el tamaño de la imagen:"+String.valueOf(width)+" "+String.valueOf(height));
            //editTextTextMultiLines.setText(String.valueOf(cantidad_bytes_img));

            //int old_pixel = bitmap.getPixel(0,0);
            //int oldRedValue = Color.red(old_pixel);

            outerloop:
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){

                    int pixel = bitmap.getPixel(x,y);


                    if (count >= cantidad_bits_msg){
                        //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                        break outerloop;
                    }
                    else{

                        int redValue = Color.red(pixel);
                        int greenValue = Color.green(pixel);
                        int blueValue = Color.blue(pixel);

                        String redValueBynaryString = Integer.toBinaryString(redValue);
                        String greenValueBynaryString = Integer.toBinaryString(greenValue);
                        String blueValueBynaryString = Integer.toBinaryString(blueValue);

                        //Log.d("TAG", "El valor r del pixel:"+"["+String.valueOf(x)+","+String.valueOf(y)+"]"+"es: "+ String.valueOf(redValue)+" que es en binario; "+redValueBynaryString);

                        //insertar bit en r
                        redValueBynaryString = redValueBynaryString.substring(0,redValueBynaryString.length()-1)+binary.charAt(count);
                        //redValueBynaryString = binary.charAt(count)+redValueBynaryString.substring(1,redValueBynaryString.length());
                        int newRedValue = Integer.parseInt(redValueBynaryString, 2);//new red binary value to decimal
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, greenValue, blueValue));//use this to change pixel values xD
                        //Log.d("TAG","Cambie "+redValue+" por "+newRedValue);
                        count = count + 1;

                        if (count >= cantidad_bits_msg){
                            //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                            break outerloop;
                        }
                        //insertar bit en g
                        greenValueBynaryString = greenValueBynaryString.substring(0,greenValueBynaryString.length()-1)+binary.charAt(count);
                        //greenValueBynaryString = binary.charAt(count)+greenValueBynaryString.substring(1,greenValueBynaryString.length());
                        int newGreenValue = Integer.parseInt(greenValueBynaryString, 2);
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, newGreenValue, blueValue));//use this to change pixel values xD
                        //Log.d("TAG","Cambie "+greenValue+" por "+newGreenValue);
                        count = count + 1;

                        if (count >= cantidad_bits_msg){
                            //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                            break outerloop;
                        }
                        //insertar bit en b
                        blueValueBynaryString = blueValueBynaryString.substring(0,blueValueBynaryString.length()-1)+binary.charAt(count);
                        //blueValueBynaryString = binary.charAt(count)+blueValueBynaryString.substring(1,blueValueBynaryString.length());
                        int newBlueValue = Integer.parseInt(blueValueBynaryString, 2);
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, newGreenValue, newBlueValue));//use this to change pixel values xD
                        //Log.d("TAG","Cambie "+blueValue+" por "+newBlueValue);
                        count = count + 1;

                    }


                }
            }


            try {
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                File myDir = new File(root + "/Steghide");
                myDir.mkdirs();
                File file = new File (myDir, fname+".png");
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
                progressDialog.dismiss();
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                /*

                String filename = "bitmap";


                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,stream);
                stream.close();

                Intent intent = new Intent(this, save_image.class);
                intent.putExtra("fname", fname);
                startActivity(intent);*/


            }
            catch (Exception e){
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }


        }




    }




}
