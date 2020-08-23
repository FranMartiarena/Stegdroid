package com.example.steghide;
//DONE:Checkear la extension del archivo seleccionado para que no sea un video, gif o otro formato no admitido
//DONE:Checkear si no se ingreso un mensaje para esconder

/*
     *Encrypt an image with text, the output file will be of type .png
     *@param path        The path (folder) containing the image to modify
     *@param original   The name of the image to modify
     *@param ext1         The extension type of the image to modify (jpg, png)
     *@param stegan   The output name of the file
     *@param message  The text to hide in the image
     *@param type     integer representing either basic or advanced encoding
*/


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class activity_image_selection2 extends AppCompatActivity {

    ImageView vista_imagen;
    String message;
    EditText editTextTextMultiLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection2);
        vista_imagen = findViewById(R.id.imageView2_id);
        vista_imagen.setDrawingCacheEnabled(true);
        vista_imagen.buildDrawingCache(true);
        editTextTextMultiLines = findViewById(R.id.editTextTextMultiLineId);

        Intent intent = getIntent();
        String path= intent.getStringExtra("path");
        Uri fileUri = Uri.parse(path);
        vista_imagen.setImageURI(fileUri);
    }


    public void encode(View view){
        message = editTextTextMultiLines.getText().toString()+"#####";//gets the secret message in string type and adds delimiter
        if (message.matches("#####")) {
            Toast.makeText(this, "¡No secret message to hide!", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] message_in_bits = message.getBytes();
        StringBuilder binary = new StringBuilder();//the variable binary get the message in bytes as a string

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

        if(cantidad_bytes_img < cantidad_bits_msg){
            Toast.makeText(this, "La imagen no es lo suficientemente grande para el mensaje a ocultar", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,image_selection.class);
            startActivity(intent);
        }
        else {
            //Log.d("TAG", "Este es el tamaño de la imagen:"+String.valueOf(width)+" "+String.valueOf(height));
            //editTextTextMultiLines.setText(String.valueOf(cantidad_bytes_img));
            int count = 0;
            vista_imagen.invalidate();
            BitmapDrawable drawable = ((BitmapDrawable)vista_imagen.getDrawable());
            Bitmap bitmap = drawable.getBitmap();
            bitmap = bitmap.copy(bitmap.getConfig() , true);

            outerloop:
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){

                    int pixel = bitmap.getPixel(x,y);

                    if (count >= cantidad_bits_msg){
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
                        int newRedValue = Integer.parseInt(redValueBynaryString, 2);
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, greenValue, blueValue));//use this to change pixel values xD
                        Log.d("TAG","Cambie "+redValue+" por "+newRedValue);
                        count = count + 1;

                        if (count >= cantidad_bits_msg){

                            break outerloop;
                        }
                        //insertar bit en g
                        greenValueBynaryString = greenValueBynaryString.substring(0,greenValueBynaryString.length()-1)+binary.charAt(count);
                        int newGreenValue = Integer.parseInt(greenValueBynaryString, 2);
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, newGreenValue, blueValue));//use this to change pixel values xD
                        Log.d("TAG","Cambie "+greenValue+" por "+newGreenValue);
                        count = count + 1;

                        if (count >= cantidad_bits_msg){
                            break outerloop;
                        }
                        //insertar bit en b
                        blueValueBynaryString = blueValueBynaryString.substring(0,blueValueBynaryString.length()-1)+binary.charAt(count);
                        int newBlueValue = Integer.parseInt(blueValueBynaryString, 2);
                        bitmap.setPixel(x, y, Color.rgb(newRedValue, newGreenValue, newBlueValue));//use this to change pixel values xD
                        Log.d("TAG","Cambie "+blueValue+" por "+newBlueValue);
                        count = count + 1;

                    }


                }
            }
            vista_imagen.setImageBitmap(bitmap);
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();

        }




    }




}
