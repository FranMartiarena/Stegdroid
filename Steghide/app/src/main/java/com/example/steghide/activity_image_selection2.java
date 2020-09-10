package com.example.steghide;
//TO DO: Poder pasar bitmaps grandes entre activities
//TO DO: Pasar el nombre de la primera imagen por intent add extra para usar el nombre en el nuevo archivo


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class activity_image_selection2 extends AppCompatActivity {

    ImageView vista_imagen;
    String message;
    EditText editTextTextMultiLines;
    String path;
    String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection2);
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


    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

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
    }



    public void encode(View view){
        message = "#%#%#"+editTextTextMultiLines.getText().toString()+"#%#&-&%#%&&/%#";
        if (message.matches("#%#%##%#&-&%#%&&/%#")) {
            Toast.makeText(this, "¡No secret message to hide!", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] message_in_bits = message.getBytes();
        StringBuilder binary = new StringBuilder();

        for (byte b : message_in_bits){
            int val = b;
            for (int i = 0; i < 8; i++){
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }

        }


        int width = vista_imagen.getDrawable().getIntrinsicWidth();
        int height = vista_imagen.getDrawable().getIntrinsicHeight();

        int cantidad_bits_msg = binary.length();
        int cantidad_bytes_img = (width*height)*3;

        int count = 0;
        vista_imagen.invalidate();
        BitmapDrawable drawable = ((BitmapDrawable)vista_imagen.getDrawable());
        Bitmap bitmap = drawable.getBitmap();
        bitmap = bitmap.copy(bitmap.getConfig() , true);
        bitmap = getResizedBitmap(bitmap, width, height);

        //Toast.makeText(this, "Viejo tamaño:"+bitmap.getWidth()+"x"+bitmap.getHeight(), Toast.LENGTH_LONG).show();

        /*if (bitmap.getWidth() > 1000 || bitmap.getHeight() > 1000){
            bitmap = getResizedBitmap(bitmap, width/2, height/2);
            //Toast.makeText(this, "Nuevo tamaño:"+bitmap.getWidth()+"x"+bitmap.getHeight(), Toast.LENGTH_LONG).show();
            cantidad_bytes_img = cantidad_bytes_img /2;
            width = width/2;
            height = height/2;
        }*/


        if(cantidad_bytes_img < cantidad_bits_msg){
            Toast.makeText(this, "La imagen no es lo suficientemente grande para el mensaje a ocultar", Toast.LENGTH_LONG).show();
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
                /*
                String fbmp = "bitmap";

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();

                FileOutputStream stream = this.openFileOutput(fbmp, Context.MODE_PRIVATE);
                stream.write(byteArray);
                stream.close();

                Intent intent = new Intent(this, save_image.class);
                intent.putExtra("fname", fname);
                startActivity(intent);*/

                String filename = "bitmap";
                //ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,stream);
                //stream.write(bStream.toByteArray());
                stream.close();

                Intent intent = new Intent(this, save_image.class);
                intent.putExtra("fname", fname);
                startActivity(intent);

                /*ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();

                Intent intent = new Intent(this, save_image.class);
                intent.putExtra("BitmapImage", byteArray);
                intent.putExtra("fname", fname);
                //Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Viejo: "+ oldRedValue, Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();*/

            }
            catch (Exception e){
                Toast.makeText(this, "Error: Try with a smaller image", Toast.LENGTH_LONG).show();
            }


        }




    }




}
