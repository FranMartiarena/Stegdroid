package com.example.steghide;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class image_selection4 extends AppCompatActivity {

    ImageView vista_imagen;
    TextView vista_texto;
    String path;
    String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection4);

        vista_imagen = findViewById(R.id.imageView3_id);
        vista_texto = findViewById(R.id.textView3);
        vista_texto.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        fname  = intent.getStringExtra("fname");
        Uri fileUri = Uri.parse(path);
        vista_imagen.setImageURI(fileUri);
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


    public String toAsciiText(String binary_message){

        String message = "";
        List<String> bytes = new ArrayList<>();

        for (int start = 0; start < binary_message.length(); start += 8) {
            bytes.add(binary_message.substring(start, Math.min(binary_message.length(), start + 8)));
        }

        for (String b : bytes){
            int charCode = Integer.parseInt(b, 2);
            String str = new Character((char)charCode).toString();
            bytes.set(bytes.indexOf(b), str);

        }

        for (String b : bytes){
            message = message + b;
        }
        return message;
    }

    public boolean checkDelimiter(String binary_message){
        if (binary_message.length() % 8 != 0 || binary_message.length() == 0){
            return false;
        }
        else{
            String message = toAsciiText(binary_message);

            if (message.length() <= 14){
                return false;
            }
            else{
                String lastFive = message.substring(message.length() -14);
                //Log.d("TAG", "Ultimos 5 caracteres: "+lastFive);
                if (lastFive.equals("#%#&-&%#%&&/%#")){
                    return true;
                }
                else{
                    return false;
                }

            }

        }

    }

    public boolean checkValidation(String binary_message){
        String message = toAsciiText(binary_message);
        String firstFive = message.substring(0,5);
        Log.d("TAG", "primeros 5 caracteres: "+firstFive);
        if (firstFive.equals("#%#%#")){
            return false;
        }
        else{
            return true;

        }

    }

    String message;
    int count;

    public String decode(View view){
        int width = vista_imagen.getDrawable().getIntrinsicWidth();
        int height = vista_imagen.getDrawable().getIntrinsicHeight();

        message = "";
        count = 0;
        vista_imagen.invalidate();
        BitmapDrawable drawable = ((BitmapDrawable)vista_imagen.getDrawable());
        Bitmap bitmap = drawable.getBitmap();
        bitmap = bitmap.copy(bitmap.getConfig() , true);
        bitmap = getResizedBitmap(bitmap, width, height);


        outerloop:
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++) {
                int pixel = bitmap.getPixel(x,y);
                if (checkDelimiter(message)){
                    //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                    String hidden = toAsciiText(message).substring(0,toAsciiText(message).length()-14);
                    hidden = hidden.substring(5,hidden.length());
                    vista_texto.setText(hidden);
                    return hidden;
                }
                else{

                    int redValue = Color.red(pixel);
                    int greenValue = Color.green(pixel);
                    int blueValue = Color.blue(pixel);

                    String redValueBynaryString = Integer.toBinaryString(redValue);
                    String greenValueBynaryString = Integer.toBinaryString(greenValue);
                    String blueValueBynaryString = Integer.toBinaryString(blueValue);

                    message = message+redValueBynaryString.charAt(redValueBynaryString.length() - 1);
                    count = count+1;
                    if (count == 5*8){
                        if (checkValidation(message)){
                            break outerloop;
                        }
                    }
                    if (checkDelimiter(message)){
                        //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                        String hidden = toAsciiText(message).substring(0,toAsciiText(message).length()-14);
                        hidden = hidden.substring(5,hidden.length());
                        vista_texto.setText(hidden);
                        return hidden;
                    }

                    message = message+greenValueBynaryString.charAt(greenValueBynaryString.length() - 1);
                    count = count+1;
                    if (count == 5*8){
                        if (checkValidation(message)){
                            break outerloop;
                        }
                    }
                    if (checkDelimiter(message)){
                        //Toast.makeText(this, "break", Toast.LENGTH_LONG).show();
                        String hidden = toAsciiText(message).substring(0,toAsciiText(message).length()-14);
                        hidden = hidden.substring(5,hidden.length());
                        vista_texto.setText(hidden);
                        return hidden;

                    }
                    message = message+blueValueBynaryString.charAt(blueValueBynaryString.length() - 1);
                    count = count+1;
                    if (count == 5*8){
                        if (checkValidation(message)){
                            break outerloop;
                        }
                    }

                }
            }
        }

        Toast.makeText(this, "No message in the Image", Toast.LENGTH_LONG).show();
        return "";
    }

    public void edit(View view){
        String message = decode(null);
        Intent intent2 = new Intent(this, activity_image_selection2.class);
        intent2.putExtra("message", message);
        //Toast.makeText(this, ""+message, Toast.LENGTH_LONG).show();
        intent2.putExtra("path", path);
        intent2.putExtra("fname", fname);
        startActivity(intent2);
        finish();
    }
}